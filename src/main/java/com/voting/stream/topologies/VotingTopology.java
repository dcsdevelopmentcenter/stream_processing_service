package com.voting.stream.topologies;
/*
 * Copyright (c) 2023 Seiko Epson. All rights reserved.
 */

import com.voting.data.LocationRecord;
import com.voting.data.VotingBallotRecord;
import com.voting.stream.serds.VotingBallotSerde;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class VotingTopology
{
	private static final Serde<String> STRING_SERDE = Serdes.String();

	@Autowired
	public void buildPipelineForState(StreamsBuilder streamsBuilder){
		KStream<String, VotingBallotRecord> messageStream = streamsBuilder
				.stream("BALLOT", Consumed.with(STRING_SERDE,VotingBallotSerde.serdes()));
		KTable<String, Long> kTable = messageStream
				.mapValues(value -> value.candidateRecord().locationRecord().stateName())
				.groupBy((key, value) -> value.toUpperCase(),Grouped.with(STRING_SERDE, STRING_SERDE))
				.count(Materialized.as("votingCountForState"));

		kTable.toStream().to("voting-count");
	}

	@Autowired
	public void buildPipelineVotingCity(StreamsBuilder streamsBuilder){

		KStream<String, VotingBallotRecord> messageStream = streamsBuilder
				.stream("BALLOT", Consumed.with(STRING_SERDE,VotingBallotSerde.serdes()));
		KTable<String, Long> kTable = messageStream
				.mapValues(VotingTopology::buildValue)
				.groupBy((key, value) -> value.toUpperCase(),Grouped.with(STRING_SERDE, STRING_SERDE))
				.count(Materialized.as("votingCountForCity"));

		kTable.toStream().to("voting-count");
	}

	private static String buildValue(final VotingBallotRecord value)
	{
		LocationRecord locationRecord = value.candidateRecord().locationRecord();
		return locationRecord.stateName()+"->"+locationRecord.cityName();
	}

	@Autowired
	void buildPipelineTest(StreamsBuilder streamsBuilder) {
		KStream<String, String> messageStream = streamsBuilder
				.stream("TEST", Consumed.with(STRING_SERDE, STRING_SERDE));

		KTable<String, Long> wordCounts = messageStream
				//.mapValues((ValueMapper<String, String>) String::toLowerCase)
				.flatMapValues(value -> Arrays.asList(value.toLowerCase().split("\\W+")))
				.groupBy((key, word) -> word, Grouped.with(STRING_SERDE, STRING_SERDE))
				.count(Materialized.as("counts"));

		wordCounts.toStream().to("output-topic-long");
	}
}
