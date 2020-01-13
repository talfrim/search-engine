package HandleReadFiles;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class QueryFileUtil {

    public static HashMap<String, String> extractQueries(String path) {
        HashMap<String, String> idTextMap = new HashMap<>();
        String num = "";
        String title = "";
        String desc = "";
        try {
            File queryFile = new File(path);
            if (!queryFile.exists())
                System.out.println("no");
            Scanner reader = new Scanner(queryFile);
            String line = reader.nextLine();
            while (reader.hasNextLine()) {
                while (reader.hasNextLine() && !line.startsWith("<num>")) {
                    line = reader.nextLine();
                }
                if (reader.hasNextLine()) {
                    String[] numArr = line.split("<num> Number: ");
                    num = numArr[1];
                }
                while (reader.hasNextLine() && !line.startsWith("<title>")) {
                    line = reader.nextLine();
                }
                if (reader.hasNextLine()) {
                    title = line.split("<title> ")[1];
                }
                while (reader.hasNextLine() && !line.startsWith("<desc> Description:")) {
                    line = reader.nextLine();
                }
                if (reader.hasNextLine())
                    line = reader.nextLine();
                while (reader.hasNextLine() && !line.startsWith("<narr> Narrative:")) {
                    desc = desc + " " + line;
                    line = reader.nextLine();
                }
                if(reader.hasNextLine()) {
                    //idTextMap.put(num, title + " " + desc);
                    //idTextMap.put(num, title + " " + ((title.charAt(0)>='A'&&title.charAt(0)<='Z') ? title.toLowerCase() : "") ); //to lower case only for titles starting with capital
                    idTextMap.put(num, title);
                    desc = "";
                }
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return idTextMap;
    }
}
