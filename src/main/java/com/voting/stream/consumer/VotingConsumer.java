package com.voting.stream.consumer;
/*
 * Copyright (c) 2023 Seiko Epson. All rights reserved.
 */

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class VotingConsumer
{

	@KafkaListener(topics = "output-topic-state", groupId = "group_id")
	public void consumerState(ConsumerRecord<String,Long> record){

		System.out.println("received = " + record.key() + " with value " + record.value());

	}

	@KafkaListener(topics = "output-topic", groupId = "group_id")
	public void consumerCity(ConsumerRecord<String,Long> record){

		System.out.println("received = " + record.key() + " with value " + record.value());

	}

	@KafkaListener(topics = "voting-count", groupId = "group_id")
	public void consumer2(ConsumerRecord<String, Long> record){

		System.out.println("received = " + record.key() + " with value " + record.value());

	}

	@KafkaListener(topics = "TEST", groupId = "group_id")
	public void consumeTest(ConsumerRecord<String, String> record){
		System.out.printf("Received from TEST topic, key[%s] & value[%s] \n",record.key(),record.value());
	}

}
