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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.camel.CamelContext;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.StartupListener;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.storage.StorageLevel;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.minlog.Log;
import com.synsys.org.apache.spark.streaming.LocalJavaStreamingContext;

public class JavaCamelStreamSuite extends LocalJavaStreamingContext {

	private static final long serialVersionUID = 1L;

	protected static final Logger LOG = LoggerFactory
			.getLogger(JavaCamelStreamSuite.class);

	private static int EVENT_LIMIT = 3;

	@Test
	public void testAPI() {
		String componentUri = "abc";
		MessagePart messagePart = MessagePart.BODY;
		StorageLevel storageLevel = StorageLevel.MEMORY_ONLY_SER();

		// tests the vanilla API, does not actually test data receiving
		assertNotNull(CamelUtils.createStream(ssc, componentUri));
		assertNotNull(CamelUtils.createStream(ssc, componentUri, messagePart));
		assertNotNull(CamelUtils.createStream(ssc, componentUri, storageLevel));
		assertNotNull(CamelUtils.createStream(ssc, componentUri, messagePart,
				storageLevel));
		
		// tests the generic API, does not actually test data receiving
		assertNotNull(CamelUtils.createStream(String.class, ssc, componentUri));
		assertNotNull(CamelUtils.createStream(String.class, ssc, componentUri, messagePart));
		assertNotNull(CamelUtils.createStream(String.class, ssc, componentUri, storageLevel));
		assertNotNull(CamelUtils.createStream(String.class, ssc, componentUri, messagePart,
				storageLevel));
	}

	@Test
	public void testStreamBody() throws Exception {
		final String uri = "direct-vm:test";
		final String producerUri = uri + "?block=true&timeout=10000";
		final String consumerUri = uri;
		testStream(producerUri, consumerUri, MessagePart.BODY, EVENT_LIMIT,
				new MessageFormat("Test message {0}"));
	}

	@Test
	public void testStreamBodyNetty4() throws Exception {
		final String uri = "netty4:tcp://localhost:5150";
		final String producerUri = uri
				+ "?sync=false&connectTimeout=10000&requestTimeout=10000&lazyChannelCreation=true";
		final String consumerUri = uri + "?sync=false";
		testStream(producerUri, consumerUri, MessagePart.BODY, EVENT_LIMIT,
				new MessageFormat("Test message {0}"));
	}

	@Test
	public void testStreamMandatoryBody() throws Exception {
		final String uri = "direct-vm:test";
		final String producerUri = uri + "?block=true&timeout=10000";
		final String consumerUri = uri;
		testStream(producerUri, consumerUri, MessagePart.MANDATORY_BODY, 0);
	}
	
	@Test
	public void testStreamAll() throws Exception {
		final String uri = "direct-vm:test";
		final String producerUri = uri + "?block=true&timeout=10000";
		final String consumerUri = uri;
		testStream(producerUri, consumerUri, MessagePart.ALL, EVENT_LIMIT,
				new MessageFormat("Test message {0}"));
	}

	protected void testStream(String producerUri, String consumerUri,
			MessagePart messagePart, int resultCount) throws Exception {
		testStream(producerUri, consumerUri, messagePart, resultCount, null);
	}

	/**
	 * A CamelService is created to send test messages from the producer URI to
	 * the {@code ReceiverInputDStream} via the consumer URI
	 * 
	 * @throws Exception
	 */
	protected void testStream(String producerUri, String consumerUri,
			MessagePart messagePart, int resultCount, MessageFormat messageForm)
			throws Exception {
		final AtomicLong dataCounter = new AtomicLong(0);

		JavaReceiverInputDStream<String> stream = CamelUtils
				.createStream(String.class, ssc, consumerUri, messagePart,
						StorageLevel.MEMORY_ONLY_SER());

		JavaDStream<String> mapped = stream
				.map(new Function<String, String>() {
					private static final long serialVersionUID = 1L;

					@Override
					public String call(String v1) throws Exception {
						return v1 + ".";
					}
				});
		mapped.foreachRDD(new Function<JavaRDD<String>, Void>() {
			private static final long serialVersionUID = 1L;

			@Override
			public Void call(final JavaRDD<String> rdd) throws Exception {
				final long count = rdd.count();
				dataCounter.addAndGet(count);
				return null;
			}
		});

		CamelService messageProducer = null;

		try {
			LOG.info("Starting context: {}", ssc);
			ssc.start();
			long startTime = System.currentTimeMillis();
			LOG.info("Started context: {}", ssc);
			long timeout = 10000;

			// TODO Need a way to detect when the context is fully started
			Thread.sleep(5000);

			/*
			 * Send a sequence of messages from the message producer to the
			 * Camel consumer running within the streaming receiver
			 */
			LOG.info("Starting Camel message producer");
			messageProducer = new CamelService();
			CamelContext context = messageProducer.getCamelContext();
			ProducerTemplate template = context.createProducerTemplate();
			context.setAllowUseOriginalMessage(false);
			context.setStreamCaching(true);
			final CountDownLatch producerStartedSignal = new CountDownLatch(1);
			context.addStartupListener(new StartupListener() {

				@Override
				public void onCamelContextStarted(CamelContext context,
						boolean alreadyStarted) throws Exception {
					producerStartedSignal.countDown();
				}

			});
			new Thread(messageProducer, "Message Producer").start();
			producerStartedSignal.await();

			for (int i = 0; i < EVENT_LIMIT; i++) {
				String body = null == messageForm ? null : messageForm
						.format(new Object[] { i });
				LOG.info("Sending message: {}", body);
				try {
					template.sendBody(producerUri, body);
				} catch (CamelExecutionException e) {
					Log.warn("sendBody() failed", e);
				}
			}

			while (dataCounter.get() == 0
					&& System.currentTimeMillis() - startTime < timeout) {
				Thread.sleep(0); // Yield
			}

			assertTrue(
					Integer.toString(EVENT_LIMIT) + " messages sent, "
							+ Long.toString(dataCounter.get())
							+ " messages received, expected "
							+ Integer.toString(resultCount),
					dataCounter.get() == resultCount);
		} finally {
			if (null != messageProducer) {
				LOG.info("Stopping Camel message producer");
				messageProducer.stop();
			}

			if (null != ssc) {
				LOG.info("Stopping context: {}", ssc);
				ssc.stop();
			}
		}
	}

