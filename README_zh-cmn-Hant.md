[English](README.md), [简体中文（普通话）](README_zh-cmn-Hans.md), [繁體中文（普通話）](README_zh-cmn-Hant.md)
# LanguageString
## 簡介
LanguageString是一個及其便利的在地化軟體庫，它允許您透過語言、下屬方言、文字、地區甚至私有標籤進行在地化，讓您可以透過連結、排列優先順序等方式靈活地適配不同場景的需求。以下是簡單的範例
```kotlin
val welcome = LangStrings( rootString = "Welcome to the world!")
welcome[Lang.zh_cmn_Hans_CN] = "欢迎来到中国！"
welcome[Lang("zh","cmn","Hant","TW")] = "歡迎來到台灣！"
welcome[Lang("zh","yue","Hant","CN","GD")] = "歡迎嚟到廣東！"
welcome[Lang(lang = "fr", region = "FR")] = "Bienvenue en France!"
welcome[Lang(lang = "fr")] = "Bienvenue dans le monde!"

println(welcome.get())                  //以当前Markdown文档语言，结果为"歡迎來到台灣！"
println(welcome[Langs.zh_CN])            //"欢迎来到中国！"
println(welcome[Langs.zh_TW])            //"歡迎來到台灣！"
println(welcome[Lang("zh","yue","Hant","CN","GD")])//"歡迎嚟到廣東！"
println(welcome[Lang(lang = "fr", region = "FR")])//"Bienvenue en France!"
println(welcome[Lang(lang = "fr", region = "CA")])//"Bienvenue dans le monde!"
```

## 優先權
您可使用優先權來配置在取得字串時由於符合不到所捨去的標籤以及轉為字串時標籤的排列順序。

### 建立優先級
在建立優先順序時，您需要確保有至少兩個標籤需要添加

- Kotlin程式碼範例：
  ```kotlin
  val DEFAULT = Tag.LANGUAGE next Tag.DIALECT next Tag.SCRIPT next Tag.REGION next Tag.PRIVATE
  ```
- Java程式碼範例:
  ```java
  MatchPriority DEFAULT = MatchPriorityUtil.create(Tag.LANGUAGE, Tag.DIALECT, Tag.SCRIPT, Tag.REGION, Tag.PRIVATE);
  ```
Java和Kotlin中呼叫的方法不是同一個，但實際上實作上和Kotlin的程式碼差異不大。這樣做只是為了讓Java更方便地呼叫。
### 使用優先權
- 使用優先權進行字串轉換：
  在呼叫` Lang.joinToString(priority: MatchPriority,...) : String`方法轉換字串時，會依照其中`priority`的形參的標籤新增順序輸出由標籤組成的字串。
    - Kotlin程式碼範例：
  ```kotlin
  val result = Lang("zh","yue","Hant","CN","GD")
            .joinToString(LANGUAGE next REGION next PRIVATE next DIALECT next SCRIPT)
  //此時result的值是zh_CN_GD_yue_Hant
  ```
    - Java程式碼範例：
  ```java
  Lang lang = new Lang("zh","yue","Hant","CN","GD");
  MatchPriority priority = MatchPriorityUtil.create(Tag.LANGUAGE, Tag.REGION, Tag.PRIVATE, Tag.DIALECT, Tag.SCRIPT);
  String result = lang.joinToString(priority,"-","","",s -> s);
  //此時result的值是zh-CN-GD-yue-Hant
  ```
- 使用優先權進行匹配字串
  在呼叫`LangStrings.match(lang: Lang,priority: MatchPriority,...) : Lang`方法匹配`LangStrings`對象所含的`Lang`對象時，若匹配不到完整標籤的`Lang`對象，則會以優先權新增的倒序進行剔除標籤。具體參閱原始碼及文檔
    - Kotlin程式碼範例：
  ```kotlin
  val lang = Lang("zh","yue","Hant","CN","GD")
  val priority = LANGUAGE next REGION next PRIVATE next DIALECT next SCRIPT

  val langStrings = LangStrings("root")

  langStrings[Langs.zh_CN] = "zh_CN"
  langStrings[Lang(lang = "zh")] = "zh"

  println(langStrings[lang,priority]) //zh_CN
  println(langStrings[lang]) //zh
  ```
    - Java程式碼範例：
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
  以上的範例均沒有使用連結.
## 連結
<b style="color:darkred"> ！注意！連結不適用於私有標籤。以下有關標籤的討論都不將私有標籤包含在內</b>

