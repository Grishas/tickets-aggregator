package com.ctns.suggester.index.build;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.shingle.ShingleAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.spell.Dictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.search.suggest.FileDictionary;
import org.apache.lucene.search.suggest.analyzing.AnalyzingInfixSuggester;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;

import com.ctnc.SuggesterDto;
import com.ctnc.shared.IndexKey;
import com.ctnl.Resources;

//http://stackoverflow.com/questions/24968697/how-to-implements-auto-suggest-using-lucenes-new-analyzinginfixsuggester-api
//http://blog.mikemccandless.com/2014/11/apache-lucene-500-is-coming.html
//https://today.java.net/pub/a/today/2005/08/09/didyoumean.html
//http://puneetkhanal.blogspot.co.il/2013/04/simple-auto-suggester-using-lucene-41.html
//http://stackoverflow.com/questions/24968697/how-to-implements-auto-suggest-using-lucenes-new-analyzinginfixsuggester-api
//http://blog.mikemccandless.com/2012/09/lucenes-new-analyzing-suggester.html	

public class SuggesterIndexBuild {
	
	private Logger logger = Logger.getLogger(SuggesterIndexBuild.class);

	@Autowired
	@Qualifier(value="AnalyzingInfixSuggesterPerformerName")
	private AnalyzingInfixSuggester analyzingInfixSuggesterPerformerName;
	
	@Autowired
	@Qualifier(value="AnalyzingInfixSuggesterEventName")
	private AnalyzingInfixSuggester analyzingInfixSuggesterEventName;
	
	@Autowired
	@Qualifier(value="AnalyzingInfixSuggesterVenueName")
	private AnalyzingInfixSuggester analyzingInfixSuggesterVenueName;
	
	@Autowired
	@Qualifier(value="AnalyzingInfixSuggesterLocationName")
	private AnalyzingInfixSuggester analyzingInfixSuggesterLocationName;
	
	@Autowired 
	private SuggesterIndexBuildProperties properties;
	
	public void build(List<SuggesterDto> suggesters, IndexKey indexKey) throws SuggesterIndexBuildException
	{
		switch (indexKey) {
		case event:

			try {
				this.analyzingInfixSuggesterEventName.build(new SuggestertIndexIterator(suggesters.iterator()));
			} catch (IOException e) {
				throw new SuggesterIndexBuildException(e);
			}
			
			break;
		case venue:
			

			try {
				this.analyzingInfixSuggesterVenueName.build(new SuggestertIndexIterator(suggesters.iterator()));
			} catch (IOException e) {
				throw new SuggesterIndexBuildException(e);
			}
			
			
			break;
		case performer:
			
			try {
				this.analyzingInfixSuggesterPerformerName.build(new SuggestertIndexIterator(suggesters.iterator()));
			} catch (IOException e) {
				throw new SuggesterIndexBuildException(e);
			}
			
			break;
		case location:
			
			try {
				this.analyzingInfixSuggesterLocationName.build(new SuggestertIndexIterator(suggesters.iterator()));
			} catch (IOException e) {
				throw new SuggesterIndexBuildException(e);
			}
			
			break;
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public void destroy(){
		try {
			analyzingInfixSuggesterEventName.close();
			analyzingInfixSuggesterPerformerName.close();
			analyzingInfixSuggesterVenueName.close();
			analyzingInfixSuggesterLocationName.close();			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//spelling

	@Autowired private ApplicationContext context;
	
	public void buildSpell(IndexKey indexKey) throws SuggesterIndexBuildException 
	{
		Dictionary dictionary = 
				this.context.getBean(
						Dictionary.class,
								Resources.getPathToSuggesterSpellData(
								indexKey,
								this.properties.getBaseFolderPath()));
		
		IndexWriterConfig indexWriterConfig = this.context.getBean( IndexWriterConfig.class );
		
		SpellChecker spellChecker = this.context.getBean( SpellChecker.class , indexKey );
		
		try {
			spellChecker.indexDictionary(dictionary,indexWriterConfig,true);
		} catch (IOException e) {
			throw new SuggesterIndexBuildException();
		}
		finally 
		{
			try {
				spellChecker.close();
			} catch (IOException e) {
				logger.error(e);
			}
		}
	}
}
