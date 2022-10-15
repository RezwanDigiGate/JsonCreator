package com.flightschedule.common.util.json.service;

import com.flightschedule.common.util.json.payload.request.ClassNameRequestDto;

public interface JsonCreatorService {
    StringBuilder create(ClassNameRequestDto requestDto);
}
