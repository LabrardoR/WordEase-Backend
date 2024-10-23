package com.head.wordeasebackend.model.response;


import lombok.Data;

@Data
public class WordQueryForAIResponse {
    private WordQueryResponse wordQueryResponse;
    private String message;
}
