package io.github.createduser.lang_string;

import io.github.createduser.lang_string.Tag.*

/**
 * 用于处理`Lang`与字符串之间映射关系的类
 *
 * @property baseLinks 基础链接映射。这个属性是直接引用。所以改变原先构造函数传入的形参时这个属性也会跟着改变
 * @property langStringMap `Lang`与字符串的映射
 * @property links 链接映射。由于是将原先构造函数传入的形参`link`复制后再赋值，所以原先的形参改变时这个属性不会跟着改变
 * @constructor 将形参`links`复制后在添加`baseLinks`的键值对再复制给类中的属性
 *
 * @param rootString 根字符串。在获取字符串时如果实在没有匹配到的字符串，则就会返回这个。
 * @param baseLinks 基础链接映射。这个属性是直接引用。所以改变构造函数传入的形参时对应的属性也会跟着改变
 * @param links 链接映射。初始化时会将这个形参复制后再赋值给对应的属性，所以形参改变时对应的属性不会跟着改变
 */
class LangStrings(rootString: String,links : MutableMap<Lang,Lang> = mutableMapOf(),val baseLinks : MutableMap<Lang,Lang> = LangStrings.baseLinks){
    private val langStringMap = mutableMapOf(Langs.root to rootString)
    val links : MutableMap<Lang,Lang>

    companion object Companion{
        /**
         * 用于配置基本的链接关系
         *
         * 其中的链接关系及链接的原因：
         * 1. zh_*_Hans_* 链接zh_CN：大部分简体中文指的都是中文（中国大陆）
         * 2. zh_*_Hant_* 链接zh_TW：大部分繁体中文指的都是中文（中国台湾）
         * 3. zh_CN链接至zh_cmn_Hans_CN：中文（中国大陆）大多指的都是简体中文（中国大陆，普通话）。并且由于有可能会通过`Locale`对象来获得字符串，而`Locale`对象在获得简体中文（中国大陆）的环境时，返回的都是zh_CN，添加这个链接关系有利于适配`Locale`对象
         * 4. zh_TW链接至zh_cmn_Hant_TW：中文（中国台湾）大多指的都是繁体中文（中国台湾，普通话）。并且由于有可能会通过`Locale`对象来获得字符串，而`Locale`对象在获得繁体中文（中国台湾）的环境时，返回的都是zh_TW，添加这个链接关系有利于适配`Locale`对象
         * 5. ?_*_*_? 链接至?_?：在用`Locale`获得本地化环境时，大多数情况下不会有方言和文字标签，添加这个链接关系有利于适配`Locale`对象
         */
        @JvmField
        val baseLinks : MutableMap<Lang,Lang> = mutableMapOf(
            Lang(lang = "zh", dialect = "*", script = "Hans", region = "*") to Langs.zh_CN,
            Lang(lang = "zh", dialect = "*", script = "Hant", region = "*") to Langs.zh_TW,

            Langs.zh_CN to Langs.zh_cmn_Hans_CN,
            Langs.zh_TW to Langs.zh_cmn_Hant_TW,

            Lang("?","*","*","?") to Lang("?","","","?"),
        )
    }

    init {
        this.links = links.toMutableMap()
        this.links += this.baseLinks
    }

    /**
     * 获得字符串。
     *
     * 直接通过`Lang.match`的结果在`LangStrings.langStringMap`获得字符串
     *
     * @param lang 语言
     * @param priority 优先级，匹配时会以优先级倒序剔除标签
     * @param useLink 是否使用链接
     * @param linkFirst 是否先使用链接关系匹配。具体参阅`LangStrings.match`
     * @return `lang`对应的字符串
     * @see LangStrings.match
     */
    operator fun get(lang: Lang = Lang(),priority: MatchPriority = MatchPriority.DEFAULT,useLink : Boolean = true,linkFirst : Boolean = true):String = langStringMap[match(lang,priority,useLink,linkFirst)]!!

