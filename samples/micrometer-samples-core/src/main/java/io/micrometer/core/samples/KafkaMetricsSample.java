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
package io.micrometer.core.samples;

import com.github.charithe.kafka.EphemeralKafkaBroker;
import com.github.charithe.kafka.KafkaHelper;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.kafka.KafkaConsumerMetrics;
import io.micrometer.core.samples.utils.SampleConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import reactor.core.publisher.Flux;

import java.time.Duration;

import static java.util.Collections.singletonList;

public class KafkaMetricsSample {
    private final static String TOPIC = "my-example-topic";

    public static void main(String[] args) throws Exception {
        EphemeralKafkaBroker broker = EphemeralKafkaBroker.create();
        broker.start();
        KafkaHelper kafkaHelper = KafkaHelper.createFor(broker);

        MeterRegistry registry = SampleConfig.myMonitoringSystem();
        new KafkaConsumerMetrics().bindTo(registry);

        KafkaConsumer<String, String> consumer = kafkaHelper.createStringConsumer();
        KafkaProducer<String, String> producer = kafkaHelper.createStringProducer();

        consumer.subscribe(singletonList(TOPIC));

        Flux.interval(Duration.ofMillis(10))
                .doOnEach(n -> producer.send(new ProducerRecord<>(TOPIC, "hello", "world")))
                .subscribe();

        for (; ; ) {
            consumer.poll(Duration.ofMillis(100));
            consumer.commitAsync();
        }
    }
}
