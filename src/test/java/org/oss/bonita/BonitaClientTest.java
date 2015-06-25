/*******************************************************************************
 * Copyright (c) 2015 Open Software Solutions GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License v2.0
 * which accompanies this distribution, and is available at
 *
 * Contributors:
 *     Open Software Solutions GmbH
 ******************************************************************************/
package org.oss.bonita;

import org.oss.bonita.client.BonitaClient;


/**
 * Bonita Instance has to run on your local machine or somewhere else otherwise this test will fail.
 *
 * @author magnus
 *
 */
public class BonitaClientTest {

	//@Test - only test with runing Bonita instance
	public void launchClient() throws Exception {
		BonitaClient.main(new String[]{"resources/bonitaclient.properties"});
	}
}
