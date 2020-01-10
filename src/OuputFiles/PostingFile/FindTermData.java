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
                if(pattern.matcher(ansLine).find()){
                    reader.close();
                    return ansLine;
                }
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
