package HandleSearch;

import IndexerAndDictionary.Dictionary;
import IndexerAndDictionary.Indexer;
import OuputFiles.DocumentFile.FindDocData;
import OuputFiles.PostingFile.FindTermData;
import TermsAndDocs.Terms.Term;
import TermsAndDocs.Terms.TermBuilder;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static IndexerAndDictionary.Indexer.dictionary;

/**
 * this class is responsible for the return of all the relevant docs for a given query
 * this class uses the class Ranker for that purpose
 */
public class Searcher {
    private ArrayList<String> docsPath;
    private static Pattern splitByEntities= Pattern.compile("[E][N][T][I][T][I][E][S][:]");
    private static Pattern splitByDotCom= Pattern.compile("[\\;]");

    public Searcher(ArrayList<String> docsPath)
    {
        this.docsPath = docsPath;
    }


    /**
     * @param docNo
     * @return ArrayList of the five (if exists) most dominating entities in the doc
     */
    public ArrayList<Term> FiveTopEntities(String docNo){
        Dictionary dictionary = Indexer.dictionary;

        //finding the doc's properties
        String docData = searchDocInFile(docNo);

        //gets all of the entities in a doc
        String[] splitter = splitByEntities.split(docData);
        String strEntities = splitter[1];
        String[] mayEntities = splitByDotCom.split(strEntities);
        TermBuilder builder = new TermBuilder();
        ArrayList<Term> realEntities = new ArrayList<>();
        //keeping only the right entities
        for (int i = 0; i < mayEntities.length; i++) {
            Term t = builder.buildTerm("EntityTerm", mayEntities[i]);
            if(dictionary.contains(t))
                realEntities.add(t);
        }
        if(realEntities.size() <= 5){
            return realEntities;
        }
        else{
            ArrayList<Double> scores = calculateScores(realEntities, docNo);
            ArrayList<Term> topFive = new ArrayList<>();
            for (int i = 0; i <= 5; i++) {
                topFive.add(extractBiggestScore(scores, realEntities));
            }
            return topFive;
        }
    }

    /**
     * this method gets String docNo and returns all of the doc's properties from our the docs file via string line
     * @param docNo
     * @return String line of data
     */
    public String searchDocInFile(String docNo)
    {
        int numOfFiles = 6;
        ArrayList<FindDocData> answers = new ArrayList<>();
        ExecutorService pool = Executors.newFixedThreadPool(numOfFiles);

        //using threads to search through different files
        for (int i = 0; i < numOfFiles; i++) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(docsPath.get(i)));
                FindDocData findDocData = new FindDocData(reader, docNo);
                answers.add(findDocData);
                pool.execute(findDocData);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        try {
            pool.shutdown();
            pool.awaitTermination(200000, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //returning the line of doc's data
        for(FindDocData finder : answers) {
            if(finder.getDocData() != null) {
                return finder.getDocData();
            }
        }
        return null;
    }

    /**
     * this method is responsible for returning the scores for all the entity terms in a document
     * score is calculated by : ((size of term) * (number of appearances in the doc)) / log(appearances in corpus)
     * @param realEntities
     * @param docNo
     * @return array list of scores for each entity
     */
    private ArrayList<Double> calculateScores(ArrayList<Term> realEntities, String docNo) {
        Pattern docNoSplit = Pattern.compile(docNo + ";");
        ArrayList<Double> scores = new ArrayList<>();
        for (int i = 0; i < realEntities.size(); i++) {

            int entitySize = realEntities.get(i).getData().length();
            int appearancesInDoc = getNumOfAppearancesInDoc(dictionary, realEntities.get(i), docNoSplit);
            int appearancesInCorpus = dictionary.get(realEntities.get(i)).getTotalCount();

            //calculating by formula
            double score = entitySize * appearancesInDoc;
            score = score / Math.log(appearancesInCorpus);
            scores.add(score);

        }
        return scores;
    }


    /**
     * this method is responsible for returning the number of appearances given term made in a given doc
     * by getting the answer from the posting file
     * @param dictionary
     * @param entity
     * @param docNoSplit
     * @return
     */
    private int getNumOfAppearancesInDoc(Dictionary dictionary, Term entity, Pattern docNoSplit) {
        String path = dictionary.get(entity).getPointer().getFileStr();
        //finding the line of properties of the doc in posting file
        FindTermData finder = new FindTermData();
        String entitryLine = finder.findLine(path, entity.getData());

        //getting from the line the number of appearances the term made inside the doc
        String[] splitter = docNoSplit.split(entitryLine);
        String contains = splitter[1];
        String strApperances = "";
        char ch = contains.charAt(0);
        while (ch != ')'){
            strApperances += ch;
        }
        int countApperances = Integer.parseInt(strApperances);

        return countApperances;
    }


    /**
     * this method is responsible for extracting and returning
     * the biggest score of term from the scores and realEntities arrays
     * @param scores
     * @param realEntities
     * @return term with the biggest score
     */
    private Term extractBiggestScore(ArrayList<Double> scores, ArrayList<Term> realEntities) {
        double maxScore = 0;
        int index = 0;
        for (int i = 0; i < scores.size(); i++) {
            if(scores.get(i) > maxScore){
                maxScore = scores.get(i);
                index = i;
            }
        }
        scores.remove(index);
        return realEntities.remove(index);
    }

}
