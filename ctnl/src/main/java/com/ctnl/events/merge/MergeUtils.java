package com.ctnl.events.merge;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;

import com.ctnc.Constants;

public class MergeUtils {

	public static boolean isAInB(List<Long> a, List<Long> b) {
		
		int counter = 0;
		
		for(Long a_ : a)
		{
			for(Long b_ : b)
			{
				if(a_.equals(b_))
				{
					counter++;
				}
			}
		}
		
		if(counter==a.size())
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public static List<Long> getCurrentResultsInternalIds(List<Document> documents){
		
		List<Long> result = new ArrayList<Long>(documents.size());
		
		for(Document document : documents){
			Long internalId = Long.valueOf(document.getField(Constants.EVENT_INTERNAL_ID).stringValue());
			result.add(internalId);
		}
		return result;
	}
	
	public static  boolean isDuplicateSourceInSameResult(List<Document> documents){
		List<String> help = new ArrayList<String>();
		for(Document document : documents){
			String source = document.getField(Constants.SOURCE).stringValue();
			//check for duplicate source in same results
			if(help.contains(source)==true){
				return true;
			}
			else{
				help.add(source);
			}
		}
		return false;
	}

	
}
