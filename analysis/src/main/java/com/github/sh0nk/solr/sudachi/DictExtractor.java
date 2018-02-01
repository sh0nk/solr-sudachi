package com.github.sh0nk.solr.sudachi;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class DictExtractor {
    private final String fileName;

    public DictExtractor(String fileName) {
        this.fileName = fileName;
    }

    public String extractTo(String targetDir) throws IOException {
        InputStream in = getClass().getClassLoader().getResourceAsStream(fileName);

        File newFile = new File(targetDir, fileName);
        if (newFile.exists()) {
            return null;
        }

        try {
            FileUtils.copyToFile(in, newFile);
            return newFile.getAbsolutePath();
        } catch (IOException e) {
            throw new IOException(String.format(
                    "Dictionary file %s cannot be extracted from jar file to Solr config dir.", fileName), e);
        }
    }
}
