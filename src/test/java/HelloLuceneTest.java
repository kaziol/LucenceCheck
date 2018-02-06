import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class HelloLuceneTest {
    HelloLucene helloLucene;
    public static String indexDir="D:\\indexDir";
    String inputDir = "D:\\Pobrane";

    HelloLuceneTest() throws IOException {
        helloLucene =  new HelloLucene(inputDir,indexDir);
    }

    @BeforeClass
    public static void cleanIndexDir(){
        System.out.println("beforeclass");

        File dir = new File("D:\\indexDir");
        deleteFiles(dir);
    }

    public static void deleteFiles(File file){
        if(!file.isDirectory() || file.listFiles().length==0){
            System.out.println("DELETE");
            file.delete();
        }
        else {
            for(File f: file.listFiles()){
                deleteFiles(f);
            }
        }
    }
    

    @org.junit.jupiter.api.Test
    void searchIndex() throws IOException, ParseException {
        //created files with and without content to verify the process
        Set<String> result = helloLucene.searchIndex("dzień dobry");
        assertTrue(result.contains("testcontains.txt"));
        assertFalse(result.contains("testnotcontaining.txt"));


    }

}