	/**
	 * A CamelService is created to send test messages from the producer URI to
	 * the {@code ReceiverInputDStream} via the consumer URI
	 * 
	 * @throws Exception
	 */
	protected void streamMandatoryBody(String producerUri, String consumerUri)
			throws Exception {
		final MessageFormat testForm = new MessageFormat("Test message {0}");
		final AtomicLong dataCounter = new AtomicLong(0);

		JavaReceiverInputDStream<Serializable> stream = CamelUtils
				.createStream(ssc, consumerUri, MessagePart.MANDATORY_BODY,
						StorageLevel.MEMORY_ONLY_SER());

		JavaDStream<Serializable> mapped = stream
				.map(new Function<Serializable, Serializable>() {
					private static final long serialVersionUID = 1L;

					@Override
					public Serializable call(Serializable v1) throws Exception {
						return v1 + ".";
					}
				});
		mapped.foreachRDD(new Function<JavaRDD<Serializable>, Void>() {
			private static final long serialVersionUID = 1L;

			@Override
			public Void call(final JavaRDD<Serializable> rdd) throws Exception {
				final long count = rdd.count();
				dataCounter.addAndGet(count);
				return null;
			}
		});

		CamelService messageProducer = null;

		try {
			LOG.info("Starting context: {}", ssc);
			ssc.start();
			long startTime = System.currentTimeMillis();
			LOG.info("Started context: {}", ssc);
			long timeout = 10000;

			// TODO Need a way to detect when the context is fully started
			Thread.sleep(5000);

			/*
			 * Send a sequence of messages from the message producer to the
			 * Camel consumer running within the streaming receiver
			 */
			LOG.info("Starting Camel message producer");
			messageProducer = new CamelService();
			CamelContext context = messageProducer.getCamelContext();
			ProducerTemplate template = context.createProducerTemplate();
			context.setAllowUseOriginalMessage(false);
			context.setStreamCaching(true);
			final CountDownLatch producerStartedSignal = new CountDownLatch(1);
			context.addStartupListener(new StartupListener() {

				@Override
				public void onCamelContextStarted(CamelContext context,
						boolean alreadyStarted) throws Exception {
					producerStartedSignal.countDown();
				}

			});
			new Thread(messageProducer, "Message Producer").start();
			producerStartedSignal.await();

			for (int i = 0; i < EVENT_LIMIT; i++) {
				String body = testForm.format(new Object[] { i });
				LOG.info("Sending message: {}", body);
				try {
					template.sendBody(producerUri, body);
					template.sendBody(producerUri, null);
				} catch (CamelExecutionException e) {
					Log.warn("sendBody() failed", e);
				}
			}

			while (dataCounter.get() == 0
					&& System.currentTimeMillis() - startTime < timeout) {
				Thread.sleep(0); // Yield
			}

			assertTrue(Integer.toString(EVENT_LIMIT) + " messages sent, "
					+ Long.toString(dataCounter.get()) + " messages received",
					dataCounter.get() > 0);
		} finally {
			if (null != messageProducer) {
				LOG.info("Stopping Camel message producer");
				messageProducer.stop();
			}

			if (null != ssc) {
				LOG.info("Stopping context: {}", ssc);
				ssc.stop();
			}
		}
	}
}
