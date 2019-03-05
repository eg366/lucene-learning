package com.eg366.lucene.demo01.common;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;

import java.io.IOException;

/**
 * @author juny-zhang
 * @date 2019/3/5 10:57
 */
public class TestUtil {

    public static int hitCount(IndexSearcher search, Query query) throws IOException {
        // 参数1：指的是scoreDocs的个数
        return (int) search.search(query, 1).totalHits;
    }
}
