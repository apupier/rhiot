/**
 * Licensed to the Rhiot under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The licenses this file to You under the Apache License, Version 2.0
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
package io.rhiot.gateway.heartbeat

import org.apache.camel.builder.RouteBuilder
import org.apache.camel.model.RouteDefinition
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

import static io.rhiot.utils.Properties.stringProperty
import static java.lang.System.currentTimeMillis
import static java.net.InetAddress.getLocalHost

@Component
@ConditionalOnProperty(name = 'camellabs.iot.gateway.heartbeat.mqtt', havingValue = 'true')
class MqttHeartbeatVerticle extends RouteBuilder {

    def topic = stringProperty('camellabs.iot.gateway.heartbeat.mqtt.topic', 'heartbeat')

    def brokerUrl = stringProperty('camellabs.iot.gateway.heartbeat.mqtt.broker.url')

    // Private helpers

    private String generateHeartBeatMessage() {
        try {
            return getLocalHost().getHostName() + ":" + currentTimeMillis();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    void configure() throws Exception {
        from('heartbeat?multipleConsumers=true').transform().simple(generateHeartBeatMessage()).
                to("paho:${topic}?brokerUrl=${brokerUrl}")
    }

}
