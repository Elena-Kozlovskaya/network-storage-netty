package com.kozlovskaya.network.storage.common.messages.file;

import com.kozlovskaya.network.storage.common.messages.AbstractMessage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

//запрос от клиента серверу
public class FileRequest extends AbstractMessage {

    private String filename;
    private long fileLength;
    byte[] fileData;

    private final String command;

    public FileRequest(Path path, String command) throws IOException {
        filename = path.getFileName().toString();
        fileLength = Files.size(path);
        fileData = Files.readAllBytes(path);
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
