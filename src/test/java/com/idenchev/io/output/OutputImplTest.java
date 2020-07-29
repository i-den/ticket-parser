package com.idenchev.io.output;

import com.idenchev.io.config.AppConfig;
import com.idenchev.io.output.template.FileReplacementStrategyFactory;
import com.idenchev.io.output.template.TemplateCreator;
import com.idenchev.io.output.template.UserFilesTemplateCreator;
import com.idenchev.malware.InfectedUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class OutputImplTest {
    OutputImpl output;
    TemplateCreator templateCreator;
    FileReplacementStrategyFactory fileReplacementStrategyFactory;
    TicketFileCreator ticketFileCreator;
    Set<InfectedUser> infectedUserSet;
    File testTicketFileDir;

    @BeforeEach
    void setUp() {
        infectedUserSet = new HashSet<>();

        testTicketFileDir = Paths.get("src", "test", "resources", "tickets").toFile();
        testTicketFileDir.mkdirs();

        ticketFileCreator = new TicketFileCreator(testTicketFileDir.getAbsolutePath());
        fileReplacementStrategyFactory = new FileReplacementStrategyFactory(new AppConfigMock());
        loadTemplate("template.txt");

        output = new OutputImpl(templateCreator, ticketFileCreator);
    }

    @AfterEach
    void teardown() throws IOException {
        Files.walk(testTicketFileDir.toPath())
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }

    @Test
    void givenEmptyList_noFilesShouldBeCreated() {
        output.createTickets(infectedUserSet);
        File[] files = testTicketFileDir.listFiles();
        assertEquals(files.length, 0);
    }

    @Test
    void givenListWith1User_1FileShouldBeCreated() {
        InfectedUser user = new InfectedUser("user");
        user.addFile("com.denchev.malware.php");
        infectedUserSet.add(user);
        output.createTickets(infectedUserSet);
        File[] files = testTicketFileDir.listFiles();
        assertEquals(files.length, 1);
    }

    @Test
    void givenListWith1User_thatShouldUsePastebinSite_1FileShouldBeCreated() {
        InfectedUser user = new InfectedUser("azis");
        for (int i = 0; i < 40; i++) {
            user.addFile("/home/infected-file-" + i + ".php");
        }
        infectedUserSet.add(user);
        output.createTickets(infectedUserSet);
        File[] files = testTicketFileDir.listFiles();
        assertEquals(files.length, 1);
    }

    @Test
    void givenListWith10Users_thatShouldUsePastebinSite_10FilesShouldBeCreated() {
        for (int i = 0; i < 10; i++) {
            InfectedUser user = new InfectedUser("user-" + i);
            for (int j = 0; j < 40; j++) {
                user.addFile("/home/infected-file-" + j + ".php");
            }
            infectedUserSet.add(user);
        }
        output.createTickets(infectedUserSet);
        File[] files = testTicketFileDir.listFiles();
        assertEquals(files.length, 10);
    }

    private void loadTemplate(String template) {
        templateCreator = new UserFilesTemplateCreator(
                getClass().getClassLoader().getResourceAsStream("template/" + template),
                fileReplacementStrategyFactory,
                "INFECTED_USER_NAME",
                "INFECTED_USER_ALL_FILES"
        );
    }

    private static class AppConfigMock implements AppConfig {

        @Override
        public List<String> getIgnoreIfBeginsWithAnyList() {
            return null;
        }

        @Override
        public List<String> getIgnoreIfContainsAnyList() {
            return null;
        }

        @Override
        public List<String> getIgnoreIfEndsWithAnyList() {
            return null;
        }

        @Override
        public List<String> getIgnoreCPanelUserList() {
            return null;
        }

        @Override
        public String getTicketSaveDirectory() {
            return null;
        }

        @Override
        public Boolean shouldUseFullPath() {
            return null;
        }

        @Override
        public Boolean shouldUsePasteBinSite() {
            return null;
        }

        @Override
        public Integer getMaxScanFiles() {
            return 30;
        }

        @Override
        public String getApiKey() {
            return "awBemaNwcy7etBMwELBvYCjobeoxmoUGKUM50TH7Q";
        }

        @Override
        public String getTemplateUserString() {
            return null;
        }

        @Override
        public String getTemplateMalwareScanString() {
            return null;
        }
    }
}