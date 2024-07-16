package io.github.createduser.lang_string.test

import com.sun.scenario.effect.impl.prism.ps.PPSRenderer
import io.github.createduser.lang_string.*
import io.github.createduser.lang_string.Tag.*
import java.util.Locale
import kotlin.test.Test
import kotlin.test.assertEquals

class LangStringsTest {
    companion object Companion{
        var testLangStrings = LangStrings("匹配到了root")
    }
    init {
        testLangStrings[Langs.zh_cmn_Hans_CN] = "匹配到了zh_cmn_Hans_CN"
        testLangStrings[Lang("zh","","Hans","CN")] = "匹配到了zh_Hans_CN"
        testLangStrings[Lang("zh","cmn","","CN")] = "匹配到了zh_cmn_CN"
        testLangStrings[Lang(lang = "zh", region = "CN")] = "匹配到了zh_CN"
        testLangStrings[Lang("zh")] = "匹配到了zh"
        testLangStrings[Lang(lang = "zh", region = "HK")] = "匹配到了zh_HK"
    }
    @Test
    fun getTest(){
        assertEquals("匹配到了zh_cmn_Hans_CN", testLangStrings[Langs.zh_cmn_Hans_CN])
        assertEquals("匹配到了zh", testLangStrings[Lang("zh","TW")])
        assertEquals("匹配到了zh", testLangStrings[Langs.zh_cmn_Hant_TW])
        assertEquals("匹配到了zh_cmn_CN", testLangStrings[Lang("zh","cmn","Hant","CN"), LANGUAGE next DIALECT next REGION next SCRIPT next PRIVATE,true,false])
        assertEquals("匹配到了zh_CN", testLangStrings[Lang("zh","yue","Hans","CN")])
        assertEquals("匹配到了zh_HK", testLangStrings[Lang("zh","yue","Hant","HK")])
    }
}