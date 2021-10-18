package com.kozlovskaya.network.storage.common.messages.file;

import com.kozlovskaya.network.storage.common.messages.Request;


//запрос, содержащий файл и команду
public class FileRequest extends Request {

    byte[] fileData;
    private String fileName;
    private long filePosition;
    private long fileSize;
    private String command;

    public FileRequest() {
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFilePosition() {
        return filePosition;
    }

    public void setFilePosition(long filePosition) {
        this.filePosition = filePosition;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public byte[] getFileData() {
        return fileData;
    }

    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
