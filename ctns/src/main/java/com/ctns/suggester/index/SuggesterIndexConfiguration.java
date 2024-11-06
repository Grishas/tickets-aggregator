package com.ctns.suggester.index;
import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.spell.Dictionary;
import org.apache.lucene.search.spell.LuceneDictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.search.suggest.analyzing.AnalyzingInfixSuggester;
import org.apache.lucene.search.suggest.analyzing.FreeTextSuggester;
import org.apache.lucene.search.suggest.analyzing.FuzzySuggester;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.MMapDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import com.ctnc.Constants;
import com.ctnc.shared.IndexKey;
import com.ctnl.Resources;

@Configuration
@PropertySource("classpath:suggester.index.configuration")
public class SuggesterIndexConfiguration {
	
	private int minPrefixChars = 4; 
	@Autowired
	private Environment environment;	
	
	@Bean
	@Scope("singleton")
	public SuggesterIndexService getSuggesterIndexService(){		
		return new SuggesterIndexService();
	}
	
	@Bean
	@Scope("singleton")
	public SuggesterIndexProperties getSuggesterIndexProperties(){		
		
		SuggesterIndexProperties properties = new SuggesterIndexProperties();
		
		properties.setBaseFolderPath(this.environment.getProperty("dataRootPath"));
		
		properties.setSuggesterMaxNumberOfItemsToReturn(
				this.environment.getProperty("ctns.suggester.max.number.of.items.to.return",Integer.class));
		return properties;
	}
	
	@Bean
	//@Scope("prototype")
	@Scope("singleton")
	public SuggesterIndexLookup getSuggesterIndexLookup(){
		return new SuggesterIndexLookup();
	}
	
	//bugs / fixes 
	//http://jirasearch.mikemccandless.com/search.py?index=jira
	//falback like google 
	//Finding long tail suggestions using Lucene’s new FreeTextSuggester
	//http://www.javacodegeeks.com/2014/01/finding-long-tail-suggestions-using-lucenes-new-freetextsuggester.html
	//AnalyzingInfixSuggester needs duplicate handling
	//https://issues.apache.org/jira/browse/LUCENE-6336	
	//https://github.com/DmitryKey/luke/releases/
	//http://java.dzone.com/articles/three-exciting-lucene-features
	@Bean
	@Qualifier(value="AnalyzingInfixSuggesterEventName")
	@Scope("singleton")
	public AnalyzingInfixSuggester getAnalyzingInfixSuggesterEventName() throws SuggesterIndexException{
		
		String suggesterIndexDirectory = 
				Resources.getSuggesterIndexFolder(
						this.getSuggesterIndexProperties().getBaseFolderPath());
		
		Directory directory = null;
		try {
			directory = MMapDirectory.open( com.ctnu.io.UtilsIO.getPath(suggesterIndexDirectory) );
		} catch (IOException e) {
			throw new SuggesterIndexException(e);
		}
		
		//Analyzer  indexAnalyzer 			= new EnglishAnalyzer();
		//Analyzer  queryAnalyzer 			= new EnglishAnalyzer();
		
		//Analyzer  indexAnalyzer 			= new StandardAnalyzer();
		//Analyzer  queryAnalyzer 			= new StandardAnalyzer();
		
		Analyzer  indexAnalyzer 			= new SimpleAnalyzer();
		Analyzer  queryAnalyzer 			= new SimpleAnalyzer();
		
		
		AnalyzingInfixSuggester analyzingInfixSuggester;
		try {
			analyzingInfixSuggester = new AnalyzingInfixSuggester( directory, indexAnalyzer , queryAnalyzer ,minPrefixChars,true);
		} catch (IOException e) {
			throw new SuggesterIndexException(e);
		}
		
		return analyzingInfixSuggester;
	}
	
	@Qualifier(value="AnalyzingInfixSuggesterVenueName")
	@Bean
	@Scope("singleton")
	public AnalyzingInfixSuggester getAnalyzingInfixSuggesterVenueName() throws SuggesterIndexException{
		
		String suggesterIndexDirectory = 
				Resources.getSuggesterByVenueNameIndexFolder(
						this.getSuggesterIndexProperties().getBaseFolderPath());
		
		Directory directory = null;
		try {
			directory = MMapDirectory.open( com.ctnu.io.UtilsIO.getPath(suggesterIndexDirectory) );
		} catch (IOException e) {
			throw new SuggesterIndexException(e);
		}
		
		//Analyzer  indexAnalyzer 			= new EnglishAnalyzer();
		//Analyzer  queryAnalyzer 			= new EnglishAnalyzer();
		
		//Analyzer  indexAnalyzer 			= new StandardAnalyzer();
		//Analyzer  queryAnalyzer 			= new StandardAnalyzer();
		
		Analyzer  indexAnalyzer 			= new SimpleAnalyzer();
		Analyzer  queryAnalyzer 			= new SimpleAnalyzer();
		
		
		AnalyzingInfixSuggester analyzingInfixSuggester;
		try {
			analyzingInfixSuggester = new AnalyzingInfixSuggester( directory, indexAnalyzer , queryAnalyzer ,minPrefixChars,true);
		} catch (IOException e) {
			throw new SuggesterIndexException(e);
		}
		
		return analyzingInfixSuggester;
	}
	
