package com.idenchev;

import com.idenchev.io.config.AppConfig;
import com.idenchev.malware.InfectedUser;
import com.idenchev.malware.MalwareParser;

import java.util.Set;

public abstract class TicketParser {
    protected AppConfig appConfig;
    protected MalwareParser malwareParser;

    TicketParser() {
        appConfig = makeAppConfig();
        malwareParser = makeMalwareParser();
    }

    public void parse() {
        Set<InfectedUser> infectedUsers = malwareParser.parseInfectedUsers();
    }

    abstract AppConfig makeAppConfig();
    abstract MalwareParser makeMalwareParser();
}
