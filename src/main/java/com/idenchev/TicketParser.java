package com.idenchev;

import com.idenchev.io.config.AppConfig;

public abstract class TicketParser {
    AppConfig appConfig;

    TicketParser() {
        appConfig = makeAppConfig();
    }

    public void parse() {

    }

    abstract AppConfig makeAppConfig();
}
