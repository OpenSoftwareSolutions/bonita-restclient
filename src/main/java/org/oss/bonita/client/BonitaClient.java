/*******************************************************************************
 * Copyright (c) 2015 Open Software Solutions GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License v2.0
 * which accompanies this distribution, and is available at
 *
 * Contributors:
 *     Open Software Solutions GmbH
 ******************************************************************************/
package org.oss.bonita.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.zip.ZipInputStream;

import org.oss.bonita.repository.CloudAccessor;
import org.oss.bonita.repository.ftp.FtpRepository;
import org.oss.bonita.utils.bonita.RestClient;
import org.oss.bonita.utils.bonita.api.ActivityComplete;
import org.oss.bonita.utils.bonita.api.Variable;
import org.oss.bonita.utils.dom.FormFieldExtractor;


public class BonitaClient {

	private static final String ACTIVITY_INSTANCE_ID = "activityinstanceid";
	private static final String FORM_COMPLETED = "formcompleted";
	private static final String TRUE = "true";

	private static Logger LOGGER = Logger.getLogger(BonitaClient.class
			.getName());

	private final String bonitaUrl;
	private final String bonitaUser;
	private final String bonitaPassword;
	private final String cloudMandator;
	private final String cloudFileSuffix;
	private final String cloudHost;
	private final String cloudUser;
	private final String cloudPassword;

	/**
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws Exception {
		LOGGER.info("START ftp repository sync");
		String prpertyFileName = "bonitaclient.properties";
		if (args.length>0) {
			prpertyFileName = args[0];
		}
		File propertyFile = new File(prpertyFileName);
		if (propertyFile.exists()) {
			Properties props = new Properties();
			props.load(new FileInputStream(propertyFile));
			BonitaClient client = new BonitaClient(props.getProperty("bonitaUrl"),
					props.getProperty("bonitaUser"),props.getProperty("bonitaPassword"),
					props.getProperty("cloudMandator"), props.getProperty("cloudFileSuffix"),
					props.getProperty("cloudHost"), props.getProperty("cloudUser"),props.getProperty("cloudPassword"));
			client.synchronizeCloud();
		} else {
			LOGGER.warning("Property file " + propertyFile.getAbsolutePath() + " not found.");
		}
		LOGGER.info("END ftp repository sync");
	}




	public BonitaClient(String bonitaUrl, String bonitaUser,
			String bonitaPassword, String cloudMandator,
			String cloudFileSuffix, String cloudHost, String cloudUser,
			String cloudPassword) {
		super();
		this.bonitaUrl = bonitaUrl;
		this.bonitaUser = bonitaUser;
		this.bonitaPassword = bonitaPassword;
		this.cloudMandator = cloudMandator;
		this.cloudFileSuffix = cloudFileSuffix;
		this.cloudHost = cloudHost;
		this.cloudUser = cloudUser;
		this.cloudPassword = cloudPassword;
	}




	/**
	 * @throws Exception
	 */
	public void synchronizeCloud() throws Exception {
		while (true) {
			CloudAccessor ca = new FtpRepository(cloudMandator, cloudFileSuffix,
					cloudHost, cloudUser, cloudPassword);
			RestClient restClient = new RestClient(bonitaUrl);
			restClient.loginAs(bonitaUser, bonitaPassword);
			for (String domainId : ca.queryDomainIds()) {
				LOGGER.info("Processing: " + domainId);
				InputStream zipFile = ca.getInstance(domainId, false);
				ZipInputStream zis = new ZipInputStream(zipFile);
				zis.getNextEntry();
				FormFieldExtractor extractor = new FormFieldExtractor(zis);
				Map<String, String> variables = extractor.extractFields();
				if (TRUE.equals(variables.get(FORM_COMPLETED))) {
					ActivityComplete ac = new ActivityComplete();
					for (Map.Entry<String, String> entry : variables.entrySet()) {
						if (!entry.getKey().equals(ACTIVITY_INSTANCE_ID)) {
							ac.getVariables().add(
									new Variable(entry.getKey(), entry
											.getValue()));
						}
					}
					String activityInstanceId = variables.get(ACTIVITY_INSTANCE_ID);
					restClient.updateAndCompleteActivity(activityInstanceId, ac);
					LOGGER.info("Activity: " + activityInstanceId +" completed with: " + ac.getVariables());
					ca.removeInstance(domainId);
					LOGGER.info("Remove instance: " + domainId );
				}
			}
			restClient.logout();
			Thread.sleep(5000);
		}
	}

}
