package com.ctns.merge.index.build;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.ctnc.Event;

public class MergeIndexBuildService {

	private Logger logger = Logger.getLogger(MergeIndexBuildService.class);

	@Autowired
	private MergeIndexBuild mergeIndexBuild;
	
	@Autowired 
	private MergeIndexBuildProperties mergeBuildProperties;
	
    public MergeIndexBuildService(){}

	public void build(Map<String, List<Event>> events, Map<String, List<String>> identicalVenues, Map<String, Set<String>> identicalLocations) throws MergeIndexBuildException {	
		this.mergeIndexBuild.build(events,identicalVenues,identicalLocations);
    }	
}
