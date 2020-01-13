package OuputFiles.PostingFile;

import TermsAndDocs.Terms.Term;
import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;

/**
 * this class is responsible for finding the line of properties in given posting file
 * helps to searcher
 */
public class FindTermsData {
    private Pattern bracket = Pattern.compile("[\\(]");

    public FindTermsData() { }


    /**
     * @return the line of the given entity in the posting file
     */
    public String findLine(String path, String entityVal) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String ansLine = reader.readLine();
            while (ansLine != null) {
                String [] splitter = bracket.split(ansLine);
                if(splitter[0].equals(entityVal)){
                    reader.close();
                    return ansLine;
                }
                ansLine = reader.readLine();
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param key 
     * @param value
     * @return
     */
    public ArrayList<Pair<Term, String>> searchAllTermsInPostFile(String key, ArrayList<Pair<Term, String>> value) {
    }
}
