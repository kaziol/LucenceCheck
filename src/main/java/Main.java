import org.apache.lucene.queryparser.classic.ParseException;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        try {
            HelloLucene helloLucene = new HelloLucene("D:\\Pobrane", "D:\\indexDir");
            helloLucene.searchIndex("dzień dobry");
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }
}
