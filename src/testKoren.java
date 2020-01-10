import HandleSearch.Searcher;
import IndexerAndDictionary.Dictionary;
import IndexerAndDictionary.Indexer;
import OuputFiles.DictionaryFileHandler;
import OuputFiles.DocumentFile.DocumentFileHandler;
import TermsAndDocs.Terms.Term;

import java.util.ArrayList;

public class testKoren {
    public static void main(String[] args) {
        //testing searcher
        ArrayList<String> docsPath = new ArrayList<>();
        String output = "d:\\documents\\users\\korenish\\Documents\\korenish\\OUTPUT";
        for (int i = 0; i < 7; i++) {
            String docFilePath =  output+ "\\" + "stemOur" + "\\DocsFiles\\docFile" + i;
            docsPath.add(docFilePath);
        }
        DictionaryFileHandler dfh = new DictionaryFileHandler(new Dictionary());
        Indexer.dictionary = dfh.readFromFile(output, true);

        long start = System.currentTimeMillis();
        //
        // Searcher searcher = new Searcher(docsPath);
//
        //
        // ArrayList<Term> arr1 = searcher.FiveTopEntities("FBIS4-16257");
        //
        // ArrayList<Term> arr2 = searcher.FiveTopEntities("FT911-4805");
        //
        // ArrayList<Term> arr3 = searcher.FiveTopEntities("FT923-9562");
        //
        // ArrayList<Term> arr4 = searcher.FiveTopEntities("FT941-14268");
        //
        // ArrayList<Term> arr5 = searcher.FiveTopEntities("LA030889-0002");
        //
        // ArrayList<Term> arr6 = searcher.FiveTopEntities("LA122490-0123");

        long end = System.currentTimeMillis();
        System.out.println( "time for additional searcher -->" + (end-start));
    }
}
