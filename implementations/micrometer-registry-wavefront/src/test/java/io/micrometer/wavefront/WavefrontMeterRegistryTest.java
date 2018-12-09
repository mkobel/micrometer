/**
 * Copyright 2017 Pivotal Software, Inc.
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
package io.micrometer.wavefront;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MockClock;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link WavefrontMeterRegistry}.
 *
 * @author Johnny Lim
 */
class WavefrontMeterRegistryTest {
    private final WavefrontConfig config = new WavefrontConfig() {
        @Override
        public String get(String key) {
            return null;
        }

        @Override
        public String uri() {
            return "uri";
        }

        @Override
        public String apiToken() {
            return "apiToken";
        }
    };

    private final WavefrontMeterRegistry registry = new WavefrontMeterRegistry(config, new MockClock());

    @Test
    void addMetric() {
        Stream.Builder<String> metricsStreamBuilder = Stream.builder();
        Meter.Id id = registry.counter("name").getId();
        registry.addMetric(metricsStreamBuilder, id, null, System.currentTimeMillis(), 1d);
        assertThat(metricsStreamBuilder.build().count()).isEqualTo(1);
    }

    @Test
    void addMetricWhenNanOrInfinityShouldNotAdd() {
        Stream.Builder<String> metricsStreamBuilder = Stream.builder();
        Meter.Id id = registry.counter("name").getId();
        registry.addMetric(metricsStreamBuilder, id, null, System.currentTimeMillis(), Double.NaN);
        registry.addMetric(metricsStreamBuilder, id, null, System.currentTimeMillis(), Double.POSITIVE_INFINITY);
        assertThat(metricsStreamBuilder.build().count()).isEqualTo(0);
    }
}
