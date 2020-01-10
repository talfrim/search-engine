package HandleSearch;

import HandleParse.DataConfiguration.Stemmer;
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
    private double k1 = 2;

    /**
     * k1 parameter for bm25
     */
    private double b = 0.75;

    /**
     * This is the number of documents in the corpus
     */
    public final int numOfDocs = 472531;

    /**
     * This is the avg doc length
     */
    public final int avgDocLength = 200; //TODO




    /**
     * @param isSemantic field mentioning if we should take into account the result of the semantic connection
     * @param isSemanticOnline mentioning if in case of ranking with semantics we will use the online option
     */
    public Ranker(boolean isSemantic, boolean isSemanticOnline) {
        this.isSemantic = isSemantic;
        this.isSemanticOnline=isSemanticOnline;
    }

    /**
     * computes the final ranking of the document, by calculating BM25 and TfIdf ranking of the
     * original query, and if {@code isSemantic} is true with the semantic close words also.
     * @return ranking
     */
    public double rankDocument(
            ArrayList<String> queryWords, ArrayList<Integer> queryWordsTfs, ArrayList<Integer> queryWordsDfs,
             ArrayList<String> similarWords, ArrayList<Integer> similarWordsTfs, ArrayList<Integer> similarWordsDfs,
              int maxTf,  ArrayList<String> docHeaderStrings)
    {
        double output;
        if (!isSemantic) {
            output=0;
        }
        return 0;
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
    private int isTermInHeader(String term, ArrayList<String> docHeaderStrings) {
        ArrayList<String> headerAfterStemming=new ArrayList<>();
        for (int i=0; i<docHeaderStrings.size(); i++) {
            String lower = docHeaderStrings.get(0).toLowerCase();
            String stemmed = stemmStr(lower);
            headerAfterStemming.add(stemmed);
        }
        return headerAfterStemming.contains(term) ? 1 : 0 ;
    }

    /**
     * @param df
     * @return idf of given df, based on {@code numOfDocs} field
     */
    private double getIdf(int df) {
        return (Math.log(numOfDocs/df)) / Math.log(2);
    }

    /**
     * using stemmer class to stem given string
     * @param toStem
     * @return stemmed string
     * @see Stemmer
     */
    private String stemmStr(String toStem) {
        Stemmer stemm = new Stemmer();
        stemm.add(toStem.toCharArray(), toStem.length());
        stemm.stem();
        return (stemm.toString());
    }



}
