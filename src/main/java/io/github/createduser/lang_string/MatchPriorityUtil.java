package io.github.createduser.lang_string;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * 这个工具类用于让Java开发者更好地使用优先级。
 * <p> 当然，Kotlin开发者也可以使用这个类库，只是没有直接使用中缀函数MatchPriority.next(Tag)和Tag.next(Tag)那样直观
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
