
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.NullFragmenter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.TokenSources;
import org.apache.lucene.search.similarities.BooleanSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

/**
 * Search engine for web interface with highlighter
 */
public class SearchEngine {
    // The path of index folder
    private static final String INDEX_DIR = "data\\index";

    private static StandardAnalyzer analyzer = new StandardAnalyzer();

    //    public static void main(String[] args) throws Exception {
//        SearchEngine searchEngine = new SearchEngine();
//        String s = searchEngine.searchResult("C9");
//        System.out.println(s);
//    }

    /**
     * Used to generate top 10 web pages with html format
     *
     * @author Jialuo Ding
     */
    public String searchResult(String keyWords) throws Exception {
        long startTime = System.currentTimeMillis();
        TopDocs topDocs = searcher(keyWords);
        String resultNumber = "Total Results :" + topDocs.totalHits + "<br>"
                + "-----------------------------------------------<br>";
        String result = createHighLight(topDocs, keyWords);
        long endTime = System.currentTimeMillis();
        long TotalTime = endTime - startTime;
        String runingTime = "-----------------------------------------------<br>"
                + "Running time: " + TotalTime + "<br>";
        return resultNumber + result + runingTime;
    }


    /**
     * Used to highlight the result returned by searcher
     *
     * @author Jialuo Ding
     */
    public String createHighLight(TopDocs topDocs, String keyWords)
            throws IOException, ParseException, InvalidTokenOffsetsException {
        IndexSearcher searcher = createSearcher();
        Query query = createQuery(keyWords);

        SimpleHTMLFormatter simpleHTMLFormatter =
                new SimpleHTMLFormatter("<font color='red'>", "</font>");
        Highlighter highlighter =
                new Highlighter(simpleHTMLFormatter, new QueryScorer(query));
        highlighter.setTextFragmenter(new SimpleFragmenter(1024));
        StringBuffer result = new StringBuffer();
        for (ScoreDoc sd : topDocs.scoreDocs) {
            Document doc = searcher.doc(sd.doc);
            result.append("title : " + doc.get("title") +
                    ", url : " + doc.get("url") +
                    ", anchor :");
            result.append("[");
            for (String anchor : doc.getValues("anchor")) {
                result.append(" '" + anchor + "' ");
            }
            result.append("]");
            result.append("<br>");
        }
        String text = result.toString();
        TokenStream tokenStream =
                analyzer.tokenStream("title", new StringReader(text));
        highlighter.setTextFragmenter(new NullFragmenter());
        String highLightText = highlighter.getBestFragment(tokenStream, text);

        return highLightText;
    }

    /**
     * create the IndexSearcher and set the Similarity
     *
     * @author Jialuo Ding
     */
    private static IndexSearcher createSearcher() throws IOException {
        Directory dir = FSDirectory.open(Paths.get(INDEX_DIR));

        IndexReader reader = DirectoryReader.open(dir);

        IndexSearcher searcher = new IndexSearcher(reader);

        searcher.setSimilarity(new BooleanSimilarity());
        return searcher;
    }

    /**
     * create the MultiField QueryParser
     *
     * @author Jialuo Ding
     */
    private static Query createQuery(String keyWords) throws ParseException {
        String[] fields = {"title", "content"};
        Map<String, Float> map = new HashMap<>();
        map.put(fields[0], 3f);
        map.put(fields[1], 1f);
        MultiFieldQueryParser mqp = new MultiFieldQueryParser(fields, analyzer, map);
        Query query = mqp.parse(keyWords);
        return query;
    }

    //Get the top documents
    public static TopDocs searcher(String keyWords) throws IOException, ParseException {
        IndexSearcher searcher = createSearcher();
        Query query = createQuery(keyWords);
        //search the index
        TopDocs hits = searcher.search(query, 10);
        return hits;
    }

}
