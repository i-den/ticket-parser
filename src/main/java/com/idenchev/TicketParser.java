package com.idenchev;

import com.idenchev.io.config.AppConfig;
import com.idenchev.io.output.Output;
import com.idenchev.malware.InfectedUser;
import com.idenchev.malware.MalwareParser;

import java.util.Set;

public abstract class TicketParser {
    protected AppConfig appConfig;
    protected MalwareParser malwareParser;
    protected Output output;

    TicketParser() {
        appConfig = makeAppConfig();
        malwareParser = makeMalwareParser();
        output = makeOutput();
    }

    public void parse() {
        Set<InfectedUser> infectedUsers = malwareParser.parseInfectedUsers();
        output.createTickets(infectedUsers);
        output.notifyOutcome();
    }

    abstract AppConfig makeAppConfig();
    abstract MalwareParser makeMalwareParser();
    protected abstract Output makeOutput();
}
