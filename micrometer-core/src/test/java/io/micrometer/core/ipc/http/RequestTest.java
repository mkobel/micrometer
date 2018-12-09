/**
 * Copyright 2018 Pivotal Software, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micrometer.core.ipc.http;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link HttpSender.Request}.
 *
 * @author Johnny Lim
 */
class RequestTest {

    @Test
    void compressShouldAddContentEncodingHeader() throws IOException, NoSuchFieldException, IllegalAccessException {
        HttpSender.Request.Builder builder = HttpSender.Request.build("https://micrometer.io/", mock(HttpSender.class)).compress();
        Field requestHeadersField = HttpSender.Request.Builder.class.getDeclaredField("requestHeaders");
        requestHeadersField.setAccessible(true);
        Map<String, String> requestHeaders = (Map<String, String>) requestHeadersField.get(builder);
        assertThat(requestHeaders).containsEntry("Content-Encoding", "gzip");
    }

}
