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
package io.rhiot.cloudplatform.camel.openalpr;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.stream.Collectors;

import static io.rhiot.utils.Uuids.uuid;

public class OpenalprProducer extends DefaultProducer {

    public OpenalprProducer(Endpoint endpoint) {
        super(endpoint);
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        File imageFile = new File(getEndpoint().getWorkDir(), uuid() + ".jpg");
        try {
            byte[] image = exchange.getIn().getBody(byte[].class);
            IOUtils.write(image, new FileOutputStream(imageFile));
            List<String> output = getEndpoint().getProcessManager().executeAndJoinOutput(openalprCommand(getEndpoint(), imageFile));
            List<PlateMatch> plates = output.stream().filter(line -> line.contains("confidence:")).map(line -> line.replaceAll("- ", "").split("confidence:")).
                    map(pair -> new PlateMatch(pair[0].trim(), Double.parseDouble(pair[1].trim()))).collect(Collectors.toList());
            exchange.getIn().setBody(plates);
        } finally {
            imageFile.delete();
        }
    }

    @Override
    public OpenalprEndpoint getEndpoint() {
        return (OpenalprEndpoint) super.getEndpoint();
    }

    private String[] openalprCommand(OpenalprEndpoint openalprEndpoint, File imageFile) {
        return new String[]{
                "docker", "run", "-t",
                "-v", openalprEndpoint.getWorkDir().getAbsolutePath() +  ":/data:ro",
                getEndpoint().getDockerImage(), "-c", openalprEndpoint.getCountry(), imageFile.getName()};
    }

}