package com.kozlovskaya.network.storage.common.messages.file;

import com.kozlovskaya.network.storage.common.messages.AbstractMessage;
import com.kozlovskaya.network.storage.common.messages.Request;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileMessage extends Request {

    private String fileName;
    private int partNumber;
    private int partsCount;
    byte[] fileData;
    private String command;

    public FileMessage() {
    }

    @Override
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setPartNumber(int partNumber) {
        this.partNumber = partNumber;
    }

    public void setPartsCount(int partsCount) {
        this.partsCount = partsCount;
    }

    @Override
    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }

    public FileMessage(String fileName, int partNumber, int partsCount, byte[] fileData) {
        this.fileName = fileName;
        this.partNumber = partNumber;
        this.partsCount = partsCount;
        this.fileData = fileData;
    }

    @Override
    public byte[] getFileData() {
        return fileData;
    }


    @Override
    public String getFileName() {
        return fileName;
    }


    public int getPartNumber() {
        return partNumber;
    }


    public int getPartsCount() {
        return partsCount;
    }


    @Override
    public String getCommand() {
        return command;
    }

    @Override
    public void setCommand(String command) {
        this.command = command;
    }
}

