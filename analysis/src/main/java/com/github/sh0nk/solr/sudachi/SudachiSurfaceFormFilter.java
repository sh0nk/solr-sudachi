package com.github.sh0nk.solr.sudachi;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;

/**
 * Replaces the SolrSudachiTokenizer's default normalized form term text
 * with the {@link SurfaceFormAttribute} (untouched input split token)
 */
public final class SudachiSurfaceFormFilter extends TokenFilter {
    private final CharTermAttribute termAtt;
    private final SurfaceFormAttribute surfaceAtt;

    public SudachiSurfaceFormFilter(TokenStream input) {
        super(input);
        termAtt = addAttribute(CharTermAttribute.class);
        surfaceAtt = addAttribute(SurfaceFormAttribute.class);
    }

    @Override
    public boolean incrementToken() throws IOException {
        if (input.incrementToken()) {
            if (surfaceAtt != null && surfaceAtt.getSurface() != null && !surfaceAtt.getSurface().isEmpty()) {
                termAtt.setEmpty().append(surfaceAtt.getSurface());
            }
            return true;
        }
        return false;
    }
}
