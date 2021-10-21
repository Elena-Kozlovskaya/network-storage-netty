package com.kozlovskaya.network.storage.common.messages.service;

import com.kozlovskaya.network.storage.common.messages.Response;

public class ServiceResponse extends Response {

    private String response;

    public void setResponse(String response) {
        this.response = response;
    }

    public String getResponse() {
        return response;
    }
}
