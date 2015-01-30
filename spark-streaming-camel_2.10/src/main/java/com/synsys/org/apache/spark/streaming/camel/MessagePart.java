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
 * {@code MessagePart} is an enumeration of the
 * {@link org.apache.camel.Message} parts to be processed.
 * <p>
 * One of:
 * <dl>
 * <dt>ALL</dt>
 * <dd>The entire message is processed</dd>
 * <dt>BODY</dt>
 * <dd>If present in the message, the body is processed else the message is
 * skipped</dd>
 * <dt>MANDATORY_BODY</dt>
 * <dd>If present in the message, the body is processed else a
 * {@link CamelServiceException} is thrown</dd>
 * </dl>
 * </li>
 */
public enum MessagePart {
	ALL, BODY, MANDATORY_BODY
}
