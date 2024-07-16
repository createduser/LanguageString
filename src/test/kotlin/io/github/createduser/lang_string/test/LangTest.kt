package io.github.createduser.lang_string.test

import io.github.createduser.lang_string.Lang
import io.github.createduser.lang_string.Langs
import java.util.Locale
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class LangTest {
    @Test
    fun equalsAndConstructorTest(){
        val zh_cmn_Hans_CN = Lang("zh","cmn","Hans","CN")
        assertEquals(zh_cmn_Hans_CN,Langs.zh_cmn_Hans_CN)
        assertFalse{ zh_cmn_Hans_CN.equals(1) }

        val zh_CN = Lang(lang = "zh", region = "CN")
        val China = Lang(Locale.CHINA)
        assertEquals(zh_CN,China)

        val langHere = Lang()
        val langHereFormLocale = Lang(Locale.getDefault())
        assertEquals(langHere,langHereFormLocale)
    }
    @Test
    fun getterAndToStringTest(){
        assertEquals(Langs.zh_cmn_Hans_CN.toString(),"zh_cmn_Hans_CN")
        assertEquals(Lang(lang = "zh", region = "CN").toString(),"zh_CN")
        assertEquals(Lang(lang = "zh", script = "hans", region = "CN").toString(),"zh_Hans_CN")
        assertEquals(Lang("zh","yue","hans","cn").toString(),"zh_yue_Hans_CN")
        assertEquals(Lang("ZH","YUE","HANT","HK").toString(),"zh_yue_Hant_HK")
        assertEquals(Lang("Zh","yUE","hAnT","mO").toString(),"zh_yue_Hant_MO")
    }
    @Test
    fun hashCodeTest(){
        assertEquals(Langs.zh_cmn_Hans_CN.hashCode(),Lang("zh","cmn","Hans","CN").hashCode())
    }
}