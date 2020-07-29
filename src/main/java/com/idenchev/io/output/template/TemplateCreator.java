package com.idenchev.io.output.template;

import com.idenchev.malware.InfectedUser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public abstract class TemplateCreator {
    protected InputStream defaultTemplateFileStream;
    protected StringBuilder defaultTemplate;
    FileReplacementStrategyFactory fileReplacementStrategyFactory;

    TemplateCreator(InputStream defaultTemplateFileStream,  FileReplacementStrategyFactory fileReplacementStrategyFactory) {
        this.defaultTemplateFileStream = defaultTemplateFileStream;
        this.fileReplacementStrategyFactory = fileReplacementStrategyFactory;
        defaultTemplate = new StringBuilder();
        loadDefaultTemplate();
    }

    public abstract String getReplacedTemplate(InfectedUser infectedUser);

    private void loadDefaultTemplate() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(defaultTemplateFileStream))) {
               String line;
               while ((line = reader.readLine()) != null) {
                   defaultTemplate.append(line);
                   defaultTemplate.append(System.lineSeparator());
               }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void defaultTemplateReplaceAll(String from, String to) {
        int index = defaultTemplate.indexOf(from);
        while (index != -1) {
            defaultTemplate.replace(index, index + from.length(), to);
            index += to.length();
            index = defaultTemplate.indexOf(from, index);
        }
    }
}
