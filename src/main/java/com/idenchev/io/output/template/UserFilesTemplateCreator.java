package com.idenchev.io.output.template;

import com.idenchev.malware.InfectedUser;

import java.io.InputStream;

public class UserFilesTemplateCreator extends TemplateCreator {
    private String usernameTemplateString;
    private String filesTemplateString;

    public UserFilesTemplateCreator(InputStream defaultTemplateFileStream,
                                    FileReplacementStrategyFactory fileReplacementStrategyFactory,
                                    String usernameTemplateString,
                                    String filesTemplateString) {
        super(defaultTemplateFileStream, fileReplacementStrategyFactory);
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
