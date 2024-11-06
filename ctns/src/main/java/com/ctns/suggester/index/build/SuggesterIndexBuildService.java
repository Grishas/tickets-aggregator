package com.ctns.suggester.index.build;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import com.ctnc.SuggesterDto;
import com.ctnc.shared.IndexKey;

public class SuggesterIndexBuildService {

	@Autowired
	private SuggesterIndexBuild suggesterIndexBuild;

    public SuggesterIndexBuildService(){}

	public void build(List<SuggesterDto> suggesters, IndexKey indexKey) throws SuggesterIndexBuildException {	
		this.suggesterIndexBuild.build( suggesters ,indexKey);
    }

	public void buildSpell(IndexKey indexKey) throws SuggesterIndexBuildException {
		suggesterIndexBuild.buildSpell(indexKey);
	}	
}
