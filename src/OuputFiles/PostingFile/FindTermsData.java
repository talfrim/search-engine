package OuputFiles.PostingFile;

import TermsAndDocs.Pairs.TermDocPair;
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
     * @param path
     * @param requestList
     * @return
     */
    public ArrayList<Pair<TermDocPair, String>> searchAllTermsInPostFile(String path, ArrayList<Pair<TermDocPair, String>> requestList) {
        try {
            ArrayList<Pair<TermDocPair, String>> termAndLine = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String ansLine = reader.readLine();
            int i = 0;
            int requestSize = requestList.size();
            while (ansLine != null && i < requestSize) {
                String [] splitter = bracket.split(ansLine);
                if(splitter[0].equals(requestList.get(i).getValue())){
                    termAndLine.add(new Pair<>(requestList.get(i).getKey(), ansLine));
                    i++;
                }
                ansLine = reader.readLine();
            }
            reader.close();
            return termAndLine;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
