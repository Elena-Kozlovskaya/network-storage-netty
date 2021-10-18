package com.kozlovskaya.network.storage.common.messages.service;

import com.kozlovskaya.network.storage.common.messages.Request;

public class ServiceRequest extends Request {

    private String fileName;
    private String command;
    private byte[] file;


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }
}
