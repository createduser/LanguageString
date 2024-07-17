package io.github.createduser.lang_string;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
* This tool class is used to allow Java developers to better use priorities.
 * <p> Of course, Kotlin developers can also use this class library, but it is not as intuitive as directly using the infix functions MatchPriority.next(Tag) and Tag.next(Tag)
 *
 * @see MatchPriorityKt
 */
public class MatchPriorityUtil {
    @NotNull
    public static MatchPriority create(@NotNull Tag... tags){
        List<Tag> tagList = Arrays.asList(tags);

        MatchPriority result = new MatchPriority();

        result.setTagList(tagList);

        return result;
    }
    @NotNull
    public static MatchPriority create(@NotNull MatchPriority priority, @NotNull Tag... tags){
        MatchPriority result = MatchPriorityKt.next(priority,tags[0]);

        for (int index = 1;index < tags.length;index++){
            MatchPriorityKt.next(result,tags[index]);
        }

        return result;
    }
}
