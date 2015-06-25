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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;

import org.junit.Test;
import org.oss.bonita.repository.CloudAccessor;
import org.oss.bonita.repository.ftp.DomainsRegistry;
import org.oss.bonita.repository.ftp.FtpRepository;


public class RepositoryTest {

	@Test
	public void parseDomainsXml() throws Exception {
		DomainsRegistry reg = DomainsRegistry.fromFile(new File("testresources/domains.xml"));
		assertNotNull(reg);
	}


	@Test
	public void uploadDomainsXml() throws FileNotFoundException, IOException {
		CloudAccessor ca = new FtpRepository("foo","zip","ftp.digireport.ch","halter@digireport.ch","davidnaef");
		ca.putInstance("domains.xml", new FileInputStream("testresources/domains.xml"));
	}

	@Test
	public void listDomains() throws IOException {
		CloudAccessor ca = new FtpRepository("foo","zip","ftp.digireport.ch","halter@digireport.ch","davidnaef");
		Set<String> domains = ca.queryDomainIds();
		assertTrue(domains.contains("domains.xml"));
		assertTrue(domains.contains("2015-04-27-19-49-33-7"));
	}

	@Test
	public void testNewRepo() throws IOException {
		CloudAccessor ca = new FtpRepository("bar","zip", "ftp.digireport.ch","halter@digireport.ch","davidnaef");
		ca.putInstance("cool.xml", new FileInputStream("testresources/domains.xml"));
		Set<String> domains = ca.queryDomainIds();
		assertTrue(domains.contains("cool.xml"));


	}
}
