[English](README.md), [简体中文（普通话）](README_zh-cmn-Hans.md), [繁體中文（普通話）](README_zh-cmn-Hant.md)
# LanguageString
## Introduction
LanguageString is an extremely convenient localization class library. It allows you to localize by language, subordinate dialect, text, region and even private tags, allowing you to flexibly adapt to the needs of different scenarios through linking, prioritization, etc. Here is a simple example
```kotlin
val welcome = LangStrings( rootString = "Welcome to the world!")
welcome[Lang.zh_cmn_Hans_CN] = "欢迎来到中国！"
welcome[Lang("zh","cmn","Hant","TW")] = "歡迎來到台灣！"
welcome[Lang("zh","yue","Hant","CN","GD")] = "歡迎嚟到廣東！"
welcome[Lang(lang = "fr", region = "FR")] = "Bienvenue en France!"
welcome[Lang(lang = "fr")] = "Bienvenue dans le monde!"

println(welcome.get())                  //In the current Markdown document language, the result is "Welcome to the world!"
println(welcome[Langs.zh_CN])            //"欢迎来到中国！"
println(welcome[Langs.zh_TW])            //"歡迎來到台灣！"
println(welcome[Lang("zh","yue","Hant","CN","GD")])//"歡迎嚟到廣東！"
println(welcome[Lang(lang = "fr", region = "FR")])//"Bienvenue en France!"
println(welcome[Lang(lang = "fr", region = "CA")])//"Bienvenue dans le monde!"
```

## priority
You can use the priority to configure the tags that are discarded due to no matches when obtaining the string and the order in which the tags are sorted when converting to a string.

### Create priority
When creating priorities you need to make sure there are at least two tags to add

- Kotlin code example:
  ```kotlin
  val DEFAULT = Tag.LANGUAGE next Tag.DIALECT next Tag.SCRIPT next Tag.REGION next Tag.PRIVATE
  ```
- Java code example:
  ```java
  MatchPriority DEFAULT = MatchPriorityUtil.create(Tag.LANGUAGE, Tag.DIALECT, Tag.SCRIPT, Tag.REGION, Tag.PRIVATE);
  ```
The methods called in Java and Kotlin are not the same, but the actual implementation is not much different from the Kotlin code. This is done just to make it easier to call from Java.
### Use priority
- Use precedence for string conversion:
  When calling the `Lang.joinToString(priority: MatchPriority,...) : String` method to convert a string, a string composed of labels will be output in the order in which the labels of the `priority` formal parameter are added.
    - Kotlin code example:
  ```kotlin
  val result = Lang("zh","yue","Hant","CN","GD")
            .joinToString(LANGUAGE next REGION next PRIVATE next DIALECT next SCRIPT)
  //The value of result at this time is zh_CN_GD_yue_Hant
  ```
    - Java code example:
  ```java
  Lang lang = new Lang("zh","yue","Hant","CN","GD");
  MatchPriority priority = MatchPriorityUtil.create(Tag.LANGUAGE, Tag.REGION, Tag.PRIVATE, Tag.DIALECT, Tag.SCRIPT);
  String result = lang.joinToString(priority,"-","","",s -> s);
  //The value of result at this time is zh-CN-GD-yue-Hant
  ```
- Use priority to match strings
  When calling the `LangStrings.match(lang: Lang,priority: MatchPriority,...) : Lang` method to match the `Lang` object contained in the `LangStrings` object, if the `Lang` object with the complete label cannot be matched, it will Tags are culled in reverse order of priority added. Please refer to the source code and documentation for details
    - Kotlin code example:
  ```kotlin
  val lang = Lang("zh","yue","Hant","CN","GD")
  val priority = LANGUAGE next REGION next PRIVATE next DIALECT next SCRIPT

  val langStrings = LangStrings("root")

  langStrings[Langs.zh_CN] = "zh_CN"
  langStrings[Lang(lang = "zh")] = "zh"

  println(langStrings[lang,priority]) //zh_CN
  println(langStrings[lang]) //en
  ```
    - Java code example:
  ```java
  Lang lang = new Lang("zh","yue","Hant","CN","GD");
  MatchPriority priority = MatchPriorityUtil.create(Tag.LANGUAGE, Tag.REGION, Tag.PRIVATE, Tag.DIALECT, Tag.SCRIPT);
  MatchPriority defaultPriority = MatchPriority.DEFAULT;

  LangStrings langStrings = new LangStrings("root",new HashMap<>(),LangStrings.Companion.getBaseLinks());

  langStrings.set(Langs.zh_CN, "zh_CN");
  langStrings.set(new Lang("zh","","",""), "zh");

  System.out.println(langStrings.get(lang,priority,false,false)); //zh_CN
  System.out.println(langStrings.get(lang,defaultPriority,false,false)); //en
  ```
  None of the examples above use links.
