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

import java.io.Serializable;

import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.api.java.{ JavaReceiverInputDStream, JavaStreamingContext, JavaDStream }

import scala.reflect.ClassTag
import org.apache.spark.streaming.dstream.{ ReceiverInputDStream, DStream }

object CamelUtils {
  /**
   * Create an input stream that receives messages from an Apache Camel component.
   * @param ssc           StreamingContext object
   * @param componentUri  Uri of the Apache Camel component
   * @param messagePart   The part(s) of the message to store. Defaults to MessagePart.BODY
   * @param storageLevel  RDD storage level. Defaults to StorageLevel.MEMORY_AND_DISK_SER_2
   * @tparam V            The type of the message value
   */
  def createStream[V: ClassTag](
    ssc: StreamingContext,
    componentUri: String,
    messagePart: MessagePart = MessagePart.BODY,
    storageLevel: StorageLevel = StorageLevel.MEMORY_AND_DISK_SER_2): ReceiverInputDStream[V] = {
    new CamelInputDStream[V](ssc, componentUri, messagePart, storageLevel)
  }

  /**
   * Create an input stream that receives messages from an Apache Camel component.
   * Storage level of the data will be the default StorageLevel.MEMORY_AND_DISK_SER_2.
   * @param valueClass    The type of the message value
   * @param jssc          JavaStreamingContext object
   * @param componentUri  Uri of the Apache Camel component
   * @param messagePart   The part(s) of the message to receive
   */
  def createStream[V](
    valueClass: Class[V],
    jssc: JavaStreamingContext,
    componentUri: String,
    messagePart: MessagePart): JavaReceiverInputDStream[V] = {
    implicit val valueClassTag: ClassTag[V] = ClassTag(valueClass)
    createStream[V](jssc.ssc, componentUri, messagePart)
  }

  /**
   * Create an input stream that receives messages from an Apache Camel component.
   * The stored Message part is MessagePart.BODY
   * The storage level of the data will be the default StorageLevel.MEMORY_AND_DISK_SER_2.
   * @param valueClass    The type of the message value
   * @param jssc          JavaStreamingContext object
   * @param componentUri  Uri of the Apache Camel component
   */
  def createStream[V](
    valueClass: Class[V],
    jssc: JavaStreamingContext,
    componentUri: String): JavaReceiverInputDStream[V] = {
    implicit val valueClassTag: ClassTag[V] = ClassTag(valueClass)
    createStream[V](jssc.ssc, componentUri)
  }

  /**
   * Create an input stream that receives messages from an Apache Camel component.
   * The stored Message part is MessagePart.BODY
   * @param valueClass    The type of the message value
   * @param jssc          JavaStreamingContext object
   * @param componentUri  Uri of the Apache Camel component
   * @param storageLevel  RDD storage level.
   */
  def createStream[V](
    valueClass: Class[V],
    jssc: JavaStreamingContext,
    componentUri: String,
    storageLevel: StorageLevel): JavaReceiverInputDStream[V] = {
    implicit val valueClassTag: ClassTag[V] = ClassTag(valueClass)
    createStream[V](jssc.ssc, componentUri, MessagePart.BODY, storageLevel)
  }

  /**
   * Create an input stream that receives messages from an Apache Camel component.
   * @param valueClass    The type of the message value
   * @param jssc          JavaStreamingContext object
   * @param componentUri  Uri of the Apache Camel component
   * @param messagePart   The part(s) of the message to receive
   * @param storageLevel  RDD storage level
   */
  def createStream[V](
    valueClass: Class[V],
    jssc: JavaStreamingContext,
    componentUri: String,
    messagePart: MessagePart,
    storageLevel: StorageLevel): JavaReceiverInputDStream[V] = {
    implicit val valueClassTag: ClassTag[V] = ClassTag(valueClass)
    createStream[V](jssc.ssc, componentUri, messagePart, storageLevel)
  }

  /**
   * Create an input stream that receives messages from an Apache Camel component.
   * Storage level of the data will be the default StorageLevel.MEMORY_AND_DISK_SER_2.
   * @param jssc          JavaStreamingContext object
   * @param componentUri  Uri of the Apache Camel component
   * @param messagePart   The part(s) of the message to receive
   *
   * @deprecated Use the equivalent generic method above
   */
  def createStream(
    jssc: JavaStreamingContext,
    componentUri: String,
    messagePart: MessagePart): JavaReceiverInputDStream[Serializable] = {
    implicitly[ClassTag[AnyRef]].asInstanceOf[ClassTag[Serializable]]
    createStream[Serializable](jssc.ssc, componentUri, messagePart)
  }

  /**
   * Create an input stream that receives messages from an Apache Camel component.
   * The stored Message part is MessagePart.BODY
   * The storage level of the data will be the default StorageLevel.MEMORY_AND_DISK_SER_2.
   * @param jssc          JavaStreamingContext object
   * @param componentUri  Uri of the Apache Camel component
   *
   * @deprecated Use the equivalent generic method above
   */
  def createStream(
    jssc: JavaStreamingContext,
    componentUri: String): JavaReceiverInputDStream[Serializable] = {
    implicitly[ClassTag[AnyRef]].asInstanceOf[ClassTag[Serializable]]
    createStream[Serializable](jssc.ssc, componentUri)
  }

  /**
   * Create an input stream that receives messages from an Apache Camel component.
   * The stored Message part is MessagePart.BODY
   * @param jssc          JavaStreamingContext object
   * @param componentUri  Uri of the Apache Camel component
   * @param storageLevel  RDD storage level
   *
   * @deprecated Use the equivalent generic method above
   */
  def createStream(
    jssc: JavaStreamingContext,
    componentUri: String,
    storageLevel: StorageLevel): JavaReceiverInputDStream[Serializable] = {
    implicitly[ClassTag[AnyRef]].asInstanceOf[ClassTag[Serializable]]
    createStream[Serializable](jssc.ssc, componentUri, MessagePart.BODY, storageLevel)
  }

  /**
   * Create an input stream that receives messages from an Apache Camel component.
   * @param jssc          JavaStreamingContext object
   * @param componentUri  Uri of the Apache Camel component
   * @param messagePart   The part(s) of the message to receive
   * @param storageLevel  RDD storage level
   *
   * @deprecated Use the equivalent generic method above
   */
  def createStream(
    jssc: JavaStreamingContext,
    componentUri: String,
    messagePart: MessagePart,
    storageLevel: StorageLevel): JavaReceiverInputDStream[Serializable] = {
    implicitly[ClassTag[AnyRef]].asInstanceOf[ClassTag[Serializable]]
    createStream[Serializable](jssc.ssc, componentUri, messagePart, storageLevel)
  }
}
