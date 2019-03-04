package com.eg366.lucene.demo01;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * 通过索引进行搜索
 *
 * @author eg366
 * @date 2019/3/4 16:16
 */
public class Searcher {

    public static void search(String indexDir, String q) throws IOException, ParseException {
        Directory dir = FSDirectory.open(Paths.get(indexDir));
        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher is = new IndexSearcher(reader);

        QueryParser parser = new QueryParser("contents", new StandardAnalyzer());
        Query query = parser.parse(q);

        long start = System.currentTimeMillis();
        TopDocs hits = is.search(query, 10);
        long end = System.currentTimeMillis();

        System.out.println("Found " + hits.totalHits + " document(s) (int " + (end - start) + " millisenconds) that matched query '" + q + "':");

        for (ScoreDoc scoreDoc : hits.scoreDocs) {
            Document doc = is.doc(scoreDoc.doc);
            System.out.println(doc.get("fullPath"));
        }
    }

    public static void main(String[] args) throws IOException, ParseException {
        String indexDir = "E:\\workspaces\\eg366_learn\\lucene-learning\\index-files\\demo01";
        String q = "patent";

        search(indexDir, q);
    }
}
