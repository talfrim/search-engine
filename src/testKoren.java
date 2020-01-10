import HandleSearch.Searcher;
import IndexerAndDictionary.Dictionary;
import IndexerAndDictionary.Indexer;
import OuputFiles.DictionaryFileHandler;
import OuputFiles.DocumentFile.DocumentFileHandler;
import TermsAndDocs.Terms.Term;

import java.util.ArrayList;

public class testKoren {
    public static void main(String[] args) {
        Searcher searcher = new Searcher(null,true);
        System.out.println(searcher.getSemanticallyCloseWords());

    }
}
