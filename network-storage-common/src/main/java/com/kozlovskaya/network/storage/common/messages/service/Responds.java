package com.kozlovskaya.network.storage.common.messages.service;

import com.kozlovskaya.network.storage.common.messages.AbstractMessage;

public class Responds extends AbstractMessage {

    private String responds;


    public Responds(String responds) {
        this.responds = responds;
    }

    public String getResponds() {
        return responds;
    }
}
