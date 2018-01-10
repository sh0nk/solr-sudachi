package com.github.sh0nk.solr.sudachi;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.BaseTokenStreamTestCase;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.KeywordTokenizer;
import org.apache.solr.core.SolrResourceLoader;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TestSudachiSurfaceFormFilterFactory extends BaseTokenStreamTestCase {
    private Analyzer analyzer;

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

    @Override
    public void setUp() throws Exception {
        super.setUp();
        analyzer = new Analyzer() {
            @Override
            protected TokenStreamComponents createComponents(String s) {
                try {
                    Tokenizer tokenizer = createTokenizer(Collections.emptyMap());
                    return new TokenStreamComponents(tokenizer,
                            new SudachiSurfaceFormFilterFactory(Collections.emptyMap()).create(tokenizer));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        checkOneTerm(analyzer, "人間", "人間");
    }

    @Override
    public void tearDown() throws Exception {
        analyzer.close();
        super.tearDown();
    }

    @Test
    public void testBasics() throws IOException {
        assertAnalyzesTo(analyzer, "吾輩は猫である。",
                new String[] {"吾輩", "は", "猫", "で", "ある"});
    }

    @Test
    public void testEnglish() throws IOException {
        assertAnalyzesTo(analyzer, "This is a pen.",
                new String[] {"This", "is", "a", "pen"});
    }

    @Test
    public void testUnicode() throws IOException {
        assertAnalyzesTo(analyzer, "\u1f77\u1ff2\u1f96\u1f50\u1fec  yirhp",
                new String[] {"\u1f77", "\u1ff2\u1f96\u1f50\u1fec", "yirhp"});
    }

    @Test
    public void testEmptyTerm() throws IOException {
        analyzer = new Analyzer() {
            @Override
            protected TokenStreamComponents createComponents(String s) {
                Tokenizer tokenizer = new KeywordTokenizer();
                return new TokenStreamComponents(tokenizer,
                        new SudachiSurfaceFormFilterFactory(Collections.emptyMap()).create(tokenizer));
            }
        };
        checkOneTerm(analyzer, "", "");
        analyzer.close();
    }

}