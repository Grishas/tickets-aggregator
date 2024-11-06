package com.ctnl.events.merge;

public enum MergeDecision {
	Merge,
	Merged,
	Single,
	Error,
	MoreResultsThanMaxNumberOfSources, 
	ZeroResults,  
	NothingTodo, 
	MergeOverride, 
	DuplicateSourceInMergeResult
	
}
