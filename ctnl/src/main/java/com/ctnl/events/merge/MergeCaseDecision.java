package com.ctnl.events.merge;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.lucene.document.Document;

import com.ctnc.Constants;
import com.ctnc.Event;

public class MergeCaseDecision {

	public MergeDecision decide(
			Event event,
			int sourcesNumber,
			List<Document> documents,
			Map<Long, List<Long>> mergedIndex,
			List<Long> singleIndex,
			Map<Long, Long> invertMergeIndex) throws MergeException {

			//single event case
			if ( documents.size() == 1  )
			{		
				//check if event is not merged event
				if( invertMergeIndex.containsKey( event.getInternalId() ) )
				{
					return MergeDecision.Merged;//already merged with diff parent
				}
				else
				{
					return MergeDecision.Single;//single event
				}
			}
			
			if( documents.size() == 0 )
			{
				return MergeDecision.ZeroResults;
			}
			
			//if find more results than maximum number of sources 
			if( documents.size() > sourcesNumber )
			{
				return MergeDecision.MoreResultsThanMaxNumberOfSources;
			}
			
			//merged case
			if ( ( documents.size() > 1 ) &&  ( documents.size() <= sourcesNumber ) )
			{	
				boolean haveDuplicateSource = MergeUtils.isDuplicateSourceInSameResult(documents);
				
				if(haveDuplicateSource==true)
				{
					return MergeDecision.DuplicateSourceInMergeResult;
				}
				
				if( invertMergeIndex.containsKey( event.getInternalId() ) )
				{
					//get root of previous event 
					Long previousMergeRoot = invertMergeIndex.get(event.getInternalId());
					//get all related events of previous event 
					List<Long> previousMergeRootEvents = mergedIndex.get(previousMergeRoot);

					//clone previous and add root to got full picture
					List<Long> previousMergeRootEventsTmp = new ArrayList<Long>(previousMergeRootEvents.size()+1);
					for(Long element : previousMergeRootEvents){
						previousMergeRootEventsTmp.add(new Long(element));
					}
					previousMergeRootEventsTmp.add(new Long(previousMergeRoot));
					//end clone

					List<Long> currentResultsInternalIds = MergeUtils.getCurrentResultsInternalIds(documents);
					//if current merge result is better/bigger
					if( documents.size() >  previousMergeRootEventsTmp.size()  )
					{							
						if( MergeUtils.isAInB(previousMergeRootEventsTmp,currentResultsInternalIds) == false )
						{
							throw new MergeException("Logical error.Can't find previous events in better merge");
						}
						
						return MergeDecision.MergeOverride;
					}
					else//in this case just validate and go back nothing to do.Current merge is not better than prev
					{
						if( MergeUtils.isAInB(currentResultsInternalIds,previousMergeRootEventsTmp)== false )
						{
							throw new MergeException("Logical error.Can't find previous events in new merge");
						}
						
						return MergeDecision.NothingTodo;
					}
				}
				else
				{
					return MergeDecision.Merge;
				}
			}
			
			return MergeDecision.Error;
		}
}
