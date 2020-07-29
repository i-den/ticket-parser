package com.idenchev.io.output.template;


import com.idenchev.io.output.NotifierSingleton;
import com.idenchev.malware.InfectedUser;


public class JoiningFileReplacementStrategy implements FileReplacementStrategy {
    @Override
    public String getReplacedFileTemplateString(InfectedUser user) {
        NotifierSingleton.INSTANCE.addNormal(user);
        return String.join(System.lineSeparator(), user.getFiles());
    }
}
