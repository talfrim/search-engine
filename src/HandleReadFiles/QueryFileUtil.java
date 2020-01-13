package HandleReadFiles;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

/**
 * This class is responsible for reading queries from queries file
 */
public class QueryFileUtil {

    /**
     *
     * @param path of query file
     * @return map of query id and query content
     */
    public static HashMap<String, String> extractQueries(String path) {
        HashMap<String, String> idTextMap = new HashMap<>();
        String num = "";
        String title = "";
        String desc = "";
        try {
            File queryFile = new File(path);
            if (!queryFile.exists())
                System.out.println("no query file");
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
                if (reader.hasNextLine()) {
                    //idTextMap.put(num, title + " " + desc);
                    //idTextMap.put(num, title + " " + ((title.charAt(0)>='A'&&title.charAt(0)<='Z') ? title.toLowerCase() : "") ); //to lower case only for titles starting with capital
                    //add lower case to capitals
                    String[] strings = title.split(" ");
                    for (int i = 0; i < strings.length; i++) {
                        if (partOfEntity(i, strings))
                            title = title + " " + strings[i].toLowerCase();
                    }
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

    private static boolean partOfEntity(int i, String[] strings) {
        if (!(strings[i].charAt(0) >= 'A' && strings[i].charAt(0) <= 'Z'))
            return false;
        if (i != 0)
            if (strings[i - 1].charAt(0) >= 'A' && strings[i - 1].charAt(0) <= 'Z')
                return true;
        if (i != strings.length - 1)
            if (strings[i + 1].charAt(0) >= 'A' && strings[i + 1].charAt(0) <= 'Z')
                return true;
        return false;
    }
}
