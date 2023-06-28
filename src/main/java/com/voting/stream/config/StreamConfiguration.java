package com.voting.stream.config;
/*
 * Copyright (c) 2023 Seiko Epson. All rights reserved.
 */

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.config.StreamsBuilderFactoryBeanConfigurer;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.streams.StreamsConfig.APPLICATION_ID_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG;

@Configuration
@EnableKafkaStreams
public class StreamConfiguration
{
	@Value(value = "${spring.kafka.admin.bootstrap.servers}")
	private String bootstrapServers;

	/* Kafka Stream configurations */
	@Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
	public KafkaStreamsConfiguration kafkaStreamsConfiguration(){
		Map<String, Object> props = new HashMap<>();
		props.put(APPLICATION_ID_CONFIG, "voting-streams");
		props.put(BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		props.put(DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
		props.put(DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.Long().getClass().getName());
		return new KafkaStreamsConfiguration(props);

	}

	@Bean
	public StreamsBuilderFactoryBeanConfigurer configurer() {
		return fb -> fb.setStateListener((newState, oldState) -> {
			System.out.println("State transition from " + oldState + " to " + newState);
		});
	}

	/* Kafka Consumer Configurations */

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, Long> kafkaLongListenerContainerFactory(final ConsumerFactory<String, Long> longConsumerFactory) {
		final ConcurrentKafkaListenerContainerFactory<String, Long> factory = new ConcurrentKafkaListenerContainerFactory();
		factory.setConsumerFactory(longConsumerFactory);
		return factory;
	}

	@Bean
	public DefaultKafkaConsumerFactory<String, Long> longConsumerFactory() {
		final Map<String, Object> config = new HashMap<>();
		config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		config.put(ConsumerConfig.GROUP_ID_CONFIG, "group_id");
		config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
		config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
		config.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
		config.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, LongDeserializer.class);

		config.put(JsonDeserializer.TRUSTED_PACKAGES, "com.dcs.voting.stream.data.*, com.voting.data.*, com.voting.data.VotingBallotRecord");
		return new DefaultKafkaConsumerFactory<>(config);
	}

	/* Kafka Producer Configurations */
	@Bean
	public ProducerFactory<String, String> producerFactory() {
		final Map<String, Object> config = new HashMap<>();
		config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		return new DefaultKafkaProducerFactory<>(config);
	}

	@Bean
	public KafkaTemplate<String, String> kafkaTemplate(final ProducerFactory<String, String> producerFactory) {
		return new KafkaTemplate<>(producerFactory);
	}

}
