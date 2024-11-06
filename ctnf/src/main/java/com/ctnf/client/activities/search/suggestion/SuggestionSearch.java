package com.ctnf.client.activities.search.suggestion;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

public class SuggestionSearch implements IsSerializable, Suggestion {

	
	private String display;
    private String replacement;

    // Required for IsSerializable to work
    public SuggestionSearch() {}

    // Convenience method for creation of a suggestion
    public SuggestionSearch(String display, String replacement) 
    {
        this.display = display;
        this.replacement = replacement;
    }

    public String getDisplayString() 
    {
        return display;
    }

    public String getReplacementString() 
    {
        return replacement;
    }

}
