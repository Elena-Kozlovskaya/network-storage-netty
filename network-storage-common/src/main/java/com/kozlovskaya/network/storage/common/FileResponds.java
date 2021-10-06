package com.kozlovskaya.network.storage.common;

public class FileResponds extends AbstractMessage{

    private String responds;

    public FileResponds(String responds) {
        this.responds = responds;
    }

    public String getResponds() {
        return responds;
    }


}
