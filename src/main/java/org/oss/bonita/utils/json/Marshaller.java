/*******************************************************************************
 * Copyright (c) 2015 Open Software Solutions GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License v2.0
 * which accompanies this distribution, and is available at
 *
 * Contributors:
 *     Open Software Solutions GmbH
 ******************************************************************************/
package org.oss.bonita.utils.json;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Marshaller<T> {

	ObjectMapper mapper = new ObjectMapper();
	public String marshall(T obj) throws Exception {
		return mapper.writeValueAsString(obj);
	}

	public T unmarshall(String json, Class<T> clazz) throws Exception {
		return mapper.readValue(json, clazz);
	}
}
