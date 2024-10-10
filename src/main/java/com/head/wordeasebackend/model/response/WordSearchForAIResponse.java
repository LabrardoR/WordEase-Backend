package com.head.wordeasebackend.model.response;


import lombok.Data;

@Data
public class WordSearchForAIResponse {
    private WordSearchResponse wordSearchResponse;
    private String message;
}
