package com.github.sh0nk.solr.sudachi;

import com.worksap.nlp.sudachi.Morpheme;
import org.apache.lucene.util.Attribute;

/**
 * Attribute for {@link Morpheme#surface()}
 */
public interface SurfaceFormAttribute extends Attribute {
    String getSurface();

    void setMorpheme(Morpheme morpheme);
}
