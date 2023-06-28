package com.voting.stream.apis;
/*
 * Copyright (c) 2023 Seiko Epson. All rights reserved.
 */

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("${project.base.url}/results")
public class StreamController
{
	@Autowired
	//@Qualifier("streamsBuilderFactoryBean")
	private StreamsBuilderFactoryBean factoryBean;

	@GetMapping("/counts/{state}/{location}")
	@CrossOrigin
	public ResponseEntity<Long> getResultForLocation(@PathVariable String state, @PathVariable String location){

		KafkaStreams kafkaStreams = factoryBean.getKafkaStreams();
		ReadOnlyKeyValueStore<String, Long> counts = kafkaStreams
				.store(StoreQueryParameters.fromNameAndType("votingCountForCity", QueryableStoreTypes.keyValueStore())
		);
		return ResponseEntity.ok(counts.get(String.format("%s->%s",state.toUpperCase(),location.toUpperCase())));
	}

	@GetMapping("/counts/{state}")
	@CrossOrigin
	public ResponseEntity<Long> getResultForState(@PathVariable String state){

		KafkaStreams kafkaStreams = factoryBean.getKafkaStreams();
		ReadOnlyKeyValueStore<String, Long> counts = Optional.ofNullable(kafkaStreams)
				.orElseThrow()
				.store(StoreQueryParameters.fromNameAndType("votingCountForState", QueryableStoreTypes.keyValueStore())
				);

		return ResponseEntity.ok(counts.get(state.toUpperCase()));
	}

}
