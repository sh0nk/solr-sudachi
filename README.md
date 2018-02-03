# solr-sudachi

A Japanese morphological analyzer Sudachi as a Solr plugin.

This plugin is based on [elasticsearch-sudachi](https://github.com/WorksApplications/elasticsearch-sudachi)
which includes the common lucene Tokenizer and TokenFilters.

<img width="620px" alt="solr_analysis" src="https://user-images.githubusercontent.com/6478810/34778205-fedd8e00-f65f-11e7-8f43-bb8ec848e49a.png">

## Install

1. Edit `properties/solr.version` with your solr's version on `pom.xml` (Default is 6.2.1)
2. Do `mvn package` to generate `solr-sudachi-assembly-1.0.0-SNAPSHOT.jar` in `assembly/target/`.
3. Put `solr-sudachi-assembly-1.0.0-SNAPSHOT.jar` on `${SOLR_HOME}/lib` directory.
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
the relative file path from `${SOLR_HOME}/conf`, or absolute path.
- `systemDictDir`: Put the relative directory path from `${SOLR_HOME}/conf`, or 
absolute path to the dict file directory. All the other Sudachi system files such as `char.def` or 
`rewrite.def` which are specified on `settingsPath` are the relative path 
from the `systemDictDir`. For example, if `systemDictDir="sudachi"` is given, 
they should be put in the same "sudachi" directory.

`system_full.dic` and `system_core.dic` are bundled in `solr-sudachi` jar.
If one of the names is given in `system_dict` property on settings json,
solr-sudachi extracts it into `systemDictDir`. If it is not given, then 
the extracted file goes to `${SOLR_HOME}/conf`. This extraction is needed to be
efficient memory handling on Sudachi.


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






-----

# solr-sudachi

日本語形態素解析器 Sudachi の Solr プラグイン solr-sudachi

solr-sudachiはluceneのTokenizerやTokenFilterのインターフェイスを提供する
[elasticsearch-sudachi](https://github.com/WorksApplications/elasticsearch-sudachi)
をベースにして作られています。

<img width="620px" alt="solr_analysis" src="https://user-images.githubusercontent.com/6478810/34778205-fedd8e00-f65f-11e7-8f43-bb8ec848e49a.png">

## Install

1. `pom.xml`の中の`properties/solr.version`を、使用するSolrのバージョンに合わせて変更します。
(デフォルトのバージョンは6.2.1)
2. `mvn package`を実行すると、`assembly/target/`の中に`solr-sudachi-assembly-1.0.0-SNAPSHOT.jar`が
生成されます。
3. 生成された`solr-sudachi-assembly-1.0.0-SNAPSHOT.jar`を使用するSolrの`${SOLR_HOME}/lib`ディレクトリに
コピーします。
4. schema.xmlあるいはmanaged-schemaを、以下の設定にならって編集し、Solrを起動します。


## Version

Solr 6.2.1 以上に対応しています。

すべてのバージョンについて正常動作が確認できているわけではありません。特定のバージョンで問題が発生した場合、
githubのissueを通してご連絡ください。

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

基本的に、`solr-sudachi`はベースとなる
 [`elasticsearch-sudachi`](https://github.com/WorksApplications/elasticsearch-sudachi#configuration)
の設定を継承しています。ここでは、`elasticsearch-sudachi`との差分となる設定を主に説明します。

Sudachiで使用されるシステム辞書として、デフォルトではSudachiが生成する`system_full.dic`を使用します。
Sudachiの内部的なプラグインチェーンを指定する設定ファイルとして、
[`solr_sudachi.json`](https://github.com/sh0nk/solr-sudachi/blob/develop/assembly/src/main/resources/solr_sudachi.json)
を使用します。これらがデフォルトとして指定されているため、スキーマファイルは上記の設定のみで使用を開始することが
できますが、もし辞書やプラグインチェーンを変更したい場合は、以下のプロパティにより上書きすることができます。



- `settingsPath`: Sudachiのjson設定ファイルを`${SOLR_HOME}/conf`からの相対パス、あるいは
絶対パスで指定します。
- `systemDictDir`: 辞書ファイルがあるディレクトリを、`${SOLR_HOME}/conf`からの相対パスか
絶対パスで指定します。`char.def`や`rewrite.def`など、上記のjson設定ファイルで相対パスとして
指定しているファイルはすべてこの場所に置く必要があります。
例えば、`systemDictDir="sudachi"`と設定した場合、これらのファイルは"sudachi"ディレクトリに
置きます。

`solr-sudachi`のjarファイルにはSudachiの辞書ビルダーによって生成される
`system_full.dic`と`system_core.dic`の２つの辞書が含まれます。
もしどちらかのファイル名がSudachiのjson設定ファイルの`system_dict`プロパティに設定された場合、
solr-sudachiはこのファイルを`systemDictDir`にコピーします。もし`systemDictDir`が
指定されていない場合、jarから抽出されたファイルは`${SOLR_HOME}/conf`に置かれます。
ファイルの抽出はSudachiのメモリ効率化に必要です。


## Token Filters

コアとなるトークナイザ`SolrSudachiTokenizerFactory`のほかにいくつかのトークンフィルタが
トークナイザの後処理として用意されています。

### SudachiSurfaceFormFilterFactory

`solr-sudachi`は`elasticsearch-sudachi`の挙動をなるべく引き継いでいます。
`elasticsearch-sudachi`では、標準で分かち書きした形態素を、元の入力形である *表層形* ではなく、
 *正規化形* として出力します。この *正規化形* は *基本形* と同様に、出力された形態素自身の一致を重視
するのに役立ちますが、もしクエリとインデックスの入力文字を重視したい場合は、表層形に変換する
このTokenFilterが役立つかもしれません。

#### Example

トークンフィルタの入力

|          |1    |2   |3   |4   |5   |
|:---------|:----|:---|:---|:---|:---|
|Tokens    |吾が輩|は|猫|だ|有る|
|Surface   |吾輩  |は|猫|で|ある|

トークンフィルタの出力

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

