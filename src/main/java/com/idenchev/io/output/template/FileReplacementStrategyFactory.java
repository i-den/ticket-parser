package com.idenchev.io.output.template;

import com.idenchev.io.config.AppConfig;
import com.idenchev.malware.InfectedUser;
import com.idenchev.io.config.AppConfig;

public class FileReplacementStrategyFactory {
    AppConfig appConfig;

    public FileReplacementStrategyFactory(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    FileReplacementStrategy getStrategy(InfectedUser user) {
        if (user.getFiles().size() > appConfig.getMaxScanFiles()) {
            return new PastebinSiteFileReplacementStrategy(appConfig.getApiKey());
        } else {
            return new JoiningFileReplacementStrategy();
        }
    }
}
