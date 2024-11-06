package com.ctnu.config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import com.ctnu.crypto.CipherUtils;
import com.ctnu.http.HttpService;

@Configuration
public class UtilsConfiguration {

	@Autowired
	private Environment environment;
	
	@Bean
	@Scope("singleton")
	public HttpService getHttpService(){
		return new HttpService();
	}

	//set -DaccessKey=XXXXXXX
	@Bean
	@Scope("singleton")
	public CipherUtils getCipherUtils(){
		return new CipherUtils(this.environment.getProperty("accessKey"));
	}
}

