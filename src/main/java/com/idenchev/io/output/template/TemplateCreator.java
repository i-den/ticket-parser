package com.idenchev.io.output.template;

import com.idenchev.malware.InfectedUser;
import com.idenchev.malware.InfectedUser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public abstract class TemplateCreator {
    protected File defaultTemplateFile;
    protected StringBuilder defaultTemplate;
    FileReplacementStrategyFactory fileReplacementStrategyFactory;

    TemplateCreator(File defaultTemplateFile,  FileReplacementStrategyFactory fileReplacementStrategyFactory) {
        this.defaultTemplateFile = defaultTemplateFile;
        this.fileReplacementStrategyFactory = fileReplacementStrategyFactory;
        defaultTemplate = new StringBuilder();
        loadDefaultTemplate();
    }

    public abstract String getReplacedTemplate(InfectedUser infectedUser);

    private void loadDefaultTemplate() {
        try (BufferedReader reader = new BufferedReader(new FileReader(defaultTemplateFile))) {
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
