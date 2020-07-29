package com.idenchev.io.output.template;

import com.idenchev.malware.InfectedUser;

public interface FileReplacementStrategy {
    String getReplacedFileTemplateString(InfectedUser user);
}
