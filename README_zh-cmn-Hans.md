[English](README.md), [简体中文（普通话）](README_zh-cmn-Hans.md), [繁體中文（普通話）](README_zh-cmn-Hant.md)
# LanguageString
## 简介
LanguageString是一个及其便利的本地化类库，它允许您通过语言、下属方言、文字、地区甚至私有标签进行本地化，允许您通过链接、排列优先级等方式灵活地适配不同场景的需求。以下是简单的示例
```kotlin
val welcome = LangStrings( rootString = "Welcome to the world!")
welcome[Lang.zh_cmn_Hans_CN] = "欢迎来到中国！"
welcome[Lang("zh","cmn","Hant","TW")] = "歡迎來到台灣！"
welcome[Lang("zh","yue","Hant","CN","GD")] = "歡迎嚟到廣東！"
welcome[Lang(lang = "fr", region = "FR")] = "Bienvenue en France!"
welcome[Lang(lang = "fr")] = "Bienvenue dans le monde!"

println(welcome.get())                  //以当前Markdown文档语言，结果为"欢迎来到中国！"
println(welcome[Langs.zh_CN])            //"欢迎来到中国！"
println(welcome[Langs.zh_TW])            //"歡迎來到台灣！"
println(welcome[Lang("zh","yue","Hant","CN","GD")])//"歡迎嚟到廣東！"
println(welcome[Lang(lang = "fr", region = "FR")])//"Bienvenue en France!"
println(welcome[Lang(lang = "fr", region = "CA")])//"Bienvenue dans le monde!"
```

## 优先级
您可使用优先级来配置在获取字符串时由于匹配不到所舍去的标签以及转为字符串时标签的排列顺序。

### 创建优先级
在创建优先级时，您需要确保有至少两个标签需要添加

- Kotlin代码示例：
  ```kotlin
  val DEFAULT = Tag.LANGUAGE next Tag.DIALECT next Tag.SCRIPT next Tag.REGION next Tag.PRIVATE
  ```
- Java代码示例:
  ```java
  MatchPriority DEFAULT = MatchPriorityUtil.create(Tag.LANGUAGE, Tag.DIALECT, Tag.SCRIPT, Tag.REGION, Tag.PRIVATE);
  ```
Java和Kotlin中调用的方法不是同一个，但实际上实现上和Kotlin的代码差异不大。这样做只是为了让Java更方便地调用。
### 使用优先级
- 使用优先级进行字符串转换：
  在调用` Lang.joinToString(priority: MatchPriority,...) : String`方法转换字符串时，会按照其中`priority`的形参的标签添加顺序输出由标签组成的字符串。
  - Kotlin代码示例：
  ```kotlin
  val result = Lang("zh","yue","Hant","CN","GD")
            .joinToString(LANGUAGE next REGION next PRIVATE next DIALECT next SCRIPT)
  //此时result的值是zh_CN_GD_yue_Hant
  ```
  - Java代码示例：
  ```java
  Lang lang = new Lang("zh","yue","Hant","CN","GD");
  MatchPriority priority = MatchPriorityUtil.create(Tag.LANGUAGE, Tag.REGION, Tag.PRIVATE, Tag.DIALECT, Tag.SCRIPT);
  String result = lang.joinToString(priority,"-","","",s -> s);
  //此时result的值是zh-CN-GD-yue-Hant
  ```
- 使用优先级进行匹配字符串
  在调用`LangStrings.match(lang: Lang,priority: MatchPriority,...) : Lang`方法匹配`LangStrings`对象含有的`Lang`对象时，若匹配不到完整标签的`Lang`对象，则会以优先级添加的倒序进行剔除标签。具体参阅源代码及文档
  - Kotlin代码示例：
  ```kotlin
  val lang = Lang("zh","yue","Hant","CN","GD")
  val priority = LANGUAGE next REGION next PRIVATE next DIALECT next SCRIPT

  val langStrings = LangStrings("root")

  langStrings[Langs.zh_CN] = "zh_CN"
  langStrings[Lang(lang = "zh")] = "zh"

  println(langStrings[lang,priority])   //zh_CN
  println(langStrings[lang])            //zh
  ```
  - Java代码示例：
  ```java
  Lang lang = new Lang("zh","yue","Hant","CN","GD");
  MatchPriority priority = MatchPriorityUtil.create(Tag.LANGUAGE, Tag.REGION, Tag.PRIVATE, Tag.DIALECT, Tag.SCRIPT);
  MatchPriority defaultPriority = MatchPriority.DEFAULT;

  LangStrings langStrings = new LangStrings("root",new HashMap<>(),LangStrings.Companion.getBaseLinks());

  langStrings.set(Langs.zh_CN, "zh_CN");
  langStrings.set(new Lang("zh","","",""), "zh");

  System.out.println(langStrings.get(lang,priority,false,false));         //zh_CN
  System.out.println(langStrings.get(lang,defaultPriority,false,false));  //zh
  ```
  以上的示例均没有使用链接.
