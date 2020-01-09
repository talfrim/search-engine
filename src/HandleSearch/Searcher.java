package HandleSearch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class Searcher {
    private ArrayList<String> docsPath;
    private static Pattern splitByDotCom= Pattern.compile("[\\;]");

    public Searcher(ArrayList<String> docsPath)
    {
        this.docsPath = docsPath;
    }

    private String searchDoc(String docNo)
    {
        BufferedReader [] readers = new BufferedReader[docsPath.size()];
        for (int i = 0; i < readers.length; i++) {
            try {
                readers[i] = new BufferedReader(new FileReader(docsPath.get(i)));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        boolean found = false;
        int startIndex = 0;
        while(!found) {

        }
        return "";
    }
}
