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
        String docData = searchDoc(docNo);

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
        }
        return null;
    }

    private ArrayList<Double> calculateScores(ArrayList<Term> realEntities, String docNo) {
        for (Term entity : realEntities) {
            int entitySize = entity.getData().length();
            int appearancesInDoc = getNumOfAppearancesInDoc(docNo, dictionary, entity);
            int appearncesInCorpus = dictionary.get(entity).getTotalCount();
        }
        return null;
    }

    private int getNumOfAppearancesInDoc(String docNo, Dictionary dictionary, Term entity) {
        //TODO
        String path = dictionary.get(entity).getPointer().getFileStr();
        FindTermData finder = new FindTermData();
        String entitryLine = finder.findLine(path, entity.getData());
        return 0;
    }


    /**
     * this method gets String docNo and returns all of the doc's properties from our the docs file via string line
     * @param docNo
     * @return String line of data
     */
    private String searchDoc(String docNo)
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
}