## 链接
<b style="color:darkred"> ！注意！链接不适用于私有标签。以下有关标签的讨论都不将私有标签包含在内</b>

链接指的是在匹配`Lang`时按照一定的规则替换成另一个被链接的`Lang`

- `LangStrings.baseLinks`：这是位于`LangStrings`伴生对象内的`Map<Link,Link>`成员，里面存储着`Lang`之间的映射关系。这个成员的可见性是`public`，所以你可以直接在里面添加键值对来影响整个软件。

  - 你也可以在调用构造函数时指定其他的`baseLinks`。在每次使用到链接时，都会调用`LangStrings.updateLinks(content: MutableMap<Lang, Lang>)`以更新链接映射关系，其中`content`的默认值是`LangStrings`中的`baseLinks` **（注意，这里指的是`(LangStrings)this.baseLinks`，而不是伴生对象中的`baseLinks`）** 成员。
  - 如果你是使用的`LangStrings`伴生对象中`baseLinks`，这条可忽略。的由于是直接引用，请确保构造函数中传入的`baseLinks`在`LangStrings`销毁之后再销毁，或者在`baseLinks`销毁后不再使用原`LangStrings`对象。
- `LangStrings.links`是在匹配链接过程中实际调用的对象。在初始化时，这个成员会被赋值为主构造函数中`links`参数的复制对象，所以不会像`baseLinks`那样随着主构造函数中`links`参数改变而改变。在调用`LangStrings.updateLinks(content: MutableMap<Lang, Lang>)`时，`links`成员的的值会和`content`参数相加，从而达到更新的效果
### 通配符
- `*`：表示匹配所有字符串。这个通配符仅适用于声明匹配规则的`Lang`对象，而不适用于匹配结果的`Lang`对象。

  示例：
  ```kotlin
  Lang(lang = "zh", dialect = "*", script = "Hans", region = "*") to Langs.zh_CN
  //此时，zh_XXX_Hans_XXX会被替换为Langs.zh_CN
  ```
- `?`：表示位于该位置的字符串，在匹配结果时会被替换为声明匹配规则的`Lang`对象位于该位置的字符串。
  示例：
  ```kotlin
  Lang("?","*","*","?") to Lang("?","","","?")
  //此时，LANG_XXX_XXX_REGION都会被替换为LANG_REGION
  //例如zh_zyn_Latn_CN会被替换为zh_CN
  ```
## 语言、方言、文字、地区代码、私有标签
代码表格： 
- 语言代码的表格可以参阅[Wikipedia上的ISO 639-1词条](https://zh.wikipedia.org/wiki/ISO_6039-1)。
- 方言代码的表格可以参阅[Wikipedia上的ISO 639-3词条](https://zh.wikipedia.org/wiki/ISO_639-3)；
  想要更直观的查看世界上所有的方言，可以访问[Ethnologue.com](https://www.ethnologue.com/)。
- 文字代码的表格可以参阅[Wikipedia上的ISO 15924词条](https://zh.wikipedia.org/wiki/ISO_15924)。
- 地区代码的表格可以参阅[Wikipedia上的ISO 3166-1词条](https://zh.wikipedia.org/wiki/ISO_3166-1)。注意，在本地化时一般不使用三位数字代码
- 私有标签一般作用不大，一般用于特殊的场景，也可以用于标注某地区下的行政区。如果你将要这么做，请参阅[Wikipedia上的ISO 3166-2词条](https://zh.wikipedia.org/wiki/ISO_3166-2)。
  - 关于中国香港、中国澳门、中国台湾以及其他在[ISO 3166-1](https://zh.wikipedia.org/wiki/ISO_3166-1)中的非独立主权地区：由于本代码库主要功能为帮助开发人员更好地进行本地化，所以应按照语言代码的标准在表示这些地区时直接在地区标签使用[ISO 3166-1](https://zh.wikipedia.org/wiki/ISO_3166-1)的代码（例如`HK`、`MO`、`TW`等），而不是在地区标签使用所属主权国家代码后后再在私有标签内添加等行政区标签。
    - 正确示例：
    ```kotlin
        welcome[Lang("zh","yue","Hant","CN","GD")] = "歡迎嚟到廣東！"
        welcome[Lang("zh","cmn","Hant","TW")] = "歡迎來到台灣！"
    ```
    - 错误示例：
    ```kotlin
        welcome[Lang("zh","cmn","Hant","CN","TW")] = "歡迎來到台灣！"
    ```
