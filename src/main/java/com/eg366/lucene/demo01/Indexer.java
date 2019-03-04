package com.eg366.lucene.demo01;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * 建立索引
 *
 * @author eg366
 * @date 2019/3/4 15:19
 */
public class Indexer {

    private IndexWriter writer;

    public Indexer(String indexDir) throws IOException {
        Directory dir = FSDirectory.open(Paths.get(indexDir));
//        writer = new IndexWriter(dir, new IndexWriterConfig());
        writer = new IndexWriter(dir, new IndexWriterConfig(new StandardAnalyzer()));
        writer.deleteAll();
    }

    public void close() throws IOException {
        writer.close();
    }

    public int index(String dataDir, FileFilter filter) throws IOException {
        File[] files = new File(dataDir).listFiles();

        for (File f : files) {
            if (!f.isDirectory() && !f.isHidden() && f.exists() && f.canRead() && (filter == null || filter.accept(f))) {
                indexFile(f);
            }
        }

//        return writer.numDocs();
        return writer.numRamDocs();
    }

    private static class TextFilesFilter implements FileFilter {
        @Override
        public boolean accept(File pathname) {
            return pathname.getName().toLowerCase().endsWith(".txt");
        }
    }

    protected Document getDocument(File f) throws IOException {
        Document doc = new Document();
        doc.add(new TextField("contents", new FileReader(f)));
        doc.add(new TextField("fileName", f.getName(), Field.Store.YES));
        doc.add(new TextField("fullPath", f.getCanonicalPath(), Field.Store.YES));

        return doc;
    }

    private void indexFile(File f) throws IOException {
        System.out.println("Indexing " + f.getCanonicalPath());
        Document doc = getDocument(f);
        writer.addDocument(doc);
    }

    public static void main(String[] args) throws IOException {

        // 存放lucene索引的路径
        String indexDir = "E:\\workspaces\\eg366_learn\\lucene-learning\\index-files\\demo01";
        // 被索引文件的存放路径
        String dataDir = "E:\\workspaces\\eg366_learn\\lucene-learning\\data";

        long start = System.currentTimeMillis();
        Indexer indexer = new Indexer(indexDir);
        int numIndexed;
        try {
            numIndexed = indexer.index(dataDir, new TextFilesFilter());
        } finally {
            indexer.close();
        }
        long end = System.currentTimeMillis();

        System.out.println("Indexing " + numIndexed + " files took " + (end - start) + "milliseconds");
    }

}
