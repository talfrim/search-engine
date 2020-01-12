import HandleReadFiles.QueryFileUtil;

import java.util.HashMap;

public class talMain {

    public static void main(String[] args) {
        HashMap<String, String> ans = QueryFileUtil.extractQueries("C:\\BGU\\queries.txt");
        System.out.println();
    }
}
