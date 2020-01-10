package OuputFiles.DocumentFile;

import TermsAndDocs.Terms.DocumentDateTerm;
import TermsAndDocs.Terms.Term;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * this class is responsible for writing and writing the docs files
 */
public class DocumentFileHandler {

    public DocumentFileHandler() { }

    /**
     * this method gets the details of a document
     * and writes to the relevant file
     * @param docNo
     * @param numOfUniqueTerms
     * @param mostCommonTermCounter
     * @param mostCommmonTerm
     * @param documentDateTerm
     * @param header
     * @param docSize
     */
    public void writeDocumentDataToFile(String documentDataFilePath, String docNo, int numOfUniqueTerms, int mostCommonTermCounter, Term mostCommmonTerm,
                                        DocumentDateTerm documentDateTerm, String header, int docSize, HashSet<Term> entities) {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(docNo);
        stringBuilder.append(";");
        stringBuilder.append(docSize);
        stringBuilder.append(";");
        stringBuilder.append(numOfUniqueTerms);
        stringBuilder.append(";");
        stringBuilder.append(mostCommmonTerm.getData());
        stringBuilder.append(";");
        stringBuilder.append(mostCommonTermCounter);
        stringBuilder.append(";");
        stringBuilder.append(documentDateTerm.getData());
        stringBuilder.append(";");
        stringBuilder.append(header);
        stringBuilder.append(";");
        stringBuilder.append("ENTITIES:");
        int count = 0;
        for (Term entity: entities) {
            count++;
            stringBuilder.append(entity.getData());
            stringBuilder.append(";");
        }
        if(count > 0)
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        stringBuilder.append("\n");


        try {
            FileWriter fw = new FileWriter(documentDataFilePath, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(stringBuilder.toString());
            bw.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * this method gets String docNo and returns all of the doc's properties from our the docs file via string line
     * @param docNo
     * @return String line of data
     */
    public String searchDocInFiles(String docNo, ArrayList<String> docsPath)
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
