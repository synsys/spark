/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.synsys.org.apache.spark.streaming.camel;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.main.MainSupport;
import org.apache.camel.view.ModelFileGenerator;

/**
 * {@code CamelService} is a lightweight implementation of
 * {@link org.apache.camel.main.MainSupport} that allows Apache Camel to be
 * run on a {@link java.lang.Thread} by implementing
 * {@link java.lang.Runnable}.
 * <p>
 * Use {@link #getCamelContext()} to access the {@link org.apache.camel.CamelContext}
 * @see <a href=http://camel.apache.org>http://camel.apache.org</a>
 */

public class CamelService extends MainSupport implements Runnable {

	private CamelContext _camelContext;

	/**
	 * Default Constructor
	 */
	public CamelService() {
		this(new DefaultCamelContext());
	}

	/**
	 * Constructor
	 * @param camelContext
	 */
	public CamelService(CamelContext camelContext) {
		super();
		_camelContext = camelContext;
	}

	/** 
	 * @return The <code>CamelContext</code> if set, else null
	 */
	public CamelContext getCamelContext() {
		return _camelContext;
	}

	@Override
	public void run() {
		try {
			super.run();
		} catch (Exception e) {
			throw new CamelServiceException(e);
		}
	}

	@Override
	protected ProducerTemplate findOrCreateCamelTemplate() {
		if (getCamelContexts().size() > 0) {
			return getCamelContexts().get(0).createProducerTemplate();
		} else {
			return null;
		}
	}

	@Override
	protected Map<String, CamelContext> getCamelContextMap() {
		Map<String, CamelContext> map = new HashMap<String, CamelContext>(1);
		if (_camelContext != null) {
			map.put(_camelContext.getName(), _camelContext);
		}
		return map;
	}

	@Override
	protected ModelFileGenerator createModelFileGenerator()
			throws JAXBException {
		throw new CamelServiceException("This method is deprecated and not supported here");
	}

	@Override
	protected void doStart() throws Exception {
		super.doStart();
		if (_camelContext != null) {
			_camelContext.start();
		}
	}

	@Override
	protected void doStop() throws Exception {
		if (_camelContext != null) {
			_camelContext.stop();
		}
		super.doStop();
	}

}
