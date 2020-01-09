package HandleSearch;

import TermsAndDocs.Docs.Document;

import javax.print.Doc;
import java.util.ArrayList;

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
     * ArrayList of the words from the query
     */
    private ArrayList<String> queryWords;

    /**
     * ArrayList that will hold the semantically close words to the query
     * (only if isSemantic is true)
     */
    private ArrayList<String> semanticallyCloseWords;

    private final double weightOfQuery = 0.65;


    /**
     * @param queryTerms ArrayList of the words from the query
     * @param isSemantic field mentioning if we should take into account the result of the semantic connection
     */
    public Ranker(ArrayList<String> queryTerms, boolean isSemantic) {
        this.queryWords = queryTerms;
        this.isSemantic = isSemantic;
        semanticallyCloseWords = isSemantic ? getSemanticlyCloseWords() : null;
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
            output = weightOfQuery * getBM25Rank(queryWords,document) +
                    (1-weightOfQuery)*getBM25Rank(semanticallyCloseWords,document);
        }
        else {
            output = getBM25Rank(queryWords,document);
        }
        return output;
    }

    /**
     * @param document
     * @return the rank of the similarity of the given words and document by BM25
     */
    private double getBM25Rank(ArrayList<String> words, Document document) {
        return 0;
    }

    /**
     * Using datamuse API to get a list of similar words
     * @return array of similar words
     */
    private ArrayList<String> getSemanticlyCloseWords() {
        return null;
    }


}
