package com.idenchev.io.output;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TicketFileCreator {
    String ticketDir;

    public TicketFileCreator(String ticketDir) {
        this.ticketDir = ticketDir;
    }

    void createTicketFile(String username, String content) {
        String fileFullPath = String.format("%s%s%s-ticket.txt", ticketDir, File.separator, username);
        try (BufferedWriter fileToWrite = new BufferedWriter(new FileWriter(fileFullPath))) {
            fileToWrite.write(content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
