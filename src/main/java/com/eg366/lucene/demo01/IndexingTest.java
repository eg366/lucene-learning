package com.eg366.lucene.demo01;

import com.eg366.lucene.demo01.common.TestUtil;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * @author juny-zhang
 * @date 2019/3/5 10:15
 */
public class IndexingTest {

    private String[] ids = {"1", "2"};
    private String[] unIndexed = {"Netherlands", "Italy"};
    private String[] unStored = {"Amsterdam has lots of bridges", "Venice has lots of canals"};
    private String[] text = {"Amsterdam", "Venice"};
//    private String[] text = {"Amsterdam", "Amsterdam"};

    private Directory directory;

    private IndexWriter getIndexWriter() throws IOException {
        IndexWriterConfig conf = new IndexWriterConfig(new StandardAnalyzer());

        IndexWriter writer = new IndexWriter(directory, conf);
        return writer;
    }

    @Before
    public void init() throws IOException {
        directory = new RAMDirectory();

        IndexWriter writer = getIndexWriter();
        for (int i = 0; i < ids.length; i++) {
            Document doc = new Document();
            // 不分词，索引，存储可选
            doc.add(new StringField("id", ids[i], Field.Store.YES));
            // 不分词，不索引，存储
            doc.add(new StoredField("country", unIndexed[i]));
            // 分词，索引，存储可选
            doc.add(new TextField("contents", unStored[i], Field.Store.NO));
            // 分词，索引，存储可选
            doc.add(new TextField("city", text[i], Field.Store.YES));

            writer.addDocument(doc);
        }
        writer.close();
    }

    private int getHitCount(String fieldName, String searchString) throws IOException {
        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);

        Term t = new Term(fieldName, searchString);
        Query query = new TermQuery(t);
        int hitCount = TestUtil.hitCount(searcher, query);

//        searcher.close();
        return hitCount;
    }

    @Test
    public void testIndexWriter() throws IOException {
        IndexWriter writer = getIndexWriter();
        Assert.assertEquals(ids.length, writer.getDocStats().numDocs);
        writer.close();
    }

    @Test
    public void testIndexReader() throws IOException {
        IndexReader reader = DirectoryReader.open(directory);
        Assert.assertEquals(ids.length, reader.maxDoc());
        Assert.assertEquals(ids.length, reader.numDocs());
        reader.close();
    }

    @Test
    public void testDelBeforeOptimize() throws IOException {
        IndexWriter writer = getIndexWriter();
        Assert.assertEquals(ids.length, writer.getDocStats().numDocs);

        writer.deleteDocuments(new Term("id", "1"));
        writer.commit();

        Assert.assertTrue(writer.hasDeletions());

        Assert.assertEquals(2, writer.getDocStats().maxDoc);
        Assert.assertEquals(1, writer.getDocStats().numDocs);

        writer.close();
    }

    @Test
    public void testDelAfterOptimize() throws IOException {
        IndexWriter writer = getIndexWriter();
        Assert.assertEquals(ids.length, writer.getDocStats().numDocs);

        writer.deleteDocuments(new Term("id", "1"));
//        writer.optimize()
        writer.commit();

        Assert.assertTrue(writer.hasDeletions());

        Assert.assertEquals(2, writer.getDocStats().maxDoc);
        Assert.assertEquals(1, writer.getDocStats().numDocs);

        writer.close();
    }

    @Test
    public void testUpdate() throws IOException {
        Assert.assertEquals(1, getHitCount("city", "amsterdam"));

        IndexWriter writer = getIndexWriter();

        Document doc = new Document();
        doc.add(new StringField("id", "1", Field.Store.YES));
        doc.add(new StoredField("country", "Netherlands"));
        doc.add(new TextField("contents", "Den Haag has a lot of museums", Field.Store.NO));
        doc.add(new TextField("city", "Den Haag", Field.Store.YES));

        writer.updateDocument(new Term("id", "1"), doc);
        writer.close();

        Assert.assertEquals(0, getHitCount("city", "amsterdam"));
        Assert.assertEquals(1, getHitCount("city", "haag"));
    }
}
