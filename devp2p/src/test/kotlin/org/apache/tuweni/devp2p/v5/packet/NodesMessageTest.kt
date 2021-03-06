/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tuweni.devp2p.v5.packet

import org.apache.tuweni.bytes.Bytes
import org.apache.tuweni.crypto.SECP256K1
import org.apache.tuweni.devp2p.EthereumNodeRecord
import org.apache.tuweni.junit.BouncyCastleExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.net.InetAddress

@ExtendWith(BouncyCastleExtension::class)
class NodesMessageTest {

  @Test
  fun encodeCreatesValidBytesSequence() {
    val requestId = Bytes.fromHexString("0xC6E32C5E89CAA754")
    val total = 10
    val nodeRecords = listOf(
      EthereumNodeRecord.toRLP(SECP256K1.KeyPair.random(), ip = InetAddress.getLocalHost(), udp = 9090),
      EthereumNodeRecord.toRLP(SECP256K1.KeyPair.random(), ip = InetAddress.getLocalHost(), udp = 9091),
      EthereumNodeRecord.toRLP(SECP256K1.KeyPair.random(), ip = InetAddress.getLocalHost(), udp = 9092)
    )
    val message = NodesMessage(requestId, total, nodeRecords)

    val encodingResult = message.encode()

    val decodingResult = NodesMessage.create(encodingResult)

    assert(decodingResult.requestId == requestId)
    assert(decodingResult.total == 10)
    assert(EthereumNodeRecord.fromRLP(decodingResult.nodeRecords[0]).udp() == 9090)
    assert(EthereumNodeRecord.fromRLP(decodingResult.nodeRecords[1]).udp() == 9091)
    assert(EthereumNodeRecord.fromRLP(decodingResult.nodeRecords[2]).udp() == 9092)
  }

  @Test
  fun getMessageTypeHasValidIndex() {
    val message = NodesMessage(UdpMessage.requestId(), 0, emptyList())

    assert(4 == message.getMessageType().toInt())
  }
}
