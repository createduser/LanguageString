package io.github.createduser.lang_string

class Langs {
    companion object Langs{
        @JvmField
        val root = Lang(lang = "", region = "", script = "", dialect = "")

        /**
         * 简体中文（中国大陆，普通话），对应`zh_cmn_Hans_CN`
         */
        @JvmField
        val zh_cmn_Hans_CN = Lang("zh","cmn","Hans","CN")
        /**
         * 中文（中国大陆），对应`zh_CN`
         */
        @JvmField
        val zh_CN = Lang("zh", region = "CN")

        /**
         * 繁体中文（中国台湾，普通话），对应`zh_cmn_Hant_TW`
         */
        @JvmField
        val zh_cmn_Hant_TW = Lang("zh","cmn","Hant","TW")
        /**
         * 中文（中国台湾），对应`zh_TW`
         */
        @JvmField
        val zh_TW = Lang("zh", region = "TW")
    }
}