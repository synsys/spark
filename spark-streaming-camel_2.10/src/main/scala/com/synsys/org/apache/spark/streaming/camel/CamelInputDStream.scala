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
package com.synsys.org.apache.spark.streaming.camel

import java.io.Serializable
import java.util.concurrent.CountDownLatch

import org.apache.camel.CamelContext
import org.apache.camel.Exchange
import org.apache.camel.InvalidPayloadException
import org.apache.camel.Message
import org.apache.camel.Processor
import org.apache.camel.RoutesBuilder
import org.apache.camel.StartupListener
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.scala.dsl.builder.RouteBuilder
import org.apache.spark.Logging
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream._
import org.apache.spark.streaming.receiver.Receiver

import scala.util.{ Failure, Success, Try }

/**
 * An input stream that consumes Apache Camel [[org.apache.camel.Message]]s from
 * the specified component URI.
 */
private[streaming] class CamelInputDStream(
  @transient ssc_ : StreamingContext,
  componentUri: String,
  messagePart: MessagePart,
  storageLevel: StorageLevel) extends ReceiverInputDStream[Serializable](ssc_) with Logging {

  def getReceiver(): Receiver[Serializable] = {
    new CamelReceiver(componentUri, messagePart, storageLevel)
  }
}

private[streaming] class CamelReceiver(
  componentUri: String,
  messagePart: MessagePart,
  storageLevel: StorageLevel) extends Receiver[Serializable](storageLevel) with Logging {
  @transient
  var service: CamelService = null

  def createBuilder: RoutesBuilder = new RouteBuilder() {
    componentUri process (new Processor {
      def process(exchange: Exchange) {
        val tryStore: Try[Unit] = Try(
          messagePart match {
            case MessagePart.ALL => store(exchange.getIn(classOf[Serializable]))
            case MessagePart.MANDATORY_BODY =>
              if (null == exchange.getIn.getBody) {
                throw new InvalidPayloadException(exchange, classOf[Serializable])
              }
              store(exchange.getIn.getMandatoryBody(classOf[Serializable]))
            case MessagePart.BODY =>
              if (null != exchange.getIn.getBody) {
                store(exchange.getIn.getBody(classOf[Serializable]))
              }
          })
        tryStore match {
          case Success(_) =>
          case Failure(ex) => reportError("Message not stored", ex)
        }
        tryStore.get
      }
    })
  }

  def onStart {
    service = new CamelService
    val context = service.getCamelContext
    context.addRoutes(createBuilder)
    context.setAllowUseOriginalMessage(false)
    context.setStreamCaching(true)
    val serviceStartedSignal: CountDownLatch = new CountDownLatch(1)
    context.addStartupListener(new StartupListener() {
      def onCamelContextStarted(context: CamelContext,
        alreadyStarted: Boolean) {
        serviceStartedSignal.countDown
      }
    })
    logInfo("Camel Receiver starting")
    new Thread(service, "Camel Service").start();
    serviceStartedSignal.await
    logInfo("Camel Receiver started")
  }

  def onStop {
    if (null != service) {
      logInfo("Camel Receiver stopping")
      service.stop()
      logInfo("Camel Receiver stopped")
    }
  }
}
