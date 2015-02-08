package com.nikunj.nvideoplayer;

import android.content.SearchRecentSuggestionsProvider;

public class SearchSuggestionProvider extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = "com.nikunj.SearchSuggestionProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public SearchSuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}