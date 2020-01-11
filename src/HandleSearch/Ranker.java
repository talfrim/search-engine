package HandleSearch;

import HandleSearch.DocDataHolders.DocRankData;
import TermsAndDocs.Terms.Term;

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
     * The percentage given to the ranking functions on the original query words when calculating the rank in case of taking into account
     * the semantically close words.
     * It is slitted by two for the BM25 an the TFIDF ranking methods.
     * 1 minus this value is the percentage given to the semantic similarities words.
     */
    private final double weightOfOriginalQuery = 0.65;

    /**
     * The percentage given to the bm25 functions when scoring with the different parameters
     * The reat goes to cos similarity, is the word in the header, etc...
     */
    private final double weightOfBM25 = 0.60;


    /**
     * k1 parameter for bm25
     */
    private double k1 = 2;

    /**
     * b parameter for bm25
     */
    private double b = 0.75;

    /**
     * This is the number of documents in the corpus
     */
    public final int numOfDocs = 472531;

    /**
     * This is the avg doc length
     */
    public final int avgDocLength = 250; //TODO

    /**
     * @param isSemantic field mentioning if we should take into account the result of the semantic connection
     */
    public Ranker(boolean isSemantic) {
        this.isSemantic = isSemantic;
    }

    /**
     * computes the final ranking of the document, by calculating BM25 and TfIdf ranking of the
     * original query, and if {@code isSemantic} is true with the semantic close words also.
     * @return ranking
     */
    public double rankDocument(
            DocRankData docRankData)
    {
        double output;
        if (!isSemantic) {
            output = weightOfBM25*getBM25Rank(docRankData.getQueryWordsTfs(),docRankData.getQueryWordsDfs(),docRankData.getLengthOfDoc())
                    + 0.05*getTermsInHeaderScore(docRankData.getQueryWords(),docRankData.getDocHeaderStrings())
                    +(1-0.05-weightOfBM25)*getCosSimRank(docRankData.getQueryWordsTfs(),docRankData.getQueryWordsDfs());
        }
        else //with semantics
        output = weightOfOriginalQuery* (weightOfBM25*getBM25Rank(docRankData.getQueryWordsTfs(),docRankData.getQueryWordsDfs(),docRankData.getLengthOfDoc())
                + 0.05*getTermsInHeaderScore(docRankData.getQueryWords(),docRankData.getDocHeaderStrings())
                +(1-0.05-weightOfBM25)*getCosSimRank(docRankData.getQueryWordsTfs(),docRankData.getQueryWordsDfs()))
                +(1-weightOfOriginalQuery)* (weightOfBM25*getBM25Rank(docRankData.getSimilarWordsTfs(),docRankData.getSimilarWordsDfs(),docRankData.getLengthOfDoc())
                + 0.05*getTermsInHeaderScore(docRankData.getSimilarWords(),docRankData.getDocHeaderStrings())
                +(1-0.05-weightOfBM25)*getCosSimRank(docRankData.getSimilarWordsTfs(),docRankData.getSimilarWordsDfs()));
        return output;
    }

    private double getCosSimRank(ArrayList<Integer> tfs, ArrayList<Integer> dfs) {
        double[] queryVector = new double[tfs.size()];
        for (int i=0; i<queryVector.length; i++) {
            queryVector[i] = 1;
        }

        double[] docVector = new double[tfs.size()];
        for (int i=0; i<docVector.length; i++) {
            queryVector[i] = tfs.get(i) * getIdf(dfs.get(i));
        }

        return cosineSimilarity(queryVector,docVector);
    }

    private double cosineSimilarity(double[] vectorA, double[] vectorB) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += Math.pow(vectorA[i], 2);
            normB += Math.pow(vectorB[i], 2);
        }
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    private double getBM25Rank(ArrayList<Integer> tfs, ArrayList<Integer> dfs, int lengthOfDoc) {
        double output=0;
        for (int i=0; i< tfs.size(); i++) {
            output += getBM25ForOneTerm(tfs.get(i),dfs.get(i),lengthOfDoc);
        }
        return output;
    }

    private double getBM25ForOneTerm(Integer tf, Integer df, int lengthOfDoc) {
        return getIdf(df) * ((tf)*(k1+1)) / (tf+k1*(1-b+b*(lengthOfDoc/avgDocLength)));
    }

    /**
     * @param term
     * @param docHeaderStrings
     * @return 1 if header contains un-stemmed version of the term supllied, else false
     */
    private int isTermInHeader(Term term, ArrayList<Term> docHeaderStrings) {
        return docHeaderStrings.contains(term) ? 1 : 0 ;
    }

    /**
     * returns the percentage of the words from the query that are in the documents header
     * @param terms
     * @param docHeaderStrings
     * @return the percentage of the words from the query that are in the documents header
     */
    private double getTermsInHeaderScore(ArrayList<Term> terms, ArrayList<Term> docHeaderStrings) {
        int counter =0;
        for (Term term: terms) {
            counter += isTermInHeader(term,docHeaderStrings); //this will give us the number of terms from the list which are in the header
        }
        return ((double) counter)/((double)terms.size());
    }

    /**
     * @param df
     * @return idf of given df, based on {@code numOfDocs} field
     */
    private double getIdf(int df) {
        return (Math.log(numOfDocs/df)) / Math.log(2);
    }





}
