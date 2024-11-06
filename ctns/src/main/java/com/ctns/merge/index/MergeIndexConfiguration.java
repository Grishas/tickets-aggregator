package com.ctns.merge.index;
import java.io.IOException;
import java.nio.file.Paths;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import com.ctnc.Constants;
import com.ctnl.Resources;
import com.ctns.query.QueryBuilder;

@Configuration
@PropertySource("classpath:merge.index.configuration")
public class MergeIndexConfiguration {
	
	@Autowired
	private Environment environment;	

	@Bean
	@Scope("singleton")
	public Sort getSort()
	{
		SortField sortField=new SortField(Constants.DATE_SORT,SortField.Type.LONG);
	    Sort sort = new Sort(sortField);  
	    return sort;
	}
	
	@Bean
	@Scope("singleton")
	public IndexSearcher getIndexSearcher() throws MergeIndexException
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
													Resources.getMergeIndexData(
															this.getMergeIndexProperties().getBaseFolderPath()))));
		}
		catch (IOException e) {
			throw new MergeIndexException(e);
		}
		/*
		 IndexSearcher instances are completely thread safe, 
		 meaning multiple threads can call any of its methods, concurrently.
		 */
		return new IndexSearcher(indexReader);
	}
	
	@Bean
	@Scope("singleton")
	public MergeIndex getMergeIndex(){		
			return new MergeIndex();
	}	

	@Bean
	@Scope("singleton")
	public MergeIndexService getMergeIndexService(){		
		return new MergeIndexService();
	}
	
	@Bean
	@Scope("singleton")
	public MergeIndexProperties getMergeIndexProperties(){		
		MergeIndexProperties properties = new MergeIndexProperties();
		
		properties.setBaseFolderPath( this.environment.getProperty("dataRootPath") );
		
		Integer[] maxDistanceKmSteps = 
				this.environment.getProperty("ctns.merge.index.max.distance.km.steps",Integer[].class);
		properties.setMaxDistanceKmSteps(maxDistanceKmSteps);
		
		return properties;
	}	
	
	@Bean
	@Scope("singleton")
	public QueryBuilder getQueryBuilder(){		
		return new QueryBuilder();
	}
}

