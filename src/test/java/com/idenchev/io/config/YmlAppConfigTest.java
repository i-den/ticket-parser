package com.idenchev.io.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.io.FileInputStream;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class YmlAppConfigTest {

    AppConfig appConfigTestable;

    @BeforeEach
    void setUp() {
    }

    @Test
    void givenRealConfigFilePath_shouldLoadSuccessfully() {
        assertDoesNotThrow(() -> {
            appConfigTestable = new YmlAppConfig(
                    YmlAppConfig.class.getClassLoader().getResourceAsStream("conf/config.yml")
            );
        });
    }

    @Test
    void givenTestConfigFilePath_shouldLoadSuccessfully() {
        assertDoesNotThrow(() -> {
            appConfigTestable = new YmlAppConfig(
                    new FileInputStream(getTestableConfigFile("config.yml"))
            );
        });
    }

    @Test
    void givenFaultyConfigFilePath_shouldThrow() {
        assertThrows(Exception.class, () ->
                appConfigTestable = new YmlAppConfig(YmlAppConfig.class.getClassLoader().getResourceAsStream(
                        "music/azis/albums/together/ledena_kralica(rafter_data_bg).mp3"
                ))
        );
    }

    // ignoreIfBeginsWithAny
    @Test
    void givenEmptyIgnoreBeginsList_shouldReturnNull() throws Exception {
        appConfigTestable = new YmlAppConfig(new FileInputStream(getTestableConfigFile("config_empty.yml")));
        assertNull(appConfigTestable.getIgnoreIfBeginsWithAnyList());
    }

    @Test
    void given1IgnoreBeginsList_shouldReturn1() throws Exception {
        appConfigTestable = new YmlAppConfig(new FileInputStream(getTestableConfigFile("config_1_setting_each.yml")));
        assertEquals(appConfigTestable.getIgnoreIfBeginsWithAnyList().size(), 1);
    }

    @Test
    void givenSingleStringIgnoreBeginsList_shouldReturnTheString() throws Exception {
        appConfigTestable = new YmlAppConfig(new FileInputStream(getTestableConfigFile("config_1_setting_each.yml")));
        assertEquals(appConfigTestable.getIgnoreIfBeginsWithAnyList().get(0), "test");
    }

    @Test
    void givenMultipleStringsIgnoreBeginsList_shouldReturnCorrectStrings() throws Exception {
        appConfigTestable = new YmlAppConfig(new FileInputStream(getTestableConfigFile("config_multiple_setting_each.yml")));
        List<String> list = Arrays.asList("test1", "test2", "test3");
        assertEquals(appConfigTestable.getIgnoreIfBeginsWithAnyList(), list);
    }
    // ignoreIfBeginsWithAny
    // ignoreIfContainsAny
    @Test
    void givenEmptyIgnoreContainsList_shouldReturnNull() throws Exception {
        appConfigTestable = new YmlAppConfig(new FileInputStream(getTestableConfigFile("config_empty.yml")));
        assertNull(appConfigTestable.getIgnoreIfContainsAnyList());
    }

    @Test
    void given1IgnoreContainsList_shouldReturn1() throws Exception {
        appConfigTestable = new YmlAppConfig(new FileInputStream(getTestableConfigFile("config_1_setting_each.yml")));
        assertEquals(appConfigTestable.getIgnoreIfContainsAnyList().size(), 1);
    }

    @Test
    void givenSingleStringIgnoreContainsList_shouldReturnTheString() throws Exception {
        appConfigTestable = new YmlAppConfig(new FileInputStream(getTestableConfigFile("config_1_setting_each.yml")));
        assertEquals(appConfigTestable.getIgnoreIfContainsAnyList().get(0), "test");
    }

    @Test
    void givenMultipleStringsIgnoreContainsList_shouldReturnCorrectStrings() throws Exception {
        appConfigTestable = new YmlAppConfig(new FileInputStream(getTestableConfigFile("config_multiple_setting_each.yml")));
        List<String> list = Arrays.asList("test1", "test2", "test3");
        assertEquals(appConfigTestable.getIgnoreIfContainsAnyList(), list);
    }
    // ignoreIfContainsAny
    // ignoreIfEndsWithAny
    @Test
    void givenEmptyIgnoreEndsList_shouldReturnNull() throws Exception {
        appConfigTestable = new YmlAppConfig(new FileInputStream(getTestableConfigFile("config_empty.yml")));
        assertNull(appConfigTestable.getIgnoreIfEndsWithAnyList());
    }

    @Test
    void given1IgnoreEndsList_shouldReturn1() throws Exception {
        appConfigTestable = new YmlAppConfig(new FileInputStream(getTestableConfigFile("config_1_setting_each.yml")));
        assertEquals(appConfigTestable.getIgnoreIfEndsWithAnyList().size(), 1);
    }

    @Test
    void givenSingleStringIgnoreEndsList_shouldReturnTheString() throws Exception {
        appConfigTestable = new YmlAppConfig(new FileInputStream(getTestableConfigFile("config_1_setting_each.yml")));
        assertEquals(appConfigTestable.getIgnoreIfEndsWithAnyList().get(0), "test");
    }

    @Test
    void givenMultipleStringsIgnoreEndsList_shouldReturnCorrectStrings() throws Exception {
        appConfigTestable = new YmlAppConfig(new FileInputStream(getTestableConfigFile("config_multiple_setting_each.yml")));
        List<String> list = Arrays.asList("test1", "test2", "test3");
        assertEquals(appConfigTestable.getIgnoreIfEndsWithAnyList(), list);
    }
    // ignoreIfEndsWithAny
    // ignoreCPanelUserList
    @Test
    void givenEmptyIgnoreCPanelUserList_shouldReturnNull() throws Exception {
        appConfigTestable = new YmlAppConfig(new FileInputStream(getTestableConfigFile("config_empty.yml")));
        assertNull(appConfigTestable.getIgnoreCPanelUserList());
    }

    @Test
    void given1IgnoreCPanelUserList_shouldReturn1() throws Exception {
        appConfigTestable = new YmlAppConfig(new FileInputStream(getTestableConfigFile("config_1_setting_each.yml")));
        assertEquals(appConfigTestable.getIgnoreCPanelUserList().size(), 1);
    }

    @Test
    void givenSingleStringIgnoreCPanelUserList_shouldReturnTheString() throws Exception {
        appConfigTestable = new YmlAppConfig(new FileInputStream(getTestableConfigFile("config_1_setting_each.yml")));
        assertEquals(appConfigTestable.getIgnoreCPanelUserList().get(0), "test");
    }

    @Test
    void givenMultipleStringsIgnoreCPanelUserList_shouldReturnCorrectStrings() throws Exception {
        appConfigTestable = new YmlAppConfig(new FileInputStream(getTestableConfigFile("config_multiple_setting_each.yml")));
        List<String> list = Arrays.asList("test1", "test2", "test3");
        assertEquals(appConfigTestable.getIgnoreCPanelUserList(), list);
    }
    // ignoreCPanelUserList
    // shouldUseFullPath
    @Test
    void givenEmptyShouldUseFullPath_shouldReturnNull() throws Exception {
        appConfigTestable = new YmlAppConfig(new FileInputStream(getTestableConfigFile("config_empty.yml")));
        assertNull(appConfigTestable.shouldUseFullPath());
    }

    @Test
    void givenYesShouldUseFullPath_shouldReturnTrue() throws Exception {
        appConfigTestable = new YmlAppConfig(new FileInputStream(getTestableConfigFile("config_yes.yml")));
        assertTrue(appConfigTestable.shouldUseFullPath());
    }

    @Test
    void givenTrueShouldUseFullPath_shouldReturnTrue() throws Exception {
        appConfigTestable = new YmlAppConfig(new FileInputStream(getTestableConfigFile("config_true.yml")));
        assertTrue(appConfigTestable.shouldUseFullPath());
    }

    @Test
    void givenOnShouldUseFullPath_shouldReturnTrue() throws Exception {
        appConfigTestable = new YmlAppConfig(new FileInputStream(getTestableConfigFile("config_on.yml")));
        assertTrue(appConfigTestable.shouldUseFullPath());
    }

    @Test
    void givenNoShouldUseFullPath_shouldReturnFalse() throws Exception {
        appConfigTestable = new YmlAppConfig(new FileInputStream(getTestableConfigFile("config_no.yml")));
        assertFalse(appConfigTestable.shouldUseFullPath());
    }

    @Test
    void givenFalseShouldUseFullPath_shouldReturnFalse() throws Exception {
        appConfigTestable = new YmlAppConfig(new FileInputStream(getTestableConfigFile("config_false.yml")));
        assertFalse(appConfigTestable.shouldUseFullPath());
    }

    @Test
    void givenOffShouldUseFullPath_shouldReturnFalse() throws Exception {
        appConfigTestable = new YmlAppConfig(new FileInputStream(getTestableConfigFile("config_off.yml")));
        assertFalse(appConfigTestable.shouldUseFullPath());
    }
    // shouldUseFullPath
    // shouldUsePasteBinSite
    @Test
    void givenEmptyShouldUsePasteBinSite_shouldReturnNull() throws Exception {
        appConfigTestable = new YmlAppConfig(new FileInputStream(getTestableConfigFile("config_empty.yml")));
        assertNull(appConfigTestable.shouldUsePasteBinSite());
    }

    @Test
    void givenYesShouldUsePasteBinSite_shouldReturnTrue() throws Exception {
        appConfigTestable = new YmlAppConfig(new FileInputStream(getTestableConfigFile("config_yes.yml")));
        assertTrue(appConfigTestable.shouldUsePasteBinSite());
    }

    @Test
    void givenTrueShouldUsePasteBinSite_shouldReturnTrue() throws Exception {
        appConfigTestable = new YmlAppConfig(new FileInputStream(getTestableConfigFile("config_true.yml")));
        assertTrue(appConfigTestable.shouldUsePasteBinSite());
    }

    @Test
    void givenOnShouldUsePasteBinSite_shouldReturnTrue() throws Exception {
        appConfigTestable = new YmlAppConfig(new FileInputStream(getTestableConfigFile("config_on.yml")));
        assertTrue(appConfigTestable.shouldUsePasteBinSite());
    }

    @Test
    void givenNoShouldUsePasteBinSite_shouldReturnFalse() throws Exception {
        appConfigTestable = new YmlAppConfig(new FileInputStream(getTestableConfigFile("config_no.yml")));
        assertFalse(appConfigTestable.shouldUsePasteBinSite());
    }

    @Test
    void givenFalseShouldUsePasteBinSite_shouldReturnFalse() throws Exception {
        appConfigTestable = new YmlAppConfig(new FileInputStream(getTestableConfigFile("config_false.yml")));
        assertFalse(appConfigTestable.shouldUsePasteBinSite());
    }

    @Test
    void givenOffShouldUsePasteBinSite_shouldReturnFalse() throws Exception {
        appConfigTestable = new YmlAppConfig(new FileInputStream(getTestableConfigFile("config_off.yml")));
        assertFalse(appConfigTestable.shouldUsePasteBinSite());
    }
    // shouldUsePasteBinSite
    // ticketSaveDirectory
    @Test
    void givenEmptyTicketSaveDirectory_shouldReturnNull() throws Exception {
        appConfigTestable = new YmlAppConfig(new FileInputStream(getTestableConfigFile("config_empty.yml")));
        assertNull(appConfigTestable.getTicketSaveDirectory());
    }

    @Test
    void givenStringTicketSaveDirectory_shouldReturnTheString() throws Exception {
        appConfigTestable = new YmlAppConfig(new FileInputStream(getTestableConfigFile("config_1_setting_each.yml")));
        assertEquals(appConfigTestable.getTicketSaveDirectory(), "scanTickets");
    }
    // ticketSaveDirectory
    // maxScanFiles
    @Test
    void givenEmptyMaxScanFiles_shouldReturnNull() throws Exception {
        appConfigTestable = new YmlAppConfig(new FileInputStream(getTestableConfigFile("config_empty.yml")));
        assertNull(appConfigTestable.getMaxScanFiles());
    }
    @Test
    void given30IntegerMaxScanFiles_shouldReturnTheInteger() throws Exception {
        appConfigTestable = new YmlAppConfig(new FileInputStream(getTestableConfigFile("config_1_setting_each.yml")));
        assertEquals(appConfigTestable.getMaxScanFiles(), 30);
    }
    // maxScanFiles
    // apiKey
    @Test
    void givenEmptyApiKey_shouldReturnNull() throws Exception {
        appConfigTestable = new YmlAppConfig(new FileInputStream(getTestableConfigFile("config_empty.yml")));
        assertNull(appConfigTestable.getApiKey());
    }
    @Test
    void givenStringApiKey_shouldReturnTheString() throws Exception {
        appConfigTestable = new YmlAppConfig(new FileInputStream(getTestableConfigFile("config_1_setting_each.yml")));
        assertEquals(appConfigTestable.getApiKey(), "myApiKey");
    }
    // apiKey
    // cPanelUserTemplateString
    @Test
    void givenEmptyCPanelUserTemplateString_shouldReturnNull() throws Exception {
        appConfigTestable = new YmlAppConfig(new FileInputStream(getTestableConfigFile("config_empty.yml")));
        assertNull(appConfigTestable.getTemplateUserString());
    }
    @Test
    void givenStringCPanelUserTemplateString_shouldReturnTheString() throws Exception {
        appConfigTestable = new YmlAppConfig(new FileInputStream(getTestableConfigFile("config_1_setting_each.yml")));
        assertEquals(appConfigTestable.getTemplateUserString(), "INFECTED_USER_NAME");
    }
    // cPanelUserTemplateString
    // cPanelUserMalwareFilesString
    @Test
    void givenEmptyCPanelUserMalwareFilesString_shouldReturnNull() throws Exception {
        appConfigTestable = new YmlAppConfig(new FileInputStream(getTestableConfigFile("config_empty.yml")));
        assertNull(appConfigTestable.getTemplateMalwareScanString());
    }
    @Test
    void givenStringCPanelUserMalwareFilesString_shouldReturnTheString() throws Exception {
        appConfigTestable = new YmlAppConfig(new FileInputStream(getTestableConfigFile("config_1_setting_each.yml")));
        assertEquals(appConfigTestable.getTemplateMalwareScanString(), "INFECTED_USER_ALL_FILES");
    }
    // cPanelUserMalwareFilesString



    private String getTestableConfigFile(String filename) {
        return Paths.get("src", "test", "resources", "conf", filename).toFile().getAbsolutePath();
    }
}