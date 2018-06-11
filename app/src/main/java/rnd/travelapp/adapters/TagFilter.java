package rnd.travelapp.adapters;

import java.util.List;
import java.util.function.Function;

import rnd.travelapp.models.ReisModel;

public class TagFilter<T> implements Filter<T> {
    // Just an example, should be passed inline and removed from here.
    public static final Function<ReisModel, List<String>> REIS_MODEL_TAGS_EXTRACTOR = ReisModel::getTags;

    private final Function<T, List<String>> tagsExtractor;
    private String searchString;

    public TagFilter(Function<T, List<String>> tagsExtractor) {
        this(tagsExtractor, "");
    }

    public TagFilter(Function<T, List<String>> tagsExtractor, String searchString) {
        this.tagsExtractor = tagsExtractor;
        this.searchString = searchString.toLowerCase();
    }

    public void setSearchString(String value) {
        searchString = value.toLowerCase();
    }

    public String getSearchString() {
        return searchString;
    }

    @Override
    public boolean filter(T instance) {
        return tagsExtractor.apply(instance).stream().anyMatch(this::tagMatches);
    }

    private boolean tagMatches(String tag) {
        return tag.toLowerCase().contains(this.searchString);
    }
}
