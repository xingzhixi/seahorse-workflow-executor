/**
 * Copyright 2015, deepsense.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.deepsense.workflowexecutor.communication

import spray.json._

import io.deepsense.models.json.graph.GraphJsonProtocol.GraphReader
import io.deepsense.models.json.workflow.WorkflowJsonProtocol

case class ProtocolDeserializer(
    override val graphReader: GraphReader)
  extends MQMessageDeserializer
  with WorkflowJsonProtocol
  with ConnectMQJsonProtocol
  with LaunchMQJsonProtocol
  with AbortMQJsonProtocol
  with StatusRequestMQJsonProtocol
  with GetPythonGatewayAddressMQJsonProtocol {

  override protected def deserializeJson(jsObject: JsObject): ReadMessageMQ = {
    val fields = jsObject.fields
    val messageType = fields(MessageMQ.messageTypeKey).convertTo[String]
    val body = fields(MessageMQ.messageBodyKey)

    messageType match {
      case ConnectMQ.messageType => body.convertTo[ConnectMQ]
      case LaunchMQ.messageType => body.convertTo[LaunchMQ]
      case AbortMQ.messageType => body.convertTo[AbortMQ]
      case StatusRequestMQ.messageType => body.convertTo[StatusRequestMQ]
      case GetPythonGatewayAddressMQ.messageType => body.convertTo[GetPythonGatewayAddressMQ]
    }
  }
}