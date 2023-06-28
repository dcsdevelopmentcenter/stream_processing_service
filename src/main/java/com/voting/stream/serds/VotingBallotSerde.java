package com.voting.stream.serds;
/*
 * Copyright (c) 2023 Seiko Epson. All rights reserved.
 */

import com.voting.data.VotingBallotRecord;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

public final class VotingBallotSerde extends Serdes.WrapperSerde<VotingBallotRecord>
{

	public VotingBallotSerde(){
		super(new JsonSerializer<>(), new JsonDeserializer<>(VotingBallotRecord.class));
	}
	public VotingBallotSerde(final Serializer<VotingBallotRecord> serializer, final Deserializer<VotingBallotRecord> deserializer)
	{
		super(serializer, deserializer);
	}

	public static Serde<VotingBallotRecord> serdes(){
		JsonSerializer<VotingBallotRecord> serializer = new JsonSerializer<>();
		JsonDeserializer<VotingBallotRecord> deserializer = new JsonDeserializer<>(VotingBallotRecord.class);
		return Serdes.serdeFrom(serializer,deserializer);
	}
}
