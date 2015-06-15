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

import org.junit.Test;
import org.oss.bonita.client.BonitaClient;


public class BonitaClientTest {

	@Test
	public void launchClient() throws Exception {
		BonitaClient.main(new String[]{"resources/bonitaclient.properties"});
	}
}