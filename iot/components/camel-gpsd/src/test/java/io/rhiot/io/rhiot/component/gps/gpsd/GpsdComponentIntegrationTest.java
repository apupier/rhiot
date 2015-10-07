/**
 * Licensed to the Rhiot under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.rhiot.io.rhiot.component.gps.gpsd;

import de.taimos.gpsd4java.types.TPVObject;
import io.rhiot.deployer.detector.Device;
import io.rhiot.deployer.detector.DeviceDetector;
import io.rhiot.deployer.detector.SimplePortScanningDeviceDetector;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static io.rhiot.deployer.detector.Device.DEVICE_RASPBERRY_PI_2;
import static org.junit.Assume.assumeTrue;

public class GpsdComponentIntegrationTest extends CamelTestSupport {

    static DeviceDetector deviceDetector = new SimplePortScanningDeviceDetector();

    @BeforeClass
    public static void beforeClass() {
        List<Device> devices = deviceDetector.detectDevices();
        boolean isRpiAvailable = devices.size() == 1 && devices.get(0).type().equals(DEVICE_RASPBERRY_PI_2);
        assumeTrue(isRpiAvailable);
    }

    @AfterClass
    public static void afterClass() {
        deviceDetector.close();
    }

    @Test
    public void testGpsd() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:foo");
        mock.expectedMinimumMessageCount(9);


        //Should get at least 9 messages within 10 seconds
        assertMockEndpointsSatisfied(10, TimeUnit.SECONDS);
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() {
                from("gpsd://gpsSpeedTest?host=rhiot-pi&port=2947").routeId("gpsdSpeed")
                    .process(exchange -> {
                        TPVObject tpvObject = exchange.getIn().getHeader("io.rhiot.gpsd.tpvObject", TPVObject.class);
                        if (tpvObject.getSpeed() > 0) {
                            log.warn("Moving at [{}] meters/second, course [{}]", tpvObject.getSpeed(), tpvObject.getCourse());
                        } else {
                            log.info("GPS is stationary");
                        }
                    }).to("mock:foo")
                ;
            }
        };
    }
}