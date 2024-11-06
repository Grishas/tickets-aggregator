package com.ctnb;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.eclipse.jetty.util.log.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import com.ctnl.Resources;
import com.ctnl.load.fs.LoadService;
import com.ctnu.UtilsException;
import com.ctnu.io.UtilsIO;

public class PageCacheService {

	private Logger logger = Logger.getLogger(PageCacheService.class);

	private @Autowired LoadService loadService;
	
	private Map<String,String> pageCacheMetadata = new ConcurrentHashMap<String,String>();
	
	protected void initPageCache()
	{
		this.pageCacheMetadata = this.loadService.getPageCacheMetadata();
		logger.info("Load cache pages metadata: "+this.pageCacheMetadata.toString() );

	}
	
	public String tryToGetPageFromCache(String key)
	{
		String pageMetadata = null;
		
		if(this.pageCacheMetadata.containsKey(key)){
			try {
				pageMetadata = UtilsIO.readAllBytes(pageCacheMetadata.get(key), "utf-8");
				logger.info("Found cache page for key: "+key );
			} catch (UtilsException e) {
				logger.error(e);
				return null;
			}
		}
		return pageMetadata;
	}
	
	public boolean addPageToCache(String key,String data)
	{
		String path = 
				this.loadService.getLoadFSProperties().getBaseFolderPath()+
				UtilsIO.getSeparator()+
				Resources.pageCacheFolderName+
				UtilsIO.getSeparator()+
				key;
		
		try {
			
			UtilsIO.writeToFile(path,data);
			logger.info("Add cache page for key: "+key );

		} catch (UtilsException e) {
			logger.error(e);
			return false;
		}
		
		pageCacheMetadata.put(key, path);
		
		return true;
	}
	
	private final int keepCacheFor = 1000*60*60*24*7;//one week

	@Scheduled(fixedRate=86400000)//each 24 hours
	private void validatePageCache()
	{
		File[] files = this.loadService.getPageCacheFiles();
			
		BasicFileAttributes fileAttribute = null;

		long todayTime = Calendar.getInstance().getTimeInMillis();

		for (File file : files) 
		{
			try {
				fileAttribute = Files.readAttributes(Paths.get(file.getAbsolutePath()), BasicFileAttributes.class);
				logger.info(file.getAbsolutePath()+":::"+ fileAttribute.creationTime().toString());
				
				long pageTime = fileAttribute.creationTime().toMillis();
				long result = todayTime - pageTime;
				if( result >= keepCacheFor )
				{
					logger.info("Delete page cache: "+file.getName());
					this.pageCacheMetadata.remove(file.getName());
					file.delete();
				}
				
			} catch (IOException e) {
				logger.error(e);
			}
		}		
	}
}
