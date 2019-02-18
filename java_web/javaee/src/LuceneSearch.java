import com.sun.xml.internal.fastinfoset.util.StringArray;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.search.similarities.BooleanSimilarity;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.LMJelinekMercerSimilarity;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Search engine for JavaFX interface without highlighter
 */
public class LuceneSearch {
    //directory contains the lucene indexes
    private static final String INDEX_DIR = "data\\index";

    private StringBuffer result;

    public String indexSearch(String query) throws Exception {
        long startTime = System.currentTimeMillis();
        //Create lucene searcher. It search over a single IndexReader.
        IndexSearcher searcher = createSearcher();

        //Search indexed contents using search term
        TopDocs foundDocs = searchInContent(query, searcher);

        result = new StringBuffer();
        //Total found documents
        result.append("Total Results : " + foundDocs.totalHits + "\n");

        //Let's return the information of the web pages which have searched term
        for (ScoreDoc sd : foundDocs.scoreDocs) {
            Document d = searcher.doc(sd.doc);

            result.append("title : " + d.get("title") +
                    ", url : " + d.get("url") +
                    ", anchor :");
            result.append("[");
            for (String anchor : d.getValues("anchor")) {
                result.append(" \"" + anchor + "\" ");
            }
            result.append("]");
            result.append("\n");
        }
        long endTime = System.currentTimeMillis();
        long TotalTime = endTime - startTime;
        result.append("Running time: " + TotalTime + "\n");
        System.out.println(result);
        return result.toString();
    }

    private static TopDocs searchInContent(String textToFind, IndexSearcher searcher) throws Exception {
        //Create search query
        //QueryParser qp = new QueryParser("content", new StandardAnalyzer());

        String[] fields = {"title", "content"};
        Map<String, Float> map = new HashMap<>();
        map.put(fields[0], 3f);
        map.put(fields[1], 1f);
        MultiFieldQueryParser mqp = new MultiFieldQueryParser(fields, new StandardAnalyzer(),map);
        Query query = mqp.parse(textToFind);

        //search the index
        TopDocs hits = searcher.search(query, 10);
        return hits;
    }

    private static IndexSearcher createSearcher() throws IOException {
        Directory dir = FSDirectory.open(Paths.get(INDEX_DIR));

        IndexReader reader = DirectoryReader.open(dir);

        //Index searcher
        IndexSearcher searcher = new IndexSearcher(reader);

        //searcher.setSimilarity(new BM25Similarity(1.2F,0.9F));
        searcher.setSimilarity(new BooleanSimilarity());
        //searcher.setSimilarity(new LMJelinekMercerSimilarity(0.8f));
        return searcher;
    }
}
