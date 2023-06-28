package com.voting.data;
/*
 * Copyright (c) 2023 Seiko Epson. All rights reserved.
 */

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class VotingBallotSerializer implements Serializer<VotingBallotRecord>
{
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public void configure(final Map<String, ?> configs, final boolean isKey)
	{
		Serializer.super.configure(configs, isKey);
	}

	@Override
	public byte[] serialize(final String topic, final VotingBallotRecord record)
	{
		try
		{
			return objectMapper.writeValueAsBytes(record);
		} catch (JsonProcessingException e)
		{
			throw new SerializationException("Error when serializing MessageDto to byte[]");
		}
	}

	@Override
	public byte[] serialize(final String topic, final Headers headers, final VotingBallotRecord data)
	{
		return Serializer.super.serialize(topic, headers, data);
	}

	@Override
	public void close()
	{
		Serializer.super.close();
	}
}
