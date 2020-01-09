package HandleSearch;

import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.regex.Pattern;

/**
 * this class is responsible for the return of all the relevant docs for a given query
 * this class uses the class Ranker for that purpose
 */
public class Searcher {
    private ArrayList<String> docsPath;

    public Searcher(ArrayList<String> docsPath)
    {
        this.docsPath = docsPath;
    }


    /**
     * this method gets String docNo and returns all of the doc's properties from our the docs file via string line
     * @param docNo
     * @return String line of data
     */
    private String searchDoc(String docNo)
    {
        int numOfFiles = 6;
        ArrayList<FindDocData> answers = new ArrayList<>();
        ExecutorService pool = Executors.newFixedThreadPool(numOfFiles);
        for (int i = 0; i < numOfFiles; i++) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(docsPath.get(i)));
                FindDocData findDocData = new FindDocData(reader, docNo);
                answers.add(findDocData);
                pool.execute(findDocData);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        try {
            pool.shutdown();
            pool.awaitTermination(200000, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for(FindDocData finder : answers) {
            if(finder.getDocData() != null) {
                return finder.getDocData();
            }
        }
        System.out.println("couldn't find the doc in our files");
        return null;
    }

}
