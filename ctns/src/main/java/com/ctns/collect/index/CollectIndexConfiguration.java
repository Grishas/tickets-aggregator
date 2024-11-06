package com.ctns.collect.index;
import java.io.IOException;
import java.nio.file.Paths;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;

import com.ctnl.Resources;

@Configuration
@PropertySource("classpath:collect.index.configuration")
public class CollectIndexConfiguration {
	
	@Autowired
	private Environment environment;	

	@Bean
	@Scope("singleton")
	public IndexSearcher getIndexSearcher() throws CollectIndexException
	{
		/*
		 IndexReader instances are completely thread safe, 
		 meaning multiple threads can call any of its methods, concurrently. 
		 */
		IndexReader indexReader = null;

		try {
			indexReader = DirectoryReader.open(
									FSDirectory.open(
											Paths.get(
													Resources.getCollectIndexData(
															getCollectIndexProperties().getBaseFolderPath()))));
		}
		catch (IOException e) {
			throw new CollectIndexException(e);
		}
		/*
		 IndexSearcher instances are completely thread safe, 
		 meaning multiple threads can call any of its methods, concurrently.
		 */
		return new IndexSearcher(indexReader);
	}
	
	@Bean
	@Scope("singleton")
	public CollectIndex getCollectIndex(){		
			return new CollectIndex();
	}	

	@Bean
	@Scope("singleton")
	public CollectIndexService getCollectIndexService(){		
		return new CollectIndexService();
	}
	
	@Bean
	@Scope("singleton")
	public CollectIndexProperties getCollectIndexProperties(){		
		CollectIndexProperties properties = new CollectIndexProperties();
		properties.setBaseFolderPath(this.environment.getProperty("dataRootPath"));
		return properties;	
	}	
}

