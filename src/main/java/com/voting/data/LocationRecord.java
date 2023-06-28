package com.voting.data;
/*
 * Copyright (c) 2023 Seiko Epson. All rights reserved.
 */

import java.util.Set;

public record LocationRecord(String id, String cityName, String stateName, Set<CandidateRecord> candidateRecords)
{
}
