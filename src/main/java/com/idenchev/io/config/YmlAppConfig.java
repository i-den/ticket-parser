package com.idenchev.io.config;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class YmlAppConfig implements AppConfig {
    private enum YmlAppConfigOptions {
        IGNORE_IF_BEGINS_WITH_ANY("ignoreIfBeginsWithAny"),
        IGNORE_IF_CONTAINS_ANY("ignoreIfContainsAny"),
        IGNORE_IF_ENDS_WITH_ANY("ignoreIfEndsWithAny"),
        IGNORE_CPANEL_USERS("ignoreCPanelUsers"),
        TICKET_SAVE_DIRECTORY("ticketSaveDirectory"),
        SHOULD_USE_FULL_PATH("shouldUseFullPath"),
        SHOULD_USE_PASTEBIN_SITE("shouldUsePasteBinSite"),
        MAX_SCAN_FILES("maxScanFiles"),
        API_KEY("apiKey"),
        CPANEL_USER_TEMPLATE_STRING("cPanelUserTemplateString"),
        CPANEL_USER_MALWARE_FILES_STRING("cPanelUserMalwareFilesString");

        private String configName;

        YmlAppConfigOptions(String configName) {
            this.configName = configName;
        }
    }

    private HashMap<String, Object> configMap;

    public YmlAppConfig(InputStream configFileStream) throws Exception {
        try {
            configMap = new Yaml().load(
                    configFileStream
            );
        } catch (Exception e) {
            throw new Exception("Could not load config file.", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> getIgnoreIfBeginsWithAnyList() {
        List<String> list = (List<String>) configMap.get(YmlAppConfigOptions.IGNORE_IF_BEGINS_WITH_ANY.configName);
        if (list == null)
            return new ArrayList<>();
        return list;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> getIgnoreIfContainsAnyList() {
        List<String> list = (List<String>) configMap.get(YmlAppConfigOptions.IGNORE_IF_CONTAINS_ANY.configName);
        if (list == null)
            return new ArrayList<>();
        return list;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> getIgnoreIfEndsWithAnyList() {
        List<String> list = (List<String>) configMap.get(YmlAppConfigOptions.IGNORE_IF_ENDS_WITH_ANY.configName);
        if (list == null)
            return new ArrayList<>();
        return list;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> getIgnoreCPanelUserList() {
        List<String> list = (List<String>) configMap.get(YmlAppConfigOptions.IGNORE_CPANEL_USERS.configName);
        if (list == null)
            return new ArrayList<>();
        return list;
    }

    @Override
    public String getTicketSaveDirectory() {
        return (String) configMap.get(YmlAppConfigOptions.TICKET_SAVE_DIRECTORY.configName);
    }

    @Override
    public Boolean shouldUseFullPath() {
        return (Boolean) configMap.get(YmlAppConfigOptions.SHOULD_USE_FULL_PATH.configName);
    }

    @Override
    public Boolean shouldUsePasteBinSite() {
        return (Boolean) configMap.get(YmlAppConfigOptions.SHOULD_USE_PASTEBIN_SITE.configName);
    }

    @Override
    public Integer getMaxScanFiles() {
        return (Integer) configMap.get(YmlAppConfigOptions.MAX_SCAN_FILES.configName);
    }

    @Override
    public String getApiKey() {
        return (String) configMap.get(YmlAppConfigOptions.API_KEY.configName);
    }

    @Override
    public String getTemplateUserString() {
        return (String) configMap.get(YmlAppConfigOptions.CPANEL_USER_TEMPLATE_STRING.configName);
    }

    @Override
    public String getTemplateMalwareScanString() {
        return (String) configMap.get(YmlAppConfigOptions.CPANEL_USER_MALWARE_FILES_STRING.configName);
    }
}
