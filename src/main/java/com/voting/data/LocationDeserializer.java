package com.voting.data;
/*
 * Copyright (c) 2023 Seiko Epson. All rights reserved.
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Map;

public class LocationDeserializer implements Deserializer<LocationRecord>
{
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public void configure(final Map<String, ?> configs, final boolean isKey)
	{
		Deserializer.super.configure(configs, isKey);
	}

	@Override
	public LocationRecord deserialize(final String topic, final byte[] data)
	{
		int id;
		int nameSize;
		String name;
		try{
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
			name = new String(nameBytes, "UTF-8");
			return  new LocationRecord("1",name,"", null);
		} catch (UnsupportedEncodingException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public LocationRecord deserialize(final String topic, final Headers headers, final byte[] data)
	{
		return Deserializer.super.deserialize(topic, headers, data);
	}
}
