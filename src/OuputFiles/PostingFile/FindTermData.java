package OuputFiles.PostingFile;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * this class is responsible for finding the line of properties in given posting file
 * helps to searcher
 */
public class FindTermData {
    private Pattern bracket = Pattern.compile("[\\(]");

    public FindTermData() { }


    /**
     * @return the line of the given entity in the posting file
     */
    public String findLine(String path, String entityVal) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            Pattern pattern = Pattern.compile(entityVal);
            String ansLine = reader.readLine();
            while (ansLine != null) {
                String [] splitter = bracket.split(ansLine);
                if(pattern.matcher(splitter[0]).matches()){
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
}
