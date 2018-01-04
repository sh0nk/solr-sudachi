# solr-sudachi

A Japanese morphological analyzer Sudachi as a Solr plugin.

This plugin is based on [elasticsearch-sudachi](https://github.com/WorksApplications/elasticsearch-sudachi)
which includes the common lucene Tokenizer and TokenFilters.

Only the difference from the original elasticsearch-sudachi is that
`Tokenizer` outputs the original form of the input characters,
unlike that elasticsearch-sudachi does return normalized form.

## Install

1. `mvn package` to generate `solr-sudachi-1.0.0-SNAPSHOT-jar-with-dependencies.jar` in `target/`.
2. Put `solr-sudachi-1.0.0-SNAPSHOT-jar-with-dependencies.jar` on `${SOLR_HOME}/lib` directory.
3. Download the latest Sudachi dictionary snapshot (sudachi-0.1.1-*-dictionary-core.zip) 
from [here](https://oss.sonatype.org/content/repositories/snapshots/com/worksap/nlp/sudachi/0.1.1-SNAPSHOT/).
4. Uncompress the dictionary zip and put the extracted `system_core.dic` into `${SOLR_HOME}` dir.
For example if you have a core named `core1`, then put the file in the same directory as `core.properties`,
or `solrconfig.xml`.
5. Configure schema.xml or managed-schema with the following setting.

## Examples of schema.xml

```xml
<fieldType name="text_ja" class="solr.TextField">
 <analyzer>
   <!-- Whatever Char Filters -->
   ...
   
   <tokenizer class="com.github.sh0nk.solr.sudachi.SolrSudachiTokenizerFactory"
     mode="NORMAL"
     systemDictPath="system_core.dic"
     settingsPath="solr_sudachi.json"
     discardPunctuation="true"
   />
   
   <!-- Whatever Token Filters -->
   ...
 </analyzer>
</fieldType>
```

### Configuration

Basically `solr-sudachi` follows the config on
 [`elasticsearch-sudachi`](https://github.com/WorksApplications/elasticsearch-sudachi#configuration)
as much as possible. Here it explains only the difference from that.

- `systemDictPath`: Put the relative file path from `${SOLR_HOME}`, or 
absolute path to the dict file. The file name must follow the `settingsPath`'s 
`systemDict` property. All the other Sudachi system files such as `char.def` or 
`rewrite.def` which are specified on `settingsPath` are the relative path 
from the `systemDictPath`. For the above example, they should be put
in the same "sudachi" directory.
- `settingsPath`: Put the sudachi json configuration. 
If `settingsPath` is not provided, Sudachi's default settings are used.


## Licenses

solr-sudachi is licensed under Apache License, Ver 2.0.

The original Sudachi and elasticsearch-sudachi are 
by Works Applications Co., Ltd., which are licensed under Apache License,
Ver 2.0.

