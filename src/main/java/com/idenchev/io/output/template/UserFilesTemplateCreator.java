package com.idenchev.io.output.template;

import com.idenchev.malware.InfectedUser;

import java.io.File;

public class UserFilesTemplateCreator extends TemplateCreator {
    private String usernameTemplateString;
    private String filesTemplateString;

    public UserFilesTemplateCreator(File defaultTemplateFile,
                                    FileReplacementStrategyFactory fileReplacementStrategyFactory,
                                    String usernameTemplateString,
                                    String filesTemplateString) {
        super(defaultTemplateFile, fileReplacementStrategyFactory);
        this.usernameTemplateString = usernameTemplateString;
        this.filesTemplateString = filesTemplateString;
    }

    @Override
    public String getReplacedTemplate(InfectedUser infectedUser) {
        FileReplacementStrategy strategy = fileReplacementStrategyFactory.getStrategy(infectedUser);
        defaultTemplateReplaceAll(usernameTemplateString, infectedUser.getUsername());
        defaultTemplateReplaceAll(filesTemplateString, strategy.getReplacedFileTemplateString(infectedUser));
        return defaultTemplate.toString().trim();
    }
}
