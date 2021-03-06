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
package io.rhiot.utils.ssh

import io.rhiot.utils.ssh.server.SshServerBuilder
import org.junit.Test

import static com.google.common.truth.Truth.assertThat
import static io.rhiot.utils.Uuids.uuid

class SshTest {

    static sshd = new SshServerBuilder().build().start()

    static ssh = sshd.client('foo', 'bar')

    def file = new File("/parent/${uuid()}")

    @Test
    void shouldHandleEmptyFile() {
        assertThat(ssh.scp(file)).isNull()
    }

    @Test
    void shouldSendFile() {
        // Given
        def text = 'foo'
        ssh.scp(new ByteArrayInputStream(text.getBytes()), file)

        // When
        def received = new String(ssh.scp(file))

        // Then
        assertThat(received).isEqualTo(text)
    }

}