package com.idenchev;

import com.idenchev.io.config.AppConfig;
import com.idenchev.io.config.YmlAppConfig;
import com.idenchev.malware.MalwareParser;

public class ConsoleTicketParser extends TicketParser {

    @Override
    AppConfig makeAppConfig() {
        try {
            return new YmlAppConfig(getClass().getClassLoader().getResourceAsStream("conf/config.yml"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    MalwareParser makeMalwareParser() {
        return null;
    }
}
