package com.example.TaskApp.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response<T> {

    @JsonProperty("statusCode")
    private int statusCode;

    @JsonProperty("message")
    private String message;

    @JsonProperty("data")
    private T data;
}