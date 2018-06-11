package rnd.travelapp.adapters;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Simple tag based filter that checks if a search string is contained in any of the tags.
 * @param <T>
 */
public class TagFilter<T> implements Predicate<T> {
    private final Function<T, List<String>> tagsExtractor;
    private final String searchString;

    public TagFilter(Function<T, List<String>> tagsExtractor, String searchString) {
        this.tagsExtractor = tagsExtractor;
        this.searchString = searchString.toLowerCase();
    }

    @Override
    public boolean test(T instance) {
        return tagsExtractor.apply(instance).stream().anyMatch(this::tagMatches);
    }

    private boolean tagMatches(String tag) {
        return tag.toLowerCase().contains(this.searchString);
    }
}
