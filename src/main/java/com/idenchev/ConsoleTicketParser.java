package com.idenchev;

import com.idenchev.io.config.AppConfig;
import com.idenchev.io.config.YmlAppConfig;
import com.idenchev.malware.FileMalwareParser;
import com.idenchev.malware.LineParser;
import com.idenchev.malware.LineParserImpl;
import com.idenchev.malware.MalwareParser;
import com.idenchev.malware.validate.BeginsWithValidator;
import com.idenchev.malware.validate.CPanelUsernameValidator;
import com.idenchev.malware.validate.ContainsValidator;
import com.idenchev.malware.validate.EndsWithValidator;
import com.idenchev.malware.validate.LineValidatorChain;

import java.io.InputStream;

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
        LineValidatorChain lineValidatorChain = new LineValidatorChain();
        lineValidatorChain.addValidator(new CPanelUsernameValidator(appConfig.getIgnoreCPanelUserList()));
        lineValidatorChain.addValidator(new BeginsWithValidator(appConfig.getIgnoreIfBeginsWithAnyList()));
        lineValidatorChain.addValidator(new ContainsValidator(appConfig.getIgnoreIfContainsAnyList()));
        lineValidatorChain.addValidator(new EndsWithValidator(appConfig.getIgnoreIfEndsWithAnyList()));
        LineParser lineParser = new LineParserImpl(lineValidatorChain);
        InputStream scanFileStream = getClass().getClassLoader().getResourceAsStream("scan.txt");
        return new FileMalwareParser(
                scanFileStream,
                lineParser
        );
    }
}
