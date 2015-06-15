/*******************************************************************************
 * Copyright (c) 2015 Open Software Solutions GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License v2.0
 * which accompanies this distribution, and is available at
 *
 * Contributors:
 *     Open Software Solutions GmbH
 ******************************************************************************/
package org.oss.bonita.utils.bonita;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.impl.conn.SchemeRegistryFactory;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.oss.bonita.utils.bonita.api.ActivityComplete;
import org.oss.bonita.utils.json.Marshaller;


/**
 * @author Donat Müller, Magnus Karlsson
 *
 */
public class RestClient {

	private final HttpClient httpClient;
	private final HttpContext httpContext;
	private final String bonitaURI;

	public RestClient(String bonitaURI) {
		this.bonitaURI = bonitaURI;
		this.httpContext =  new BasicHttpContext();
		PoolingClientConnectionManager conMan = new PoolingClientConnectionManager(SchemeRegistryFactory.createDefault());
		conMan.setMaxTotal(200);
		conMan.setDefaultMaxPerRoute(200);
		this.httpClient = new DefaultHttpClient(conMan);
	}

	public void loginAs(String username, String password) {

		try {

			CookieStore cookieStore = new BasicCookieStore();
			httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

			String loginURL = "/loginservice";

			// If you misspell a parameter you will get a HTTP 500 error
			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
			urlParameters.add(new BasicNameValuePair("username", username));
			urlParameters.add(new BasicNameValuePair("password", password));
			urlParameters.add(new BasicNameValuePair("redirect", "false"));

			// UTF-8 is mandatory otherwise you get a NPE
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(urlParameters, "utf-8");
			executePostRequest(loginURL, entity);

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

	}

	public void logout() {
		consumeResponse(executeGetRequest("/logoutservice"),false);
	}


	public void setProcessInstanceVariable(String processInstanceId, String name, String type, String value) {
		String requestUrl = String.format("/API/bpm/caseVariable/%s/%s",processInstanceId,name);
		String payLoad = String.format("{id:\"%s/variableText\",name:\"%s\",type:\"%s\",value:\"%s\"}",processInstanceId,name,type,value);
		consumeResponse(executePutRequest(requestUrl,payLoad),true);
	}

	public void updateAndCompleteActivity(String activityId, ActivityComplete payloadValue) throws Exception {
		String requestUrl = String.format("/API/bpm/activity/%s",activityId);
		String payloadString = new Marshaller<ActivityComplete>().marshall(payloadValue);
		consumeResponse(executePutRequest(requestUrl,payloadString),true);
	}

	public void getProcessInstance(String processInstanceId) {
		String requestUrl = String.format("/API/bpm/case/%s",processInstanceId);
		consumeResponse(executeGetRequest(requestUrl),true);
	}

	public void listOpenedProcessInstances() {
		consumeResponse(executeGetRequest("/API/bpm/case?p=0&c=100"),true);
	}


	protected HttpResponse executeGetRequest(String apiURI) {
		try {
			HttpGet getRequest = new HttpGet(bonitaURI + apiURI);

			HttpResponse response = httpClient.execute(getRequest, httpContext);

			return response;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

	}

	private int executePostRequest(String apiURI, UrlEncodedFormEntity entity) {
		try {
			HttpPost postRequest = new HttpPost(bonitaURI + apiURI);

			postRequest.setEntity(entity);

			HttpResponse response = httpClient.execute(postRequest, httpContext);

			return consumeResponse(response, true);

		} catch (HttpHostConnectException e) {
			throw new RuntimeException("Bonita bundle may not have been started, or the URL is invalid. Please verify hostname and port number. URL used is: " + bonitaURI,e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

	}

	protected HttpResponse executePostRequest(String apiURI, String payloadAsString) {
		try {
			HttpPost postRequest = new HttpPost(bonitaURI + apiURI);

			StringEntity input = new StringEntity(payloadAsString);
			input.setContentType("application/json");

			postRequest.setEntity(input);

			HttpResponse response = httpClient.execute(postRequest, httpContext);

			return response;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	protected HttpResponse executePutRequest(String apiURI, String payloadAsString) {
		try {
			HttpPut putRequest = new HttpPut(bonitaURI + apiURI);
			putRequest.addHeader("Content-Type", "application/json");

			StringEntity input = new StringEntity(payloadAsString);
			input.setContentType("application/json");
			putRequest.setEntity(input);

			return httpClient.execute(putRequest, httpContext);

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	protected HttpResponse executeDeleteRequest(String deleteURI) {
		try {

			HttpDelete deleteRequest = new HttpDelete(bonitaURI + deleteURI);
			HttpResponse response = httpClient.execute(deleteRequest, httpContext);

			return response;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

	}

	private int consumeResponse(HttpResponse response, boolean printResponse) {

		String responseAsString = consumeResponseIfNecessary(response);
		if(printResponse) {
			System.out.println(responseAsString);
		}

		return ensureStatusOk(response);
	}

	private String consumeResponseIfNecessary(HttpResponse response) {
		if (response.getEntity() != null) {
			BufferedReader rd;
			try {
				rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

				StringBuffer result = new StringBuffer();
				String line = "";
				while ((line = rd.readLine()) != null) {
					result.append(line);
				}
				return result.toString();
			} catch (Exception e) {
				throw new RuntimeException("Failed to consume response.", e);
			}
		} else {
			return "";
		}
	}

	public void makeSureLocaleIsActive() {
		consumeResponse(executeGetRequest("/API/system/i18ntranslation?f=locale%3den"), false);
	}

	private int ensureStatusOk(HttpResponse response) {
		if (response.getStatusLine().getStatusCode() != 201 && response.getStatusLine().getStatusCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode() + " : "
					+ response.getStatusLine().getReasonPhrase());
		}
		return response.getStatusLine().getStatusCode();
	}


}
