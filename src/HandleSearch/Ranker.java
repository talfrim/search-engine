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
     * The percentage given to the ranking functions on the original query words when calculating the rank in case of taking into account
     * the semantically close words.
     * It is slitted by two for the BM25 an the TFIDF ranking methods.
     * 1 minus this value is the percentage given to the semantic similarities words.
     */
    private final double weightOfOriginalQuery = 0.65;

    /**
     * k1 parameter for bm25
     */
    private double k1;

    /**
     * k1 parameter for bm25
     */
    private double b;




    /**
     * @param isSemantic field mentioning if we should take into account the result of the semantic connection
     */
    public Ranker(boolean isSemantic, boolean isSemanticOnline) {
        this.isSemantic = isSemantic;
        this.isSemanticOnline=isSemanticOnline;
    }

    /**
     * computes the final ranking of the document, by calculating BM25 and tfIdf ranking  of the
     * original query, and if {@code isSemantic} is true with the semantic close words also.
     * @return ranking
     */
    public double rankDocument( ArrayList<Integer> queryTfs, ArrayList<Integer> similarWordsTfs, int maxTf, String docHeader) {
        double output;
        if (isSemantic) {
            output = weightOfOriginalQuery/2 * getBM25Rank(queryTfs,maxTf,docHeader)
                    + weightOfOriginalQuery/2 * getTfIdfRank(queryTfs,maxTf,docHeader)
                    + (1-weightOfOriginalQuery)/2 * getBM25Rank(similarWordsTfs,maxTf,docHeader)
                    + (1-weightOfOriginalQuery)/2 * getTfIdfRank(similarWordsTfs,maxTf,docHeader);
        }
        else {
        }
        return 0;
    }


    private double getTfIdfRank(ArrayList<Integer> queryTfs, int maxTf, String docHeader) {
        return 0;
    }

    private double getBM25Rank(ArrayList<Integer> queryTfs, int maxTf, String docHeader) {
        return 0;
    }








}
