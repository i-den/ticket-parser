package com.idenchev.io.output.template;

import com.idenchev.io.config.AppConfig;
import com.idenchev.malware.InfectedUser;

public class FileReplacementStrategyFactory {
    AppConfig appConfig;
    PastebinSiteFileReplacementStrategy pastebinSiteFileReplacementStrategy;
    JoiningFileReplacementStrategy joiningFileReplacementStrategy;

    public FileReplacementStrategyFactory(AppConfig appConfig) {
        this.appConfig = appConfig;
        pastebinSiteFileReplacementStrategy = new PastebinSiteFileReplacementStrategy(appConfig.getApiKey());
        joiningFileReplacementStrategy = new JoiningFileReplacementStrategy();
    }

    FileReplacementStrategy getStrategy(InfectedUser user) {
        if (user.getFiles().size() > appConfig.getMaxScanFiles()) {
            return pastebinSiteFileReplacementStrategy;
        } else {
            return joiningFileReplacementStrategy;
        }
    }
}
