package com.bankaudit.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;


/**
 * {@link BankAuditConfig} class used to provide the common utility method to read
 * the properties from property file of project configuration
 * 
 * @author amit.patel
 * @version 1.0
 */
public class BankAuditConfig {
	
	@Autowired
	private Environment environment;
	
	private static BankAuditConfig bauAppConfig = null;
	
	public static BankAuditConfig getInstance() {
		return bauAppConfig;
	}
	
	public String getConfigValue(String configKey) {
		return environment.getProperty(configKey);
	}
	
	static void setBauConfig(BankAuditConfig obj){
		BankAuditConfig.bauAppConfig = obj;
	}
	
	@PostConstruct
	public void initIt() throws Exception {
		setBauConfig(this);
	//	bauAppConfig = this;
	}

}
