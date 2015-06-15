/*******************************************************************************
 * Copyright (c) 2015 Open Software Solutions GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License v2.0
 * which accompanies this distribution, and is available at
 *
 * Contributors:
 *     Open Software Solutions GmbH
 ******************************************************************************/
package org.oss.bonita.utils.bonita.api;

import java.util.ArrayList;
import java.util.List;

public class ActivityComplete {

	private final String state = "completed";
	private final List<Variable> variables = new ArrayList<Variable>();

	public String getState() {
		return state;
	}
	public List<Variable> getVariables() {
		return variables;
	}
}
