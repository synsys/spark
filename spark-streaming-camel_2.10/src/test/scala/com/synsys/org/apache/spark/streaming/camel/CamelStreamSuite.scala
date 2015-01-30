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

import org.scalatest.FunSuite

import org.apache.spark.streaming.{ Seconds, StreamingContext }
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.dstream.ReceiverInputDStream

class CamelStreamSuite extends FunSuite {

  val batchDuration = Seconds(1)

  private val master: String = "local[2]"

  private val framework: String = this.getClass.getSimpleName

  test("Camel input stream") {
    val ssc = new StreamingContext(master, framework, batchDuration)
    val componentUri = "abc"
    val messagePart = MessagePart.BODY
    val storageLevel = StorageLevel.MEMORY_ONLY_SER

    // tests the API, does not actually test data receiving
    val test1: ReceiverInputDStream[Serializable] = CamelUtils.createStream(ssc, componentUri)
    val test2: ReceiverInputDStream[Serializable] = CamelUtils.createStream(ssc, componentUri, messagePart)
    val test3: ReceiverInputDStream[Serializable] = CamelUtils.createStream(ssc, componentUri, messagePart, storageLevel)

    ssc.stop()
  }
}
