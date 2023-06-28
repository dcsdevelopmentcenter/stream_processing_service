package com.voting.data;
/*
 * Copyright (c) 2023 Seiko Epson. All rights reserved.
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;

import java.nio.ByteBuffer;
import java.util.Map;

public class VotingBallotDeserializer implements Deserializer<VotingBallotRecord>
{
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public void configure(final Map<String, ?> configs, final boolean isKey)
	{
		Deserializer.super.configure(configs, isKey);
	}

	@Override
	public VotingBallotRecord deserialize(final String topic, final byte[] data)
	{
		int id;
		int nameSize;
		CandidateRecord candidateRecord;

			if(data == null){
				return null;
			}
			if(data.length < 8){
				throw new SerializationException("Size of data received by deserialiser is shorter than expected");
			}

			ByteBuffer buffer = ByteBuffer.wrap(data);
			id = buffer.getInt();
			nameSize = buffer.getInt();

			byte[] nameBytes = new byte[nameSize];
			buffer.get(nameBytes);
			candidateRecord = new CandidateRecord(null,null,null,null);
			return  new VotingBallotRecord(null);

	}

	@Override
	public VotingBallotRecord deserialize(final String topic, final Headers headers, final byte[] data)
	{
		return Deserializer.super.deserialize(topic, headers, data);
	}
}
