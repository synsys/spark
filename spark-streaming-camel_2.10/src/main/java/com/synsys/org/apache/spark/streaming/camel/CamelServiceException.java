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

/**
 * <code>CamelServiceException</code> is an unchecked exception used to wrap
 * all checked exceptions thrown by <code>CamelService</code>. 
 *
 */
public class CamelServiceException extends RuntimeException {

	private static final long serialVersionUID = 7300150133111715245L;

	/**
	 * 
	 */
	public CamelServiceException() {
		super();
	}

	/**
	 * @param message
	 */
	public CamelServiceException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public CamelServiceException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public CamelServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public CamelServiceException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
