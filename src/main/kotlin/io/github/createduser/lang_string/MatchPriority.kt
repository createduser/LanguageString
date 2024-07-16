package io.github.createduser.lang_string

class MatchPriority {
    var tagList :MutableList<Tag> = mutableListOf()

    companion object Companion{
        @JvmField
        val DEFAULT = Tag.LANGUAGE next Tag.DIALECT next Tag.SCRIPT next Tag.REGION next Tag.PRIVATE
    }

}
enum class Tag {
    LANGUAGE,DIALECT,SCRIPT,REGION,PRIVATE;
}

infix fun MatchPriority.next(tag: Tag):MatchPriority{
    tagList.add(tag)

    return this
}


infix fun Tag.next(tag: Tag):MatchPriority{
    val priority = MatchPriority()

    return priority next this next tag
}