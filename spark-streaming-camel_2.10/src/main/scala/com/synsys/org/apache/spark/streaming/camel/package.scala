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

package com.synsys.org.apache.spark.streaming

/** 
 * A Spark Streaming module that ingests data from an 
 * [[http://camel.apache.org/ Apache Camel]] component. Any of the many
 * [[http://camel.apache.org/components.html Apache Camel components]] supporting message 
 * consumption can be used.
 * 
 * Apache Camel components encapsulate data in an 
 * [[http://camel.apache.org/maven/current/camel-core/apidocs/org/apache/camel/Message.html org.
 * apache.camel.Message]]. A `Message` may have header, body and attachment parts. Typically, 
 * just the body part is of interest to Spark Streaming applications, though the entire `Message`
 * can be stored. All `Message` parts stored in Spark must implement [[java.io.Serializable]].   
 * 
 * Class [[CamelInputDStream]] is an implementation of [[ReceiverInputDStream]] that starts an
 * instance of [[CamelReceiver]] on each worker node to receive and store messages. Each 
 * [[CamelReceiver]] runs an instance of [[CamelService]] on a new thread. 
 * 
 * A [[CamelInputDStream]] is constructed with
 *  - An Apache Camel component specific URI, which selects the
 * component and declares associated arguments
 *  - The [[MessagePart]], which defines the parts of the `org.apache.camel.Message` stored in 
 * Spark. One of
 *   - '''ALL''' The entire message is stored
 *   - '''BODY''' If present in the message, the body is stored else the event is skipped
 *   - '''MANDATORY_BODY''' If present in the message, the body is stored else a
 * [[CamelServiceException]] is thrown
 *  - The [[StorageLevel]] which defines how Spark persists the data. This is described in the
 * [[http://spark.apache.org/docs/latest/programming-guide.html#rdd-persistence Spark Programming
 * Guide]]
 *
 *  ==== Some examples ==== 
 *  - '''JMS''' The [[http://camel.apache.org/jms.html Apache Camel JMS component]] is selected by
 *  the URI and passes the JMS specific arguments
 * {{{new CamelInputDStream("jms:queue:my.queue", 
 *    MessagePart.BODY, 
 *    StorageLevel.MEMORY_ONLY)}}}
 *  
 *  - '''TCP/UDP''' The [[http://camel.apache.org/netty4.html Apache Camel Netty4 component]] 
 *  is selected by the URI and passes the Netty specific arguments
 * {{{new CamelInputDStream("netty4:tcp://localhost:5150", 
 *    MessagePart.BODY, 
 *    StorageLevel.MEMORY_ONLY)}}}
 *    
 *  - '''REST''' The [[http://camel.apache.org/rest.html Apache Camel REST component]] is selected
 *  by the URI and passes the REST specific arguments
 * {{{new CamelInputDStream("rest:get:my/path", 
 *    MessagePart.BODY, 
 *    StorageLevel.MEMORY_ONLY)}}}
 * 
 *  - '''RMI''' The [[http://camel.apache.org/rmi.html Apache Camel RMI component]] is selected by
 *  the URI and passes the RMI specific arguments
 * {{{new CamelInputDStream("rmi://localhost:1099/myService", 
 *    MessagePart.ALL, 
 *    StorageLevel.MEMORY_ONLY}}}
 *  
 *  ==== Using Apache Camel routing and mediation ====
 * If Apache Camel routing and mediation capabilities are required, a full Apache Camel instance
 * can be connected using any of the available protocols, as best fits the deployment.
 * When running Spark in local mode with the full Apache Camel instance running in the same VM 
 * the most efficient way is to use the 
 * [[http://camel.apache.org/direct-vm.html Direct VM component]]
 * in the full instance to forward messages to [[CamelService]]. This is the technique used
 * in the unit tests. For example, the full Apache Camel instance uses...
 * 
 * {{{"some Camel component URI" -> "direct-vm:spark"}}}
 * 
 * ...and [[CamelInputDStream]] uses the same `URI` in the constructor...
 * {{{new CamelInputDStream("direct-vm:spark", MessagePart.BODY, StorageLevel.MEMORY_ONLY)}}}
 * 
 * When running on a cluster a network protocol is required. A queuing protocol, such as JMS,
 * has the useful side effect of automatically load balancing the cluster.
 *  
 * @see [[http://camel.apache.org/components.html Apache Camel Components]]
 * @see[[http://spark.apache.org/docs/latest/programming-guide.html Spark Programming Guide]]
 * @see [[http://spark.apache.org/docs/latest/streaming-programming-guide.html Spark Streaming
 * Programming Guide]]
 *
 **/

package object camel

