package com.github.sh0nk.solr.sudachi;

import com.worksap.nlp.sudachi.Morpheme;
import org.apache.lucene.util.AttributeImpl;
import org.apache.lucene.util.AttributeReflector;

public class SurfaceFormAttributeImpl extends AttributeImpl implements SurfaceFormAttribute {

    private Morpheme morpheme;

    @Override
    public String getSurface() {
        return morpheme == null ? null : morpheme.surface();
    }

    @Override
    public void setMorpheme(Morpheme morpheme) {
        this.morpheme = morpheme;
    }

    @Override
    public void clear() {
        morpheme = null;
    }

    @Override
    public void reflectWith(AttributeReflector attributeReflector) {
        attributeReflector.reflect(SurfaceFormAttribute.class, "surfaceForm", getSurface());
    }

    @Override
    public void copyTo(AttributeImpl attribute) {
        SurfaceFormAttribute at = (SurfaceFormAttribute) attribute;
        at.setMorpheme(morpheme);
    }
}
