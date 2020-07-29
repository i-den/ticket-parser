package com.idenchev.io.config;

import java.util.List;

public interface AppConfig {
    List<String> getIgnoreIfBeginsWithAnyList();
    List<String> getIgnoreIfContainsAnyList();
    List<String> getIgnoreIfEndsWithAnyList();
    List<String> getIgnoreCPanelUserList();
    String getTicketSaveDirectory();
    Boolean shouldUseFullPath();
    Boolean shouldUsePasteBinSite();
    Integer getMaxScanFiles();
    String getApiKey();
    String getTemplateUserString();
    String getTemplateMalwareScanString();
}
