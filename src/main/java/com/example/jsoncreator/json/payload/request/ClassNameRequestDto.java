package com.flightschedule.common.util.json.payload.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ClassNameRequestDto {
    @NotEmpty
    private String requestDtoName;
}
