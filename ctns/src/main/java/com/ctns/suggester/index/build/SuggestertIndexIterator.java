package com.ctns.suggester.index.build;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

import org.apache.lucene.search.suggest.InputIterator;
import org.apache.lucene.util.BytesRef;

import com.ctnc.SuggesterDto;

class SuggestertIndexIterator implements InputIterator{
	
	private Iterator<SuggesterDto> suggestersIterator;
	
	private SuggesterDto currentSuggester;
	
	SuggestertIndexIterator(Iterator<SuggesterDto> suggestersIterator) {
		this.suggestersIterator = suggestersIterator;
	}
	
	public Comparator<BytesRef> getComparator() {
        return null;
    }
	
	public boolean hasPayloads() {
		return false;
	}
	
	public boolean hasContexts() {
		return false;
	}
	
	// This method returns the contexts for the record, which we can
    // use to restrict suggestions.  In this example we use the
    // regions in which a product is sold.
    public Set<BytesRef> contexts() {
//        try {
//            Set<BytesRef> regions = new HashSet<BytesRef>();
//            for (String region : currentEvent.regions) {
//                regions.add(new BytesRef(region.getBytes("UTF8")));
//            }
//            return regions;
//        } catch (UnsupportedEncodingException e) {
//            throw new Error("Couldn't convert to UTF-8");
//        }
    	return null;
    }

    // This method helps us order our suggestions.  In this example we
    // use the number of products of this type that we've sold.
    public long weight() {
        //return this.currentEvent.getNumberSold();
    	return this.currentSuggester.getWeight();
    }
	
	// This method needs to return the key for the record; this is the
    // text we'll be autocompleting against.
	public BytesRef next() throws IOException 
	{	
		if (suggestersIterator.hasNext()) 
		{
            currentSuggester = suggestersIterator.next();
            try 
            {
                return new BytesRef(currentSuggester.getKeyword().getBytes("UTF8"));
            } 
            catch (UnsupportedEncodingException e) {
                throw new Error("Couldn't convert to UTF-8");
            }
        } 
		else {
            return null;
        }
	}
	
	// This method returns the payload for the record, which is
    // additional data that can be associated with a record and
    // returned when we do suggestion lookups.  In this example the
    // payload is a serialized Java object representing our product.
    public BytesRef payload() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(currentSuggester);
            out.close();
            return new BytesRef(bos.toByteArray());
        } catch (IOException e) {
        	e.printStackTrace();
            throw new Error("Well that's unfortunate.");
        }
    }
}

