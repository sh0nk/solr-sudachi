package com.github.sh0nk.solr.sudachi;

import com.google.common.base.Charsets;
import com.worksap.nlp.sudachi.Settings;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.util.ResourceLoader;
import org.apache.lucene.analysis.util.ResourceLoaderAware;
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.util.AttributeFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Factory for {@link com.github.sh0nk.solr.sudachi.SolrSudachiTokenizerFactory}.
 * <pre class="prettyprint">
 * &lt;fieldType name="text_ja" class="solr.TextField"&gt;
 *   &lt;analyzer&gt;
 *     &lt;tokenizer class="com.github.sh0nk.solr.sudachi.SolrSudachiTokenizerFactory"
 *       mode="NORMAL"
 *       systemDictDir="sudachi"
 *       settingsPath="solr_sudachi.json"
 *       discardPunctuation="true"
 *     /&gt;
 *   &lt;/analyzer&gt;
 * &lt;/fieldType&gt;
 * </pre>
 *
 * <p>If <i>settingsPath</i> is not provided, Sudachi's default settings are used.
 *
 * <p>All the Sudachi system files such as char.def or rewrite.def which are specified on
 * <i>settingsPath</i> are the relative path from the <i>systemDictDir</i>. For the above example,
 * they should be put in the same "sudachi" directory.
 */
public class SolrSudachiTokenizerFactory extends TokenizerFactory implements ResourceLoaderAware {

    private static final String MODE = "mode";
    private static final String MODE_SEARCH = "search";
    private static final String MODE_NORMAL = "normal";
    private static final String MODE_EXTENDED = "extended";
    private static final String DISCARD_PUNCTUATION = "discardPunctuation";
    private static final String SYSTEM_DICT_DIR = "systemDictDir";
    private static final String SETTINGS_PATH = "settingsPath";

    private final SolrSudachiTokenizer.Mode mode;
    private final boolean discardPunctuation;
    private final String systemDictDir;
    private final String settingsPath;

    private String fixedSystemDictDir = null;
    private String settingsContent = null;

    public SolrSudachiTokenizerFactory(Map<String, String> args) {
        super(args);

        this.mode = getMode(get(args, MODE));
        this.discardPunctuation = getBoolean(args, DISCARD_PUNCTUATION, true);
        this.systemDictDir = get(args, SYSTEM_DICT_DIR);
        this.settingsPath = get(args, SETTINGS_PATH, "solr_sudachi.json");
    }

    private SolrSudachiTokenizer.Mode getMode(String input) {
        if (input != null) {
            if (MODE_SEARCH.equalsIgnoreCase(input)) {
                return SolrSudachiTokenizer.Mode.SEARCH;
            } else if (MODE_NORMAL.equalsIgnoreCase(input)) {
                return SolrSudachiTokenizer.Mode.NORMAL;
            } else if (MODE_EXTENDED.equalsIgnoreCase(input)) {
                return SolrSudachiTokenizer.Mode.EXTENDED;
            }
        }
        return SolrSudachiTokenizer.DEFAULT_MODE;
    }

    @Override
    public void inform(ResourceLoader resourceLoader) throws IOException {
        ResourceLoaderHelper resourceLoaderHelper = new ResourceLoaderHelper(resourceLoader);
        if (systemDictDir != null) {
            fixedSystemDictDir = resourceLoaderHelper.getResourcePath(systemDictDir).toString();
        } else {
            fixedSystemDictDir = resourceLoaderHelper.getConfigDir();
        }

        ensureSettings(resourceLoaderHelper);
        ensureSystemDict();
    }

    private void ensureSettings(ResourceLoaderHelper resourceLoaderHelper) throws IOException {
        if (fileExists(settingsPath)) {
            settingsContent = getSettingsFileContent(Paths.get(settingsPath));
        } else if (fileExists(resourceLoaderHelper.getConfigDir(), settingsPath)) {
            settingsContent = getSettingsFileContent(Paths.get(resourceLoaderHelper.getConfigDir(), settingsPath));
        } else {
            settingsContent = IOUtils.toString(getClass().getClassLoader().getResourceAsStream(settingsPath),
                    Charsets.UTF_8);
        }
    }

    private void ensureSystemDict() throws IOException {
        String systemDictPath = Settings.parseSettings(fixedSystemDictDir, settingsContent).getString("systemDict");

        // 1. abs path on json
        // 2. file with base(fixedSystemDictDir) dir
        // (1.2. is how JapaneseDictionary works)
        if (fileExists(systemDictPath) || fileExists(fixedSystemDictDir, systemDictPath)) {
            return;
        }

        // 3. try to extract with the name to base dir, once extraction is done, from the next time 2. should work
        // if it's not matched, thrown
        new DictExtractor(systemDictPath).extractTo(fixedSystemDictDir);
    }

    private static boolean fileExists(String file, String... more) {
        return StringUtils.isNotEmpty(file) && Files.exists(Paths.get(file, more));
    }

    private static String getSettingsFileContent(Path fixedSettingsPath) {
        if (fixedSettingsPath == null) {
            return null;
        }

        try {
            return new String(Files.readAllBytes(fixedSettingsPath), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot read Sudachi settings file from " + fixedSettingsPath);
        }
    }

    @Override
    public Tokenizer create(AttributeFactory attributeFactory) {
        try {
            return new SolrSudachiTokenizer(discardPunctuation, mode, fixedSystemDictDir, settingsContent);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to create SolrSudachiTokenizer", e);
        }
    }
}
