package io.github.createduser.lang_string

import java.util.Locale

import io.github.createduser.lang_string.Tag.*

/**
 * 这是一个类，用于存储语言代码对应的`BaseLang`下标。
 * @see BaseLang
 * @constructor 当你调用构造函数时，你实际上并没有创建与语言代码相对应的 `Lang` 对象。事实上，`Lang`只是一个存储下标的类。这样可以大大提高内存利用效率
 * 调用构造函数时，会首先检查`Lang`的伴生对象的`baseLangs`中是否存在与该语言代码对应的`BaseLang`。如果是，则将找到的“BaseLang”对象的索引分配给“Lang”中的“index”。如果没有，请创建一个新的并添加它。同样，将`Lang`中的`index`分配给新创建的'BaseLang'对象
 *
 * @param lang 语言代码，会自动转为全小写
 * @param dialect 方言代码，会自动转为全小写
 * @param script 文本代码，会自动转为首字母大写
 * @param region 地区代码，会自动转为全大写
 */
class Lang(lang:String = "",dialect:String = "",script:String = "",region:String = "",vararg privates:String) {
    companion object Companion{
        /**
         * 存储全局`BaseLang`对象
         */
        private var baseLangs = mutableListOf(BaseLang())
    }

    /**
     * 当前`Lang`对象对应的`BaseLang`位于`baseLangs`的下标
     */
    private val index : Int

    /**
     * 用`Locale`对象初始化当前`Lang`对象
     */
    constructor(locale: Locale) : this(
        lang = locale.language,
        dialect = locale.variant,
        script = locale.script,
        region = locale.country
        )

    /**
     * 初始化当前语言环境的`Lang`对象
     */
    constructor():this(Locale.getDefault())
    init {
        val baseLang = BaseLang(
            lang =  lang.lowercase(),
            dialect = dialect.lowercase(),
            script =  script.lowercase().replaceFirstChar {it.titlecase(Locale.getDefault())},
            region =  region.uppercase(),
            privates = privates
        )
        if (baseLang !in baseLangs) {
            baseLangs.add(baseLang)
        }
        index = baseLangs.indexOf(baseLang)
    }

    private fun getBaseLang() : BaseLang {
        return baseLangs[index]
    }
    /**
     * 将`Lang`转为字符串
     *
     * 调用此函数时，相当于调用了所有形参为默认值的`Lang.joinToString`方法
     *
     * @see Lang.joinToString
     * @return language_dialect_Script_REGION格式的字符串
     */
    override fun toString(): String = this.joinToString()
    /**
     * 求哈希值，计算方法为`Lang.index`×85
     *
     * @return `Lang.index`×85
     * @see Any.hashCode
     */
    override fun hashCode(): Int {
        return index *85
    }
    /**
     * 判断此`Lang`对象是否与另一对象相同。
     *
     * 判断方法为将另一对象强转为`Lang`对象，并判断此对象是否与另一对象的`Lang.index`相等。如果不相等或过程发生错误则返回`false`，否则返回`true`
     *
     * @param other 另一对象
     * @return 是否与另一对象相同
     */
    override fun equals(other: Any?): Boolean {
        try {
            val otherLang = other as Lang
            return this.index == otherLang.index
        }catch (e:Exception){
            return false
        }
    }
    /**
     * 将`Lang`对象格式化转为字符串
     *
     * 首先新建一个`MutableList`对象，然后按顺序遍历每一个形参`priority`的每一个标签，将他们经由`transform`格式化后，将标签对应的字符串添加进列表中，然后调用`MutableList.joinToString`将列表格式化后返回。
     *
     * @param priority 优先级（正顺序）
     * @param separator 中缀
     * @param prefix 前缀
     * @param postfix 后缀
     * @param transform 格式化方法
     * @receiver `Lang`对象
     * @return `Lang`对象格式化后的字符串
     * @see MutableList.joinToString
     */
    fun joinToString(priority: MatchPriority = MatchPriority.DEFAULT, separator: String = "_", prefix: String = "", postfix: String = "", transform:(str :String)->String = {it}) : String{
        val stringList :MutableList<String> = mutableListOf();

        for (tag in priority.tagList){
            when(tag){
                PRIVATE -> {
                    for (string in this.getPrivates()){
                        if (string.isNotBlank())
                            stringList += transform(string)
                    }
                    continue
                }
                else -> {
                    if (this[tag].isNotBlank())
                        stringList += transform(this[tag])
                }
            }
        }

        return stringList.joinToString (separator = separator,prefix = prefix, postfix = postfix)
    }
    /**
     * 通过除`Tag.PRIVATE`外的标签获得对应的字符串
     *
     * @param tag 标签
     * @throws IllegalArgumentException 当使用`Tag.PRIVATE`标签时抛出。要获得Private标签的内容，请使用`Lang.getPrivates`
     * @return 标签获得对应的字符串
     * @see Lang.getPrivates
     */
    @Throws(IllegalArgumentException::class)
    operator fun get(tag:Tag):String{
         val baseLang = this.getBaseLang()

        when(tag){
            LANGUAGE -> return baseLang.lang
            REGION -> return baseLang.region
            DIALECT -> return baseLang.dialect
            SCRIPT -> return  baseLang.script
            PRIVATE ->throw IllegalArgumentException("")//TODO: 完成LangStrings后在这个异常处使用
        }
    }

    /**
     * 获得Private标签的内容
     *
     * @return Private标签的内容
     */
    fun getPrivates() : Array<out String>{
        return this.getBaseLang().privates
    }

    /**
     * 将`Lang`对象转为`MutableMap<Tag,String>`
     *
     * @return `Lang`对应的`MutableMap<Tag,String>`
     */
    fun toMutableMap() : MutableMap<Tag,String> {
        val result = mutableMapOf(
            LANGUAGE to this[LANGUAGE],
            DIALECT to this[DIALECT],
            SCRIPT to this[SCRIPT],
            REGION to this[REGION]
        )
        for (private in getPrivates())
            result += PRIVATE to private

        return result
    }

    /**
     * 将`Lang`对象转为`Locale`对象，<b style="color:darkred">私有标签会被忽略</b>
     *
     * @return 无私有标签的`Lang`对象对应的`Locale`对象
     */
    fun toLocale():Locale{
        return Locale.Builder().setLanguage(this[LANGUAGE]).setVariant(this[DIALECT]).setScript(this[SCRIPT]).setRegion(this[REGION]).build()
    }
}

private data class BaseLang (val lang:String = "",val dialect:String = "",val script:String = "",val region:String = "",val privates:Array<out String> = emptyArray()) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BaseLang) return false

        if (lang != other.lang) return false
        if (dialect != other.dialect) return false
        if (script != other.script) return false
        if (region != other.region) return false
        if (!privates.contentEquals(other.privates)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = lang.hashCode()
        result = 31 * result + dialect.hashCode()
        result = 31 * result + script.hashCode()
        result = 31 * result + region.hashCode()
        result = 31 * result + privates.contentHashCode()
        return result
    }
}