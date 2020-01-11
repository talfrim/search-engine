package HandleSearch;

import HandleParse.DataConfiguration.Stemmer;
import HandleSearch.DocDataHolder.DocNecessaryData;
import IndexerAndDictionary.CountAndPointerDicValue;
import IndexerAndDictionary.Dictionary;
import IndexerAndDictionary.Indexer;
import OuputFiles.DocumentFile.DocumentFileHandler;
import OuputFiles.PostingFile.FindTermData;
import TermsAndDocs.Pairs.TermDocPair;
import TermsAndDocs.Terms.Term;
import TermsAndDocs.Terms.TermBuilder;
import com.medallia.word2vec.Word2VecModel;
import datamuse.DatamuseQuery;
import datamuse.JSONParse;
import javafx.util.Pair;

import java.util.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * this class is responsible for the return of all the relevant docs for a given query
 * this class uses the class Ranker for that purpose
 */
public class Searcher {
    private ArrayList<String> docsPath;
    private static Pattern dfPattern = Pattern.compile("[d][f][\\{]");
    private static Pattern escape = Pattern.compile("[ ]");
    private static Pattern splitByEntities = Pattern.compile("[E][N][T][I][T][I][E][S][:]");
    private static Pattern splitByDotCom = Pattern.compile("[\\;]");
    private static Pattern splitByBracket = Pattern.compile("[\\(]");
    private HashSet<String> stopWords;

    /**
     * Field mentioning if we should take into account the result of the semantic connection
     * between the query and the document. Probably will be decided by the user.
     * If it is false, only original query words will be taken into account.
     */
    private boolean isSemantic;
    private boolean isStemm;
    private Dictionary dictionary;

    public Searcher(ArrayList<String> docsPath, boolean isSemantic, boolean isStemm, Dictionary dictionary, HashSet<String> stopWords) {
        this.docsPath = docsPath;
        this.isSemantic = isSemantic;
        this.isStemm = isStemm;
        this.dictionary = dictionary;
        this.stopWords = stopWords;
    }

    /**
     * this method is responsible for the functionality of the class
     * it receives a word and search for the documents which contains this term
     * then we calculate for each of the relevant docs it's rank
     * we are returning at most 50 relevant docs by order
     * @param query
     * @return
     */
    public ArrayList<String> search(ArrayList<String> query){
        ArrayList<String> semanticallyCloseWords = new ArrayList<>();
        if(isSemantic)
            semanticallyCloseWords = getSemanticallyCloseWords(query);
        //changing the words of the query and semantically close words so they would fit to the dictionary && posting file terms
        ArrayList<Term> queryTerms = parseQueryAndHeader(query);
        ArrayList<Term> semanticTerms = parseQueryAndHeader(semanticallyCloseWords);

        //finding the posting data for each term
        ArrayList<Pair<Term, String>> queryTermPostingData = getPostData(queryTerms);
        ArrayList<Pair<Term, String>> semanticTermPostingData = getPostData(semanticTerms);

        //TODO object DocNecessaryData
        //TODO extracting from hashes all the docNos
        //finding the doc file data for each term
        ArrayList<Pair<String, DocNecessaryData>> docsFileData = getDocsData(queryTermPostingData, semanticTermPostingData);


        return null;
    }

    /**
     * this method is filling every
     * @param queryTermPostingData
     * @param semanticTermPostingData
     * @return
     */
    private ArrayList<Pair<String, DocNecessaryData>> getDocsData(ArrayList<Pair<Term, String>> queryTermPostingData,
                                                          ArrayList<Pair<Term, String>> semanticTermPostingData) {
        ArrayList<Pair<String, DocNecessaryData>> docsDataHash = new ArrayList<>();
        HashMap<String, DocNecessaryData> hashChecker = new HashMap<>();
        for (int p = 0; p < queryTermPostingData.size(); p++) {
            Pair<Term, String> entry = queryTermPostingData.get(p);
            String termPostingData = entry.getValue();
            //finding term DF
            String[] splitterDf = dfPattern.split(termPostingData);
            String containsDF = splitterDf[1];
            int i = 0;
            char ch = containsDF.charAt(i);
            String dfStr = "";
            while (ch != '}'){
                dfStr += ch;
                i++;
                ch = containsDF.charAt(i);
            }
            int termDf = Integer.parseInt(dfStr);

            //extracting docNo && tf
            String containsNotDf = splitterDf[0];
            String[] splitterTfDocNo = splitByBracket.split(containsNotDf);
            for(int k = 1; k < splitterTfDocNo.length; k++){
                String docNoTfCurrent = splitterTfDocNo[k];
                String[] splitter = splitByDotCom.split(docNoTfCurrent);
                String currentDocNo = splitter[0];
                if(hashChecker.get(currentDocNo) == null){

                }
                else {
                }
            }

        }

        return docsDataHash;
    }

    /**
     * @param terms
     * @return hash of all the terms as keys and thier data in post file (String) as value
     */
    private ArrayList<Pair<Term, String>> getPostData(ArrayList<Term> terms) {
        ArrayList<Pair<Term, String>> termPostingData = new ArrayList<>();
        FindTermData finder = new FindTermData();
        for (Term currentTerm : terms){
            CountAndPointerDicValue dicVal = dictionary.get(currentTerm);
            if(dicVal != null){
                String path = dicVal.getPointer().getFileStr();
                String termLine = finder.findLine(path, currentTerm.getData());
                termPostingData.add(new Pair<>(currentTerm, termLine));
            }
        }
        return termPostingData;
    }


