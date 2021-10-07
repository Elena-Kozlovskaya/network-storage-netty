package com.kozlovskaya.network.storage.common.messages.file;

import com.kozlovskaya.network.storage.common.messages.AbstractMessage;

public class FileResponds extends AbstractMessage {

    private String filename;
    private long fileLength;
    byte[] fileData;

    private String command;

    public FileResponds(String filename, long fileLength, byte[] fileData, String command) {
        this.filename = filename;
        this.fileLength = fileLength;
        this.fileData = fileData;
        this.command = command;
    }

    public String getFilename() {
        return filename;
    }

    public long getFileLength() {
        return fileLength;
    }

    public byte[] getFileData() {
        return fileData;
    }

    public String getCommand() {
        return command;
    }
}
