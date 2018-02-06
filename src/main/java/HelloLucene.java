import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class HelloLucene {
    Directory index ;
    StandardAnalyzer analyzer;
    IndexWriterConfig config;
    IndexWriter writer;
    String inputFileDirectory;
    String indexDirectory;
    
    public HelloLucene(String inputFileDirectory, String indexDirectory) throws IOException {
        this.inputFileDirectory=inputFileDirectory;
        this.indexDirectory=indexDirectory;
        index = FSDirectory.open(Paths.get(indexDirectory));
        analyzer = new StandardAnalyzer();
        config = new IndexWriterConfig(analyzer);
        writer = new IndexWriter(index, config);
        readFiles();
        writer.close();
    }


    private void readFiles() throws IOException {
        File dir = new File(inputFileDirectory);
        readFiles(dir);
    }
    private void readFiles(File dir ) throws IOException {
        File[] files = dir.listFiles();
        for (File file : files) {
            String fname=file.getName();
            if(!file.isDirectory() && (fname.endsWith(".txt") || fname.endsWith(".srt"))){
                Document document = new Document();
                document.add(new TextField("content", new FileReader(file)));
                document.add(new StringField("filename", file.getName(), Field.Store.YES));
                System.out.println("Adding content: " + document.get("filename"));
                writer.addDocument(document);
            }
            else if(file.isDirectory()){
                readFiles(file);
            }
        }
    }
    public Set<String> searchIndex(String searchString) throws IOException, ParseException {
        System.out.println("Searching for '" + searchString + "'");
        Directory directory = FSDirectory.open(Paths.get(indexDirectory));
        IndexReader indexReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        QueryParser queryParser = new QueryParser("content",analyzer);
        Query query = queryParser.parse(searchString);
        int count=indexSearcher.count(query);
        TopDocs docs = indexSearcher.search(query, 10);
        ScoreDoc[] scoreDocs = docs.scoreDocs;
        int i=0;
        Set<String> fileNames= new HashSet<>();
        while(scoreDocs.length>0){
            float score=0;
            for(ScoreDoc scoreDoc: scoreDocs){
                score= scoreDoc.score> score ? scoreDoc.score: score;
                Document foundDoc=indexSearcher.doc(scoreDoc.doc);
                System.out.println(++i + ". Found looked up word in: " + foundDoc.get("filename"));
                fileNames.add(foundDoc.get("filename"));
            }
            docs=indexSearcher.searchAfter(scoreDocs[scoreDocs.length-1],query,10);
            scoreDocs = docs.scoreDocs;
        }
        return fileNames;
    }
}