    /**
     * Match
     *
     *
     * @param lang 语言
     * @param priority 优先级，匹配时会以优先级倒序剔除标签
     * @param useLink 是否使用链接
     * @param linkFirst 是否先使用链接关系匹配。如果是，那么首先会调用`LangStrings.matchLinks`，如果结果不为null，则直接获得链接对应的字符串，如果结果为null，则继续通过剔除标签的方式来匹配；如果否，那么首先通过剔除标签的方式来匹配，匹配不到再尝试使用链接关系匹配。
     * @return `lang`所匹配到的本`LangStrings`对象中存在的`Lang`对象
     * @see LangStrings.matchLink
     */
    fun match(lang: Lang,priority: MatchPriority = MatchPriority.DEFAULT,useLink : Boolean = true,linkFirst : Boolean = true) : Lang{
        if (lang in this.langStringMap)
            return lang

        if (matchLink(lang) in langStringMap && useLink && linkFirst)
            return matchLink(lang)!!

        val privates = lang.getPrivates().toMutableList()

        var language = lang[LANGUAGE]
        var dialect = lang[DIALECT]
        var script = lang[SCRIPT]
        var region = lang[REGION]

        var tagIndex = 0;
        val tagListReversed : MutableList<Tag> = priority.tagList.toMutableList()
        tagListReversed.reverse()

        var result : Lang = lang

        while (result !in this.langStringMap && tagIndex < priority.tagList.size) {


            when(tagListReversed[tagIndex]){
                LANGUAGE -> language = ""
                DIALECT -> dialect = ""
                SCRIPT -> script = ""
                REGION -> region = ""
                PRIVATE -> {
                    if (privates.isNotEmpty()){
                        privates.removeLast()
                        result = Lang(language,dialect,script,region, privates = privates.toTypedArray())
                        continue
                    }
                    tagIndex++
                    continue
                }
            }

            result = Lang(language,dialect,script,region, privates = privates.toTypedArray())

            tagIndex++
        }


        if (result in this.langStringMap)
            return result
        else if (matchLink(lang) in langStringMap && useLink && !linkFirst)
                return matchLink(lang)!!
        else
            return Langs.root
    }

    /**
     * 添加或设置字符串
     *
     * @param lang 语言
     * @param string 字符串
     */
    operator fun set(lang: Lang,string: String){
        if (lang !in langStringMap) {
            langStringMap += lang to string
            return
        }

        langStringMap[lang] = string
    }

    /**
     * 匹配链接中的语言，在匹配之前会更新链接
     *
     * @param lang 要匹配的语言
     * @return 如果匹配到了，返回结果。如果没有，返回`null`
     */
    fun matchLink(lang : Lang) : Lang? {
        this.updateLinks()

        val langKeys = this.links.keys

        for (langKey in langKeys){
            var matched = true

            val matchMap : MutableMap<Tag,Boolean> = mutableMapOf()

            Tag.entries.forEach{
                if (it != PRIVATE)
                    matchMap += it to false
            }

            for (langMapEntry in langKey.toMutableMap() - PRIVATE){
                if (langMapEntry.value == lang[langMapEntry.key]){
                    matchMap[langMapEntry.key] = true
                    continue
                }
                if (langMapEntry.value == "*" || langMapEntry.value == "?"){
                    matchMap[langMapEntry.key] = true
                    continue
                }
            }

            for (tagMatched in matchMap.values){
                if (!tagMatched) {
                    matched = false
                    break
                }
            }

            if (matched){
                val matchLang = this.links[langKey]!!

                val langMap: MutableMap<Tag,String> = mutableMapOf()

                Tag.entries.forEach {
                    if (it != PRIVATE)
                        langMap += it to matchLang[it]
                }

                for (entry in langMap){
                    if (entry.value == "?")
                        langMap[entry.key] = lang[entry.key]

                }

                return Lang(
                    lang = langMap[LANGUAGE]!!,
                    dialect = langMap[DIALECT]!!,
                    script = langMap[SCRIPT]!!,
                    region = langMap[REGION]!!
                )
            }
        }

        return null
    }

    /**
     * 更新链接。
     *
     * @param content 更新的内容。默认为`(LangStrings)this.baseLinks`
     */
    fun updateLinks(content: MutableMap<Lang, Lang> = this.baseLinks){
        this.links += content;
    }

    override fun hashCode(): Int {
        return this.langStringMap.hashCode() * 25
    }

    override fun equals(other: Any?): Boolean {
        try {
            val otherLang = other as LangStrings
            return this.langStringMap == otherLang.langStringMap
        }catch (e:Exception){
            return false
        }
    }
}