連結指的是當符合`Lang`時依照一定的規則替換成另一個被連結的`Lang`

- `LangStrings.baseLinks`：這是位於`LangStrings`伴生物件內的`Map<Link,Link>`成員，裡面儲存著`Lang`之間的映射關係。這個成員的可見性是`public`，所以你可以直接在裡面加入鍵值對來影響整個軟體。

    - 你也可以在呼叫建構函式時指定其他的`baseLinks`。每次使用到連結時，都會呼叫`LangStrings.updateLinks(content: MutableMap<Lang, Lang>)`以更新連結映射關係，其中`content`的預設值是`LangStrings`中的`baseLinks` **（請注意，這裡指的是`(LangStrings)this.baseLinks`，而不是伴生物件中的`baseLinks`）** 成員。
    - 如果你是使用的`LangStrings`伴生物件中`baseLinks`，這條可忽略。的由於是直接引用，請確保建構函式中傳入的`baseLinks`在`LangStrings`銷毀之後再銷毀，或在`baseLinks`銷毀後不再使用原`LangStrings`物件。
- `LangStrings.links`是在匹配連結過程中實際呼叫的物件。在初始化時，這個成員會被賦值為主建構子中`links`參數的複製對象，所以不會像`baseLinks`那樣隨著主建構子中`links`參數改變而改變。當呼叫`LangStrings.updateLinks(content: MutableMap<Lang, Lang>)`時，`links`成員的值會和`content`參數相加，達到更新的效果
### 通配符
- `*`：表示符合所有字串。這個通配符僅適用於宣告符合規則的`Lang`對象，而不適用於符合結果的`Lang`對象。

  範例：
  ```kotlin
  Lang(lang = "zh", dialect = "*", script = "Hans", region = "*") to Langs.zh_CN
  //此時，zh_XXX_Hans_XXX會被替換為Langs.zh_CN
  ```
- `?`：表示位於該位置的字串，在符合結果時會被替換為宣告符合規則的`Lang`物件位於該位置的字串。
  範例：
  ```kotlin
  Lang("?","*","*","?") to Lang("?","","","?")
  //此時，LANG_XXX_XXX_REGION都會被替換為LANG_REGION
  //例如zh_zyn_Latn_CN會被替換為zh_CN
  ```
## 語言、方言、文字、地區代碼、私有標籤
程式碼表格：
- 語言代碼的表格可以參考[Wikipedia上的ISO 639-1詞條](https://zh.wikipedia.org/wiki/ISO_6039-1)。
- 方言代碼的表格可以參考[Wikipedia上的ISO 639-3詞條](https://zh.wikipedia.org/wiki/ISO_639-3)；
  想要更直觀的查看世界上所有的方言，可以訪問[Ethnologue.com](https://www.ethnologue.com/)。
- 文字代碼的表格可以參考[Wikipedia上的ISO 15924詞條](https://zh.wikipedia.org/wiki/ISO_15924)。
- 地區代碼的表格可以參考[Wikipedia上的ISO 3166-1詞條](https://zh.wikipedia.org/wiki/ISO_3166-1)。注意，在本地化時一般不使用三位數字代碼
- 私有標籤一般作用不大，一般用於特殊的場景，也可以用來標註某地區下的行政區。如果你將要這麼做，請參考[Wikipedia上的ISO 3166-2詞條](https://zh.wikipedia.org/wiki/ISO_3166-2)。
    - 關於中國香港、中國澳門、中國台灣以及其他在[ISO 3166-1](https://zh.wikipedia.org/wiki/ISO_3166-1)中的非獨立主權地區：由於本程式碼庫主要功能為幫助開發人員更好地進行在地化，所以應按照語言程式碼的標準在表示這些地區時直接在地區標籤使用[ISO 3166-1](https://zh.wikipedia.org/wiki/ISO_3166-1)的代碼（例如`HK`、`MO`、`TW`等），而不是在地區標籤使用所屬主權國家代碼後後再在私有標籤內添加等行政區標籤。
        - 正確範例：
      ```kotlin
          welcome[Lang("zh","yue","Hant","CN","GD")] = "歡迎嚟到廣東！"
          welcome[Lang("zh","cmn","Hant","TW")] = "歡迎來到台灣！"
      ```
        - 錯誤範例：
      ```kotlin
          welcome[Lang("zh","cmn","Hant","CN","TW")] = "歡迎來到台灣！"
      ```