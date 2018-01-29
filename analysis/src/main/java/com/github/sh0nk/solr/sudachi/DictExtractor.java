package com.github.sh0nk.solr.sudachi;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class DictExtractor {
    private static final String[] DICTS = new String[] {"/system_core.dic", "/system_full.dic"};

    public static void extract(String configDir) throws IOException {
        for (String dict : DICTS) {
            URL dictUrl = DictExtractor.class.getResource(dict);
            File newFile = new File(configDir, dict);
            if (newFile.exists()) {
                continue;
            }

            try {
                FileUtils.copyURLToFile(dictUrl, newFile);
            } catch (IOException e) {
                throw new IOException("Dictionary file cannot be extracted from jar file to Solr config dir.", e);
            }
        }
    }
}
