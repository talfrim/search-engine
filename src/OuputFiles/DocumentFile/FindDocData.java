package OuputFiles.DocumentFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * implements Runnable class - for the purpose of running simultaneously through different files in order to find doc
 * using it's ID (docNo)
 */
public class FindDocData implements Runnable {
    private BufferedReader reader;
    private String docNo;
    private String docData;

    protected FindDocData(BufferedReader reader, String docNo) {
        this.reader = reader;
        this.docNo = docNo;
        this.docData = null;
    }

    protected String getDocData() {
        return docData;
    }

    /**
     * searching for doc in file
     * saving it's line of properties
     */
    @Override
    public void run() {
        String line = null;
        try {
            line = reader.readLine();
            while (line != null) {
                String check = "";
                int i = 0;
                char ch = line.charAt(i);
                while (ch != ';'){
                    check += ch;
                    i++;
                    ch = line.charAt(i);
                }
                if(check.equals(docNo)) {
                    this.docData = line;
                    reader.close();
                    return;
                }
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