	@Bean
	@Qualifier(value="AnalyzingInfixSuggesterPerformerName")
	@Scope("singleton")
	public AnalyzingInfixSuggester getAnalyzingInfixSuggesterPerformerName() throws SuggesterIndexException{
		
		String suggesterIndexDirectory = 
				Resources.getSuggesterByPerformerNameIndexFolder(
						this.getSuggesterIndexProperties().getBaseFolderPath());
		
		Directory directory = null;
		try {
			directory = MMapDirectory.open( com.ctnu.io.UtilsIO.getPath(suggesterIndexDirectory) );
		} catch (IOException e) {
			throw new SuggesterIndexException(e);
		}
		
		//Analyzer  indexAnalyzer 			= new EnglishAnalyzer();
		//Analyzer  queryAnalyzer 			= new EnglishAnalyzer();
		
		//Analyzer  indexAnalyzer 			= new StandardAnalyzer();
		//Analyzer  queryAnalyzer 			= new StandardAnalyzer();
		
		Analyzer  indexAnalyzer 			= new SimpleAnalyzer();
		Analyzer  queryAnalyzer 			= new SimpleAnalyzer();
		
		
		AnalyzingInfixSuggester analyzingInfixSuggester;
		try {
			analyzingInfixSuggester = new AnalyzingInfixSuggester( directory, indexAnalyzer , queryAnalyzer , minPrefixChars , true );
		} catch (IOException e) {
			throw new SuggesterIndexException(e);
		}
		
		return analyzingInfixSuggester;
	}
	
	
	@Bean
	@Qualifier(value="AnalyzingInfixSuggesterLocationName")
	@Scope("singleton")
	public AnalyzingInfixSuggester getAnalyzingInfixSuggesterLocationName() throws SuggesterIndexException{
		
		String suggesterIndexDirectory = 
				Resources.getSuggesterByLocationNameIndexFolder(
						this.getSuggesterIndexProperties().getBaseFolderPath());
		
		Directory directory = null;
		try {
			directory = MMapDirectory.open( com.ctnu.io.UtilsIO.getPath(suggesterIndexDirectory) );
		} catch (IOException e) {
			throw new SuggesterIndexException(e);
		}
		
		//Analyzer  indexAnalyzer 			= new EnglishAnalyzer();
		//Analyzer  queryAnalyzer 			= new EnglishAnalyzer();
		
		//Analyzer  indexAnalyzer 			= new StandardAnalyzer();
		//Analyzer  queryAnalyzer 			= new StandardAnalyzer();
		
		Analyzer  indexAnalyzer 			= new SimpleAnalyzer();
		Analyzer  queryAnalyzer 			= new SimpleAnalyzer();
		
		
		AnalyzingInfixSuggester analyzingInfixSuggester;
		try {
			analyzingInfixSuggester = new AnalyzingInfixSuggester( directory, indexAnalyzer , queryAnalyzer , minPrefixChars ,true);
		} catch (IOException e) {
			throw new SuggesterIndexException(e);
		}
		
		return analyzingInfixSuggester;
	}
	

	@Bean
	@Scope("singleton")
	public FuzzySuggester getFuzzySuggester() throws SuggesterIndexException{
		
		FuzzySuggester fuzzySuggester;
		
		fuzzySuggester = new FuzzySuggester( new EnglishAnalyzer());
		
		return fuzzySuggester;
	}
	
	
	@Qualifier(value="SpellCheckerEventName")
	@Bean
	@Scope("singleton")
	public SpellChecker getSpellCheckerEventName() throws SuggesterIndexException
	{
		return this.getSpellChecker(IndexKey.event);
	}
	
	@Qualifier(value="SpellCheckerPerformerName")
	@Bean
	@Scope("singleton")
	public SpellChecker getSpellCheckerPerformerName() throws SuggesterIndexException
	{
		return this.getSpellChecker(IndexKey.performer);
	}
	
	@Qualifier(value="SpellCheckerVenueName")
	@Bean
	@Scope("singleton")
	public SpellChecker getSpellCheckerVenueName() throws SuggesterIndexException
	{
		return this.getSpellChecker(IndexKey.venue);
	}
	
	@Qualifier(value="SpellCheckerLocationName")
	@Bean
	@Scope("singleton")
	public SpellChecker getSpellCheckerLocationName() throws SuggesterIndexException
	{
		return this.getSpellChecker(IndexKey.location);
	}
	
	private SpellChecker getSpellChecker(IndexKey indexKey) throws SuggesterIndexException
	{
		Directory directory = null;
		try {
			directory = MMapDirectory.open( 
					com.ctnu.io.UtilsIO.getPath(
							Resources.getSuggesterIndexSpellFolder(
									indexKey,this.getSuggesterIndexProperties().getBaseFolderPath())));
		} 
		catch (IOException e) {
			throw new SuggesterIndexException(e);
		}
		
		SpellChecker spellChecker = null;
		try {
			spellChecker = new SpellChecker(directory);
		} catch (IOException e1) {
			throw new SuggesterIndexException(e1);
		}
		
		return spellChecker;
	}
}

