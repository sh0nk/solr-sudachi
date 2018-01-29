package com.github.sh0nk.solr.sudachi;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.BaseTokenStreamTestCase;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.solr.core.SolrResourceLoader;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TestSolrSudachiTokenizerFactory extends BaseTokenStreamTestCase {

    private Tokenizer createTokenizer(Map<String, String> args) throws IOException {
        String dictFile = SolrSudachiTokenizerFactory.class.getResource("/system_full.dic").getPath();
        String settingsFile = SolrSudachiTokenizerFactory.class.getResource("/solr_sudachi.json").getPath();
        Map<String, String> map = new HashMap<>(args);
        map.put("systemDictPath", dictFile);
        map.put("settingsPath", settingsFile);
        SolrSudachiTokenizerFactory factory = new SolrSudachiTokenizerFactory(map);
        factory.inform(new SolrResourceLoader());
        return factory.create(newAttributeFactory());
    }

    @Test
    public void testDefault() throws IOException {
        Tokenizer tokenizer = createTokenizer(new HashMap<>());
        tokenizer.setReader(new StringReader("吾輩は猫である。"));
        assertTokenStreamContents(tokenizer,
                new String[] {"我が輩", "は", "猫", "だ", "有る"}
        );
    }

    @Test
    public void testPunctuation() throws IOException {
        Tokenizer tokenizer = createTokenizer(new HashMap<String, String>() {{
            put("discardPunctuation", "false");
        }});
        tokenizer.setReader(new StringReader("吾輩は猫である。"));
        assertTokenStreamContents(tokenizer,
                new String[] {"我が輩", "は", "猫", "だ", "有る", "。"}
        );
    }

    @Test
    public void testExtended() throws IOException {
        Tokenizer tokenizer = createTokenizer(new HashMap<String, String>() {{
            put("mode", "EXTENDED");
        }});
        tokenizer.setReader(new StringReader("アブラカタブラ"));
        assertTokenStreamContents(tokenizer,
                new String[] {"アブラカタブラ", "ア", "ブ", "ラ", "カ", "タ", "ブ", "ラ"},
                new int[] {1, 0, 1, 1, 1, 1, 1, 1}
        );
    }

}