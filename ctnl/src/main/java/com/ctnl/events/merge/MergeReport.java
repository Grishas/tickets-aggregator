package com.ctnl.events.merge;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.Query;

import com.ctnc.Constants;
import com.ctnc.Event;
import com.ctnu.environment.Environment;

public class MergeReport {

	private Logger logger = Logger.getLogger(Merge.class);

	public void format(
			MergeDecision decision, 
			List<Document> documents , 
			Query query ,
			Event event ,
			StringBuilder report ){

		report.append("~~~"+decision.name()+"~~~"+ Environment.getLineSeparator());
		report.append("Results number : "+documents.size() + Environment.getLineSeparator());
		report.append("Triggered by event id : "+event.getInternalId() + Environment.getLineSeparator()); 
		report.append("Query : " + query + Environment.getLineSeparator());
		
		for(Document document : documents){
			report.append(
			"[Event id]"+document.getField(Constants.ID).stringValue()+","
			+document.getField(Constants.SOURCE).stringValue()+","		
			+"[Internal id]"+document.getField(Constants.EVENT_INTERNAL_ID).stringValue()
			+",[Name]"+document.getField(Constants.EVENT_NAME).stringValue()+","
			+document.getField(Constants.DISPLAY_DATE).stringValue()+","
			+document.getField(Constants.SEGMENT_KEY).stringValue()
			+",[Venue]"+document.getField(Constants.VENUE_NAME).stringValue()
			+",[City]"+document.getField(Constants.CITY_NAME).stringValue()
			+",[Performers]");
			
			for(IndexableField indexableField : document.getFields(Constants.PERFORMER_NAME)){
				report.append(indexableField.stringValue()+",");
			}
			report.append(Environment.getLineSeparator());	
		}
	}

	public void report(
			int sourcesNumber, 
			Map<Long, Long> invertMergeIndex,
			Map<Long, List<Long>> mergeIndex, 
			List<Long> singleIndex,
			List<Long> errorIndex, 
			
			int mergeCounter, 
			int mergeDifferentCityCounter,
			int notMergeDifferentCityCounter,
			int singleCounter,
			int mergedCounter, 
			int mergeOverrideCounter, 
			int errorCounter,
			int moreResultsThanMaxNumberOfSourcesCounter,
			int zeroResultsCounter, 
			int duplicateSourceInMergeCounter,
			int nothingTodoCounter, 
			int totalEventsCounter,
			
			StringBuilder mergeReport, 
			StringBuilder mergeDifferentCity,
			StringBuilder notMergeDifferentCity,
			StringBuilder singleReport,
			StringBuilder mergedReport, 
			StringBuilder mergeOverrideReport,
			StringBuilder errorReport,
			StringBuilder moreResultsThanMaxNumberOfSourcesReport,
			StringBuilder zeroResultsReport,
			StringBuilder duplicateSourceInMergeResultReport,
			StringBuilder nothingTodoReport) {

		logger.info(Environment.getLineSeparator()+"@@@Detail Merge Report@@@");
		
		logger.info("^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~");
		
		logger.info("@@@ merge report @@@"+mergeReport.toString());
		
		logger.info("^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~");
		
		logger.info("@@@ merge different city @@@"+mergeDifferentCity.toString());
		
		logger.info("^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~");
		
		logger.info("@@@ not merge different city @@@"+notMergeDifferentCity.toString());
		
		logger.info("^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~");
		
		logger.info("@@@ single report @@@"+singleReport.toString());
		
		logger.info("^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~");
		
		logger.info("@@@ merged report @@@"+mergedReport.toString());
		
		logger.info("^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~");
		
		logger.info("@@@ merge override report @@@"+mergeOverrideReport.toString());
		
		logger.info("^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~");
		
		logger.info("@@@ error report @@@"+errorReport.toString());
		
		logger.info("^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~");
		
		logger.info("@@@ more results than max number of Sources report @@@"+moreResultsThanMaxNumberOfSourcesReport.toString());
		
		logger.info("^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~");
		
		logger.info("@@@ zero results report @@@"+zeroResultsReport.toString());
		
		logger.info("^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~");
		
		logger.info("@@@ duplicate source in merge result report @@@"+duplicateSourceInMergeResultReport.toString());
		
		logger.info("^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~");
		
		logger.info("@@@ nothing todo report @@@"+nothingTodoReport.toString());
		
		logger.info("^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~");
		
		logger.info("@@@End Detail Merge Report@@@");
		
		//***************************************************************************************************************
		
		logger.info(Environment.getLineSeparator()+"@@@Summary Merge Report@@@");
		
		logger.info(Environment.getLineSeparator()+"Sources number : "+sourcesNumber);
		logger.info(Environment.getLineSeparator()+"Invert merge index["+invertMergeIndex.size()+"] : "+invertMergeIndex);
		//logger.trace("Merge index["+mergeIndex.size()+"] : "+mergeIndex);
		//logger.trace("Single index["+singleIndex.size()+"] : "+singleIndex);
		//logger.info("Error index["+errorIndex.size()+"] : "+errorIndex);
				
		logger.info(Environment.getLineSeparator()+"Total events counter : "+totalEventsCounter);
		logger.info("Merge counter : "+mergeCounter);
		logger.info("Merge different city : "+mergeDifferentCityCounter);
		logger.info("Not merge different city : "+notMergeDifferentCityCounter);
		
		logger.info("Single counter : "+singleCounter);
		logger.info("Merged counter : "+mergedCounter);
		logger.info("Merge override counter : "+mergeOverrideCounter);
		logger.info("Error counter : "+errorCounter);
		logger.info("More results than max number of sources counter : "+moreResultsThanMaxNumberOfSourcesCounter);
		logger.info("Zero results counter : "+zeroResultsCounter);
		logger.info("Duplicate source in merge counter : "+duplicateSourceInMergeCounter);
		logger.info("Nothing todo counter : "+nothingTodoCounter);
		
		logger.info("@@@End Summary Merge Report@@@");

		//***************************************************************************************************************
		
		logger.info(Environment.getLineSeparator()+"@@@Reconciliation Merge Report@@@");
		
		int actualResult = 
				( mergeIndex.size() * sourcesNumber ) + 
				  singleIndex.size() + 
				  errorIndex.size()  +
				  zeroResultsCounter + 
				  duplicateSourceInMergeCounter
				;		
		
		if(actualResult==totalEventsCounter){
			logger.info("Reconciliation success.Actual result : "+actualResult+" vs Expected result : "+totalEventsCounter);	
		}
		else{
			logger.error("Reconciliation failure.Actual result : "+actualResult+" vs Expected result : "+totalEventsCounter);	
		}
		
		logger.info("@@@End Reconciliation Merge Report@@@"+Environment.getLineSeparator());
		
	}
	
}
