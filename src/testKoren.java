import HandleSearch.Searcher;

public class testKoren {
    public static void main(String[] args) {
        Searcher searcher = new Searcher(null,true);
        System.out.println(searcher.getSemanticallyCloseWords());
    }
}
