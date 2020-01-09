import HandleSearch.Searcher;
import IndexerAndDictionary.Dictionary;
import OuputFiles.DictionaryFileHandler;
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
        Dictionary dic = dfh.readFromFile(output, true);

        long start = System.currentTimeMillis();
        Searcher searcher = new Searcher(docsPath);
        String docData1 = searcher.searchDocInFile("FBIS4-16257");
        String docData2 = searcher.searchDocInFile("FT911-4805");
        String docData3 = searcher.searchDocInFile("FT923-9562");
        String docData4 = searcher.searchDocInFile("FT941-14268");
        String docData5 = searcher.searchDocInFile("LA030889-0002");
        String docData6 = searcher.searchDocInFile("LA122490-0123");

        System.out.println();

        ArrayList<Term> arr1 = searcher.FiveTopEntities("FBIS4-16257");
        ArrayList<Term> arr2 = searcher.FiveTopEntities("FT911-4805");
        ArrayList<Term> arr3 = searcher.FiveTopEntities("FT923-9562");
        ArrayList<Term> arr4 = searcher.FiveTopEntities("FT941-14268");
        ArrayList<Term> arr5 = searcher.FiveTopEntities("LA030889-0002");
        ArrayList<Term> arr6 = searcher.FiveTopEntities("LA122490-0123");

        long end = System.currentTimeMillis();
        System.out.println( "time for additional searcher -->" + (end-start));
    }
}
