package HandleSearch;

import TermsAndDocs.Docs.Document;
import com.medallia.word2vec.Searcher;
import com.medallia.word2vec.Word2VecModel;
import datamuse.DatamuseQuery;
import datamuse.JSONParse;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for ranking documents with respect to a query
 */
public class Ranker {

    /**
     * Field mentioning if we should take into account the result of the semantic connection
     * between the query and the document. Probably will be decided by the user.
     * If it is false, only BM25 will be taken into account.
     */
    private boolean isSemantic;

    /**
     * Field mentioning if in case of ranking with semantics we will use the online option
     *
     */
    private boolean isSemanticOnline;
    /**
     * ArrayList of the words from the query
     */
    private ArrayList<String> queryWords;

    /**
     * ArrayList that will hold the semantically close words to the query
     * (only if isSemantic is true)
     */
    private ArrayList<String> semanticallyCloseWords;

    /**
     * The percentage given to the ranking functions on the original query words when calculating the rank in case of taking into account
     * the semantically close words.
     * It is slitted by two for the BM25 an the TFIDF ranking methods.
     * 1 minus this value is the percentage given to the semantic similarities words.
     */
    private final double weightOfOriginalQuery = 0.65;


    /**
     * @param queryTerms ArrayList of the words from the query
     * @param isSemantic field mentioning if we should take into account the result of the semantic connection
     */
    public Ranker(ArrayList<String> queryTerms, boolean isSemantic) {
        this.queryWords = queryTerms;
        this.isSemantic = isSemantic;
        semanticallyCloseWords = isSemantic ? getSemanticallyCloseWordsOnline() : null;
    }

    /**
     * computes the final ranking of the document, by calculating BM25 ranking  of the
     * original querry, and if {@code isSemantic} is true with the semantic close words also.
     * @param document
     * @return
     */
    public double rankDocument(Document document) {
        double output;
        if (isSemantic) {
            output = weightOfOriginalQuery/2 * getBM25Rank(queryWords,document)
                    + weightOfOriginalQuery/2 * getTfIdfRank(queryWords,document)
                    + (1-weightOfOriginalQuery)/2 * getBM25Rank(getSemanticallyCloseWords(isSemanticOnline),document)
                    + (1-weightOfOriginalQuery)/2 * getTfIdfRank(getSemanticallyCloseWords(isSemanticOnline),document);
        }
        else {
            output = 0.5*getBM25Rank(queryWords,document) + 0.5*getTfIdfRank(queryWords,document);
        }
        return output;
    }

    private ArrayList<String> getSemanticallyCloseWords(boolean isSemanticOnline) {
        return isSemanticOnline ? getSemanticallyCloseWordsOnline() : getSemanticlyCloseWordsOffline();
    }


    private double getTfIdfRank(ArrayList<String> words, Document document) {
        return 0;
    }

    /**
     * @param words ArrayList of words
     * @param document not null
     * @return the rank of the similarity of the given words and document by BM25
     */
    private double getBM25Rank(ArrayList<String> words, Document document) {
        return 0;
    }

    /**
     * Using datamuse API to get a list of similar words
     * @apiNote requires internet connection!
     * @return ArrayList of similar words
     */
    private ArrayList<String> getSemanticallyCloseWordsOnline() {
        ArrayList<String> output = new ArrayList<>();
        DatamuseQuery datamuseQuery = new DatamuseQuery();
        JSONParse jSONParse = new JSONParse();

        for (String word:queryWords) {
            String initCloseWords = datamuseQuery.findSimilar(word);
            String[] parsedCloseWords = jSONParse.parseWords(initCloseWords);
            addArrayToList(parsedCloseWords,output);
        }
        return output;
    }

    /**
     * Using Word2vecJava to get a list of similar words
     * *Using pre-trained model
     * *If unknown wors is found, we simply ignore it
     * @return ArrayList of similar words
     */
    private ArrayList<String> getSemanticlyCloseWordsOffline() {
        ArrayList<String> output = new ArrayList<>();
        try {
            Word2VecModel model = Word2VecModel.fromTextFile(new File("\\data\\model\\word2vec.c.output.model.txt"));
            com.medallia.word2vec.Searcher semanticSearcher = model.forSearch();
            int numOfResults = 10;
            List<com.medallia.word2vec.Searcher.Match> matches = semanticSearcher.getMatches(">>wordToSearch<<",numOfResults);

            for (com.medallia.word2vec.Searcher.Match match : matches)
            {
                output.add(match.match());
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Searcher.UnknownWordException e) {
            e.printStackTrace();
        }
        return output;
    }


    /**
     * function that adds all the strings of a given string array to the given list
     */
    private void addArrayToList(String[] parsedCloseWords, List<String> list) {
        for (String word:parsedCloseWords
             ) {
            list.add(word);
        }
    }


}
