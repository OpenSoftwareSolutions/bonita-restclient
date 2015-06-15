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

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.oss.bonita.utils.bonita.api.ActivityComplete;
import org.oss.bonita.utils.bonita.api.Variable;
import org.oss.bonita.utils.json.Marshaller;


public class JsonTest {

	private final static String JSON_CONTENT = "{\"state\":\"complete\",\"variables\":[{\"name\":\"Name\",\"value\":\"Donat\"},{\"name\":\"Vorname\",\"value\":\"Müller\"},{\"name\":\"Sex\",\"value\":\"m\"}]}";
	@Test
	public void marshall() throws Exception  {
		Marshaller<ActivityComplete> m = new Marshaller<ActivityComplete>();
		ActivityComplete ac = new ActivityComplete();
		ac.getVariables().add(new Variable("Name","Donat"));
		ac.getVariables().add(new Variable("Vorname","Müller"));
		ac.getVariables().add(new Variable("Sex","m"));
		assertEquals(JSON_CONTENT,m.marshall(ac));
	}

}
