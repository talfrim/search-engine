import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class QueryFileUtil {

    public static HashMap<String, String> extractQueries(String path) {
        HashMap<String, String> idTextMap = new HashMap<>();
        String num="";
        String title="";
        String desc = "";
        try {
            File queryFile = new File(path);
            Scanner reader = new Scanner(queryFile);
            String line = reader.nextLine();
            while (reader.hasNextLine()) {
                if (line.startsWith("<num>")) {
                    num = line.split("<num> Number: ")[0];
                }
                if (line.startsWith("<title>")) {
                    title = line.split("<title> ")[0];
                }
                while (!line.startsWith("<desc> Description:")) {
                    line = reader.nextLine();
                }
                while (!line.startsWith("<narr> Narrative:")) {
                    desc = desc + " " + line;
                    line = reader.nextLine();
                }
                while (!line.startsWith("<num>")) {
                    line = reader.nextLine();
                }
                idTextMap.put(num,title + " " + desc);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return idTextMap;
    }
}
