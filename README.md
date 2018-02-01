# solr-sudachi

A Japanese morphological analyzer Sudachi as a Solr plugin.

This plugin is based on [elasticsearch-sudachi](https://github.com/WorksApplications/elasticsearch-sudachi)
which includes the common lucene Tokenizer and TokenFilters.

<img width="620px" alt="solr_analysis" src="https://user-images.githubusercontent.com/6478810/34778205-fedd8e00-f65f-11e7-8f43-bb8ec848e49a.png">

## Install

1. Edit `properties/solr.version` with your solr's version on `pom.xml` (Default is 6.2.1)
2. Do `mvn package` to generate `solr-sudachi-assembly-1.0.0-SNAPSHOT.jar` in `assembly/target/`.
2. Put `solr-sudachi-assembly-1.0.0-SNAPSHOT.jar` on `${SOLR_HOME}/lib` directory.
4. Configure schema.xml or managed-schema with the following setting, then start solr.

## Version

Solr 6.2.1 or above.

Note that not all the versions are tested. Please report through issues if any problem found with a version.

## Tokenizer

### SolrSudachiTokenizerFactory

#### Configuration

```xml
<fieldType name="text_ja" class="solr.TextField">
 <analyzer>
   <!-- Whatever Char Filters -->
   ...
   
   <tokenizer class="com.github.sh0nk.solr.sudachi.SolrSudachiTokenizerFactory"
     mode="NORMAL"
     discardPunctuation="true"
   />
   
   <!-- Whatever Token Filters -->
   ...
 </analyzer>
</fieldType>
```

Basically `solr-sudachi` follows the config on
 [`elasticsearch-sudachi`](https://github.com/WorksApplications/elasticsearch-sudachi#configuration)
as much as possible. Here it explains only the difference from that.

As default, `system_full.dic` which is provided by Sudachi is used as a dictionary.
And [`solr_sudachi.json`](https://github.com/sh0nk/solr-sudachi/blob/develop/assembly/src/main/resources/solr_sudachi.json)
is used as a tokenizer plugin setting. So the above setting is everything to start with, 
but if you want to customize, the following properties are available to configure.

- `settingsPath`: Put the sudachi json configuration with 
the relative file path from `${SOLR_HOME}/conf`, or absolute path to the dict file.
- `systemDictDir`: Put the relative directory path from `${SOLR_HOME}/conf`, or 
absolute path to the dict file directory. All the other Sudachi system files such as `char.def` or 
`rewrite.def` which are specified on `settingsPath` are the relative path 
from the `systemDictPath`. For example, if `systemDictDir="sudachi"` is given, 
they should be put in the same "sudachi" directory.

`system_full.dic` and `system_core.dic` are bundled in `solr-sudachi` jar.
If one of the names is given in `system_dict` property on settings json,
solr-sudachi extracts it into `systemDictDir`. If it is not given, then 
the extracted file goes to `${SOLR_HOME}/conf`.


## Token Filters

In addition to the core `SolrSudachiTokenizerFactory`, several token filters
are available as a post processing of the `Tokenizer`.

### SudachiSurfaceFormFilterFactory

`solr-sudachi` respects the behavior of `elasticsearch-sudachi`, which outputs 
the *Normalized form* of the tokens instead of the *Surface form* which are tokens 
from input text as it is. Sudachi's normalized form performs to make the analyzed 
tokens respected more as well as base form. But if you want to match exactly with 
the query and the index, this filter would be useful.

#### Example

Before the token filter

|          |1    |2   |3   |4   |5   |
|:---------|:----|:---|:---|:---|:---|
|Tokens    |吾が輩|は|猫|だ|有る|
|Surface   |吾輩  |は|猫|で|ある|

After the token filter

|          |1    |2   |3   |4   |5   |
|:---------|:----|:---|:---|:---|:---|
|Tokens   |吾輩  |は|猫|で|ある|


#### Configuration

```xml
<fieldType name="text_ja" class="solr.TextField">
 <analyzer>
   <tokenizer class="com.github.sh0nk.solr.sudachi.SolrSudachiTokenizerFactory"
     mode="NORMAL"
     discardPunctuation="true"
   />
   
   <filter class="com.github.sh0nk.solr.sudachi.SudachiSurfaceFormFilterFactory" />
 </analyzer>
</fieldType>
```


## Licenses

solr-sudachi is licensed under Apache License, Ver 2.0.

The original Sudachi and elasticsearch-sudachi are 
by Works Applications Co., Ltd., which are licensed under Apache License,
Ver 2.0.

