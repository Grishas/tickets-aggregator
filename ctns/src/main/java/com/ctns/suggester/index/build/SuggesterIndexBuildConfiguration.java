package com.ctns.suggester.index.build;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.shingle.ShingleAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.spell.Dictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.search.suggest.FileDictionary;
import org.apache.lucene.search.suggest.analyzing.AnalyzingInfixSuggester;
import org.apache.lucene.search.suggest.analyzing.FreeTextSuggester;
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

import com.ctnc.shared.IndexKey;
import com.ctnl.Resources;

@Configuration
@PropertySource("classpath:suggester.index.build.configuration")
public class SuggesterIndexBuildConfiguration {
	
	private int minPrefixChars = 4; 

	@Autowired
	private Environment environment;	

	@Bean
	@Scope("singleton")
	public SuggesterIndexBuildService getSuggesterIndexBuildService(){		
		return new SuggesterIndexBuildService();
	}
	
	@Bean
	@Scope("singleton")
	public SuggesterIndexBuildProperties getSuggesterIndexBuildProperties(){	
		SuggesterIndexBuildProperties properties = new SuggesterIndexBuildProperties();
		properties.setBaseFolderPath(this.environment.getProperty("dataRootPath"));
		return properties;
	}
	
	@Bean(destroyMethod="destroy")
	@Scope("singleton")
	public SuggesterIndexBuild getSuggesterIndexBuild(){
		return new SuggesterIndexBuild();
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
	@Scope("singleton")
	@Qualifier("AnalyzingInfixSuggesterEventName")
	public AnalyzingInfixSuggester getAnalyzingInfixSuggesterEventName() throws SuggesterIndexBuildException{
		
		String suggesterIndexDirectory = 
				Resources.getSuggesterIndexFolder(
						this.getSuggesterIndexBuildProperties().getBaseFolderPath());
		
		Directory directory = null;
		try {
			directory = FSDirectory.open( com.ctnu.io.UtilsIO.getPath(suggesterIndexDirectory) );
		} catch (IOException e) {
			throw new SuggesterIndexBuildException(e);
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
			throw new SuggesterIndexBuildException(e);
		}
		
		return analyzingInfixSuggester;
	}
	
	@Qualifier(value="AnalyzingInfixSuggesterVenueName")
	@Bean
	@Scope("singleton")
	public AnalyzingInfixSuggester getAnalyzingInfixSuggesterVenueName() throws SuggesterIndexBuildException{
				
		String suggesterIndexDirectory = 
				Resources.getSuggesterByVenueNameIndexFolder(
						this.getSuggesterIndexBuildProperties().getBaseFolderPath());

		Directory directory = null;
		try {
			directory = FSDirectory.open( com.ctnu.io.UtilsIO.getPath(suggesterIndexDirectory) );
		} catch (IOException e) {
			throw new SuggesterIndexBuildException(e);
		}
		
		//Analyzer  indexAnalyzer 			= new EnglishAnalyzer();
		//Analyzer  queryAnalyzer 			= new EnglishAnalyzer();
		
		
		//Analyzer  indexAnalyzer 			= new StandardAnalyzer();
		//Analyzer  queryAnalyzer 			= new StandardAnalyzer();
		
		Analyzer  indexAnalyzer 			= new SimpleAnalyzer();
		Analyzer  queryAnalyzer 			= new SimpleAnalyzer();
		
		
		AnalyzingInfixSuggester analyzingInfixSuggester;
		try {
			analyzingInfixSuggester = new AnalyzingInfixSuggester( directory, indexAnalyzer , queryAnalyzer , minPrefixChars , true);
		} catch (IOException e) {
			throw new SuggesterIndexBuildException(e);
		}
		
		return analyzingInfixSuggester;
	}
	
	@Qualifier(value="AnalyzingInfixSuggesterPerformerName")
	@Bean
	@Scope("singleton")
	public AnalyzingInfixSuggester getAnalyzingInfixSuggesterPerformerName() throws SuggesterIndexBuildException{
		
		String suggesterIndexDirectory = 
				Resources.getSuggesterByPerformerNameIndexFolder(
						this.getSuggesterIndexBuildProperties().getBaseFolderPath());


		
		Directory directory = null;
		try {
			directory = FSDirectory.open( com.ctnu.io.UtilsIO.getPath(suggesterIndexDirectory) );
		} catch (IOException e) {
			throw new SuggesterIndexBuildException(e);
		}
		
		//Analyzer  indexAnalyzer 			= new EnglishAnalyzer();
		//Analyzer  queryAnalyzer 			= new EnglishAnalyzer();
	
//		Analyzer  indexAnalyzer 			= new StandardAnalyzer();
//		Analyzer  queryAnalyzer 			= new StandardAnalyzer();

		Analyzer  indexAnalyzer 			= new SimpleAnalyzer();
		Analyzer  queryAnalyzer 			= new SimpleAnalyzer();
		
		
		AnalyzingInfixSuggester analyzingInfixSuggester;
		try {
			analyzingInfixSuggester = new AnalyzingInfixSuggester( directory, indexAnalyzer , queryAnalyzer , minPrefixChars , true);
		} catch (IOException e) {
			throw new SuggesterIndexBuildException(e);
		}
		
		return analyzingInfixSuggester;
	}
	
	@Qualifier(value="AnalyzingInfixSuggesterLocationName")
	@Bean
	@Scope("singleton")
	public AnalyzingInfixSuggester getAnalyzingInfixSuggesterLocationName() throws SuggesterIndexBuildException{
				
		String suggesterIndexDirectory = 
				Resources.getSuggesterByLocationNameIndexFolder(
						this.getSuggesterIndexBuildProperties().getBaseFolderPath());

		Directory directory = null;
		try {
			directory = FSDirectory.open( com.ctnu.io.UtilsIO.getPath(suggesterIndexDirectory) );
		} catch (IOException e) {
			throw new SuggesterIndexBuildException(e);
		}
		
		//Analyzer  indexAnalyzer 			= new EnglishAnalyzer();
		//Analyzer  queryAnalyzer 			= new EnglishAnalyzer();
		
		//Analyzer  indexAnalyzer 			= new StandardAnalyzer();
		//Analyzer  queryAnalyzer 			= new StandardAnalyzer();
		
		Analyzer  indexAnalyzer 			= new SimpleAnalyzer();
		Analyzer  queryAnalyzer 			= new SimpleAnalyzer();
		
		AnalyzingInfixSuggester analyzingInfixSuggester;
		try {
			analyzingInfixSuggester = new AnalyzingInfixSuggester( directory , indexAnalyzer , queryAnalyzer , minPrefixChars , true );
		} catch (IOException e) {
			throw new SuggesterIndexBuildException(e);
		}
		
		return analyzingInfixSuggester;
	}
	
	@Bean
	@Scope("prototype")
	public Dictionary getDictionary(String path) throws SuggesterIndexBuildException
	{
		 Reader reader = null;
		 try
		 {
			 reader = new FileReader(path);		
		 } 
		 catch (FileNotFoundException e) {
				throw new SuggesterIndexBuildException(e);
		 }
		 
		 Dictionary dictionary = new FileDictionary(reader);
		 return dictionary;
	}
	
	@Bean
	@Scope("prototype")
	public IndexWriterConfig getIndexWriterConfig()
	{
		Analyzer analyzer = new ShingleAnalyzerWrapper(new StandardAnalyzer(),5);
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
		indexWriterConfig.setOpenMode(OpenMode.CREATE);
		return indexWriterConfig;
	}
	
/*
	 			writer =        new IndexWriter(dir,

                new ShingleAnalyzerWrapper(new StandardAnalyzer(

                Version.LUCENE_CURRENT,

                Collections.emptySet()),3),

                false,

                IndexWriter.MaxFieldLength.UNLIMITED);
*/
	
	
	
	
	@Bean
	@Scope("prototype")
	public Directory getDirectory(IndexKey indexKey) throws SuggesterIndexBuildException
	{
		String suggesterIndexSpellFolder = 
				Resources.getSuggesterIndexSpellFolder(indexKey,
						this.getSuggesterIndexBuildProperties().getBaseFolderPath());

		Directory directory = null;
		try {
			directory = MMapDirectory.open( com.ctnu.io.UtilsIO.getPath(suggesterIndexSpellFolder) );
		} catch (IOException e) {
			throw new SuggesterIndexBuildException(e);
		}
		
		return directory;
	}
	
	@Bean
	@Scope("prototype")
	public SpellChecker getSpellChecker(IndexKey indexKey) throws SuggesterIndexBuildException{
		
		Directory directory = this.getDirectory(indexKey);
		
		SpellChecker spellChecker = null;
		try {
			spellChecker = new SpellChecker(directory);
		} catch (IOException e) {
			throw new SuggesterIndexBuildException(e);
		}
		
		return spellChecker;
	}	
}