    /**
     * parsing the query's words so we'll get hit in the dictionary
     * @param query
     */
    private ArrayList<Term> parseQueryAndHeader(ArrayList<String> query) {
        SearcherParse sp = new SearcherParse(this.stopWords, this.isStemm);
        HashMap<Term, TermDocPair> hash= sp.parseForSearcher(query);
        ArrayList<Term> queryTerms = new ArrayList<>();
        for (Map.Entry<Term, TermDocPair> entry : hash.entrySet()) {
            queryTerms.add(entry.getKey());
        }
        return queryTerms;
    }

    /**
     * Using online\offline methods to get similar words
     * (determined by {@code isSemanticOnline} field
     * @return ArrayList of similar words
     */
    public ArrayList<String> getSemanticallyCloseWords(ArrayList<String> query) {
        try {
            return  getSemanticallyCloseWordsOnline(query);
        }
        catch (Exception e)
        {
            return getSemanticallyCloseWordsOffline(query);
        }
    }


    /**
     * Using Word2vecJava to get a list of similar words
     * *Using pre-trained model
     * *If unknown wors is found, we simply ignore it
     * @return ArrayList of similar words
     * @param query
     */
    private ArrayList<String> getSemanticallyCloseWordsOffline(ArrayList<String> query) {
        ArrayList<String> output = new ArrayList<>();
        try {
            Word2VecModel model = Word2VecModel.fromTextFile(new File("data\\model\\word2vec.c.output.model.txt"));
            com.medallia.word2vec.Searcher semanticSearcher = model.forSearch();
            int numOfResults = 11;
            for (int i = 0; i < query.size(); i++) {
                List<com.medallia.word2vec.Searcher.Match> matches = semanticSearcher.getMatches(query.get(i).toLowerCase(), numOfResults);
                for (com.medallia.word2vec.Searcher.Match match : matches) {
                    if (!match.match().equals(query.get(i)))
                        output.add(match.match());
                }
            }
        } catch (Exception e) {
        }
        finally {
            return output;
        }
    }

    /**
     * Using datamuse API to get a list of similar words
     *
     * @return ArrayList of similar words
     * @apiNote requires internet connection!
     * @param query
     */
    private ArrayList<String> getSemanticallyCloseWordsOnline(ArrayList<String> query) {
        ArrayList<String> output = new ArrayList<>();
        DatamuseQuery datamuseQuery = new DatamuseQuery();
        JSONParse jSONParse = new JSONParse();
        int i = 1;
        for (String word : query) {
            String initCloseWords = datamuseQuery.findSimilar(word);
            String[] parsedCloseWords = jSONParse.parseWords(initCloseWords);
            addArrayToList(parsedCloseWords, output);
            if(i == 10)
                break;
            i++;
        }
        return output;

    }

    /**
     * @param docNo
     * @return {@code ArrayList) of the five (if exists) most dominating entities in the doc
     */
    public ArrayList<Term> fiveTopEntities(String docNo) {
        Dictionary dictionary = Indexer.dictionary;

        //finding the doc's properties
        DocumentFileHandler dfh = new DocumentFileHandler();
        String docData = dfh.searchDocInFiles(docNo, this.docsPath);

        //gets all of the entities in a doc
        String[] splitter = splitByEntities.split(docData);
        if (splitter.length == 1)
            return new ArrayList<>();
        String strEntities = splitter[1];
        String[] mayEntities = splitByDotCom.split(strEntities);
        TermBuilder builder = new TermBuilder();
        ArrayList<Term> realEntities = new ArrayList<>();
        //keeping only the right entities
        for (int i = 0; i < mayEntities.length; i++) {
            Term t = builder.buildTerm("EntityTerm", mayEntities[i]);
            if (dictionary.contains(t))
                realEntities.add(t);
        }
        if (realEntities.size() <= 5) {
            return realEntities;
        } else {
            ArrayList<Double> scores = calculateScores(realEntities, docNo);
            ArrayList<Term> topFive = new ArrayList<>();
            for (int i = 0; i <= 4; i++) {
                topFive.add(extractBiggestScore(scores, realEntities));
            }
            return topFive;
        }
    }


    /**
     * this method is responsible for returning the scores for all the entity terms in a document
     * score is calculated by : ((size of term (num of words)) * (number of appearances in the doc)) / log(appearances in corpus)
     * @param realEntities
     * @param docNo
     * @return array list of scores for each entity
     */
    private ArrayList<Double> calculateScores(ArrayList<Term> realEntities, String docNo) {
        Pattern docNoSplit = Pattern.compile(docNo + ";");
        ArrayList<Double> scores = new ArrayList<>();
        for (int i = 0; i < realEntities.size(); i++) {
            Term currentEntity = realEntities.get(i);
            String[] strEntitySize = escape.split(currentEntity.getData());

            int entitySize = strEntitySize.length;
            int appearancesInDoc = getNumOfAppearancesInDoc(dictionary, currentEntity, docNoSplit);
            int appearancesInCorpus = dictionary.get(currentEntity).getTotalCount();

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
        int i = 0;
        char ch = contains.charAt(0);
        while (ch != ')') {
            strApperances += ch;
            i++;
            ch = contains.charAt(i);
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
            if (scores.get(i) > maxScore) {
                maxScore = scores.get(i);
                index = i;
            }
        }
        scores.remove(index);
        return realEntities.remove(index);
    }


    /**
     * function that adds all the strings of a given string array to the given list
     */
    private void addArrayToList(String[] parsedCloseWords, List<String> list) {
        for (String word : parsedCloseWords
        ) {
            list.add(word);
        }
    }

    /**
     *
     * @param toStem
     * @return
     */
    private String stemmStr(String toStem) {
        Stemmer stemm = new Stemmer();
        stemm.add(toStem.toCharArray(), toStem.length());
        stemm.stem();
        return (stemm.toString());
    }

}
