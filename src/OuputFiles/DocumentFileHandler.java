package OuputFiles;

import TermsAndDocs.Terms.DocumentDateTerm;
import TermsAndDocs.Terms.Term;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * this class is responsible for writing and writing the docs files
 */
public class DocumentFileHandler {
    private String documentDataFilePath;

    public DocumentFileHandler(String documentDataFilePath) {
        this.documentDataFilePath = documentDataFilePath;
    }

    /**
     * this method gets the details of a document
     * and writes to the relevant file
     * @param docNo
     * @param numOfUniqueTerms
     * @param mostCommonTermCounter
     * @param mostCommmonTerm
     * @param documentDateTerm
     * @param header
     */
    public void writeDocumentDataToFile(String docNo, int numOfUniqueTerms, int mostCommonTermCounter, Term mostCommmonTerm,
                                        DocumentDateTerm documentDateTerm, String header, HashSet<Term> entities) {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(docNo);
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
        for (Term entity: entities) {
            stringBuilder.append(entity.getData());
            stringBuilder.append(";");
        }
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
}
