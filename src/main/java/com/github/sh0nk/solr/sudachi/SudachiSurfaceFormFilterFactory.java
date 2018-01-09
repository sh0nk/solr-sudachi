package com.github.sh0nk.solr.sudachi;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.util.TokenFilterFactory;

import java.util.Map;

/**
 * Factory for {@link SudachiSurfaceFormFilter}.
 * <pre class="prettyprint">
 * &lt;fieldType name="text_ja" class="solr.TextField"&gt;
 *   &lt;analyzer&gt;
 *     &lt;tokenizer class="com.github.sh0nk.solr.sudachi.SolrSudachiTokenizerFactory"
 *       systemDictPath="sudachi/system_core.dic"
 *     /&gt;
 *     &lt;filter class="com.github.sh0nk.solr.sudachi.SudachiSurfaceFormFilterFactory"/&gt;
 *   &lt;/analyzer&gt;
 * &lt;/fieldType&gt;
 * </pre>
 */
public class SudachiSurfaceFormFilterFactory extends TokenFilterFactory {

    public SudachiSurfaceFormFilterFactory(Map<String, String> args) {
        super(args);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        return new SudachiSurfaceFormFilter(tokenStream);
    }
}