## Link
<b style="color:darkred"> ! Notice ! Links don't work with private labels. The following discussion of tags does not include private tags</b>

Linking refers to replacing `Lang` with another linked `Lang` according to certain rules when matching `Lang`

- `LangStrings.baseLinks`: This is the `Map<Link,Link>` member located in the `LangStrings` companion object, which stores the mapping relationship between `Lang`. The visibility of this member is `public`, so you can directly add key-value pairs in it to affect the entire software.

    - You can also specify other `baseLinks` when calling the constructor. Every time a link is used, `LangStrings.updateLinks(content: MutableMap<Lang, Lang>)` will be called to update the link mapping relationship, where the default value of `content` is `baseLinks` in `LangStrings` **( Note that this refers to `(LangStrings)this.baseLinks`, not the `baseLinks`)** member in the companion object.
    - If you are using `baseLinks` in the `LangStrings` companion object, this can be ignored. Since it is a direct reference, please ensure that the `baseLinks` passed in the constructor is destroyed after `LangStrings` is destroyed, or the original `LangStrings` object is no longer used after `baseLinks` is destroyed.
- `LangStrings.links` is the object actually called during matching links. During initialization, this member will be assigned the copy object of the `links` parameter in the main constructor, so it will not change as the `links` parameter in the main constructor changes like `baseLinks`. When calling `LangStrings.updateLinks(content: MutableMap<Lang, Lang>)`, the value of the `links` member will be added to the `content` parameter to achieve the update effect.
### Wildcard
- `*`: means match all strings. This wildcard only applies to the `Lang` object that declares the matching rule, not to the `Lang` object that matches the result.

  Example:
  ```kotlin
  Lang(lang = "zh", dialect = "*", script = "Hans", region = "*") to Langs.zh_CN
  //At this time, zh_XXX_Hans_XXX will be replaced by Langs.zh_CN
  ```
- `?`: Indicates that the string located at this position will be replaced by the string at this position of the `Lang` object declaring the matching rule when matching the result.
  Example:
  ```kotlin
  Lang("?","*","*","?") to Lang("?","","","?")
  //At this time, LANG_XXX_XXX_REGION will be replaced by LANG_REGION
  //For example, zh_zyn_Latn_CN will be replaced by zh_CN
  ```
## Language, dialect, script, region code, private label
Code table:
- The table of language codes can be found in [ISO 639-1 entry on Wikipedia](https://zh.wikipedia.org/wiki/ISO_6039-1).
- For the table of dialect codes, please refer to [ISO 639-3 entry on Wikipedia](https://zh.wikipedia.org/wiki/ISO_639-3);
  If you want to view all the dialects in the world more intuitively, you can visit [Ethnologue.com](https://www.ethnologue.com/).
- For a table of text codes, please refer to [ISO 15924 entry on Wikipedia](https://zh.wikipedia.org/wiki/ISO_15924).
- For the table of area codes, please refer to [ISO 3166-1 entry on Wikipedia](https://zh.wikipedia.org/wiki/ISO_3166-1). Note that three-digit codes are generally not used when localizing
- Private tags are generally of little use and are generally used in special scenarios. They can also be used to mark administrative districts under a certain region. If you are going to do this, see [Wikipedia entry for ISO 3166-2](https://zh.wikipedia.org/wiki/ISO_3166-2).
    - Regarding Hong Kong, Macau, Taiwan, and other non-independent sovereign regions in [ISO 3166-1](https://zh.wikipedia.org/wiki/ISO_3166-1): Since the main functions of this code base are To help developers better localize, they should be used directly in the region tags when expressing these regions according to the language code standards [ISO 3166-1](https://zh.wikipedia.org/wiki/ISO_3166-1) code (such as `HK`, `MO`, `TW`, etc.) instead of using the sovereign country code for the region label and then adding administrative region labels in the private label.
        - Correct example:
      ```kotlin
          welcome[Lang("zh","yue","Hant","CN","GD")] = "Welcome to Guangdong!"
          welcome[Lang("zh","cmn","Hant","TW")] = "Welcome to Taiwan!"
      ```
        - Error example:
      ```kotlin
          welcome[Lang("zh","cmn","Hant","CN","TW")] = "Welcome to Taiwan!"
      ```