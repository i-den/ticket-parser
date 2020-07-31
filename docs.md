# How it works
![parser](https://i.imgur.com/mSP3mbx.jpg)

### Main Class
 - TicketParserMain creates a new TicketParser and uses its parse() method
 ```java
public static void main(String[] args) {
     TicketParser ticketParser = new ConsoleTicketParser();
     ticketParser.parse();
}
```

 - The TicketParser is an abstract class using Factory Method for supplying its basic dependencies. The parse() method s the main one started by the app
 ```java
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
``` 

 - The application flow is
```java
public void parse() {
    Set<InfectedUser> infectedUsers = malwareParser.parseInfectedUsers();
    output.createTickets(infectedUsers);
    output.notifyOutcome();
}
```

### Configuration
 - The AppConfig class is an Interface for handling configuration
```java
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
```

 - AppConfig has a single implementation - YmlAppConfig - using .yml file. It uses SnakeYAML (https://bitbucket.org/asomov/snakeyaml/src)
```java
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
    // ... other implementations
}
```
 ### Parsing the scan file
 - After loading the Configuration the application parses the scan.txt file that contains the server clamscan file. It has all cPanel users that have malware in their accounts.
 - From the parsed file it groups each cPanel user with its infected files in the InfectedUser POJO
 - Each InfectedUser POJO is then added in a Set and returned as the file parsing has completed
 - For parsing the file a single interface is used - MalwareParser
```java
public interface MalwareParser {
    Set<InfectedUser> parseInfectedUsers();
}
```
 - The InfectedUser POJO holds the cPanel user and the malware files located in its account
```java
public class InfectedUser {
    String username;
    Set<String> files;

    public InfectedUser(String username) {
        this.username = username;
        files = new HashSet<>();
    }

    public void addFile(String infectedFile) {
        files.add(infectedFile);
    }
    // ... getUsername()
    // ... getFiles()
}
```
 - MalwareParser has a single implementation - FileMalwareParser - parses the users from a scan.txt file
 - It has dependencies on one helper class - LineParser 
```java
public class FileMalwareParser implements MalwareParser {
    InputStream scanFileStream;
    LineParser lineParser;

    public FileMalwareParser(InputStream scanFileStream, LineParser lineParser) {
        this.scanFileStream = scanFileStream;
        this.lineParser = lineParser;
    }

    @Override
    public Set<InfectedUser> parseInfectedUsers() {
        Map<String, InfectedUser> infectedUsers = new HashMap<>();
        parseFile(infectedUsers);
        return new HashSet<>(infectedUsers.values());
    }

    private void parseFile(Map<String, InfectedUser> infectedUsers) {
        try (BufferedReader scanFileReader = new BufferedReader(new InputStreamReader(scanFileStream))) {
            String line;
            while ((line = scanFileReader.readLine()) != null) {
                parseLine(line, infectedUsers);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void parseLine(String line, Map<String, InfectedUser> infectedUsers) {
        lineParser.parseLine(line);
        if (lineParser.lineIsValid()) {
            String username = lineParser.getUsername();
            InfectedUser infectedUser = infectedUsers.get(username);
            if (infectedUser == null) {
                infectedUser = new InfectedUser(username);
                infectedUsers.put(username, infectedUser);
            }
            infectedUser.addFile(lineParser.getInfectedFile());
        }
    }
}
```
#### Parsing line by line
 - The LineParser class parses a line and determines whether its valid. If it is - it knows what the cPanel user and its file in the line are. It's implemented with RegEx
```java
public class LineParserImpl implements LineParser {
    public static class RegExDetails {
        // https://regex101.com/r/vCOUTO/1
        static final String VALID_LINE_REGEX = "(?<infectedFileFullPath>/(?<home>home)/(?<user>.*?)(?<infectedFile>/.*?))(?<separator>:\\s)(?<infectedType>.*)";
        static final Pattern PATTERN = Pattern.compile(VALID_LINE_REGEX);

        public enum RegExGroup {
            INFECTED_FILE_FULL_PATH("infectedFileFullPath"),
            HOME("home"),
            USER("user"),
            INFECTED_FILE("infectedFile"),
            INFECTED_TYPE("infectedType");
            String groupName;

            RegExGroup(String groupName) {
                this.groupName = groupName;
            }

            public String getGroupName() {
                return groupName;
            }
        }
    }

    String line;
    Matcher matcher;
    LineValidatorChain lineValidatorChain;

    public LineParserImpl(LineValidatorChain lineValidatorChain) {
        this.lineValidatorChain = lineValidatorChain;
    }

    @Override
    public void parseLine(String line) {
        this.line = line;
        matcher = RegExDetails.PATTERN.matcher(line);
    }

    @Override
    public boolean lineIsValid() {
        if (matcher.matches())
            return lineValidatorChain.lineIsValid(matcher);
        return false;
    }

    @Override
    public String getUsername() {
        return matcher.group(RegExDetails.RegExGroup.USER.groupName);
    }

    @Override
    public String getInfectedFile() {
        return matcher.group(RegExDetails.RegExGroup.INFECTED_FILE_FULL_PATH.groupName);
    }
}
```
 - If the RegEx matches it then uses a List of Validators to filter the line according to the configuration
```java
public class LineValidatorChain {
    List<Validator> validators;

    public LineValidatorChain() {
        validators = new LinkedList<>();
    }

    public boolean lineIsValid(Matcher matcher) {
        for (Validator validator : validators) {
            if (!validator.lineIsValid(matcher))
                return false;
        }
        return true;
    }

    public void addValidator(Validator validator) {
        validators.add(validator);
    }
}
```

 - Where the Validator is an Interface with a single method:
```java
public interface Validator {
    boolean lineIsValid(Matcher matcher);
}

public abstract class AbstractForbiddenWordsValidator implements Validator {
    protected List<String> forbiddenWords;

    protected AbstractForbiddenWordsValidator(List<String> forbiddenWords) {
        this.forbiddenWords = forbiddenWords;
    }
}

public class BeginsWithValidator extends AbstractForbiddenWordsValidator {
    public BeginsWithValidator(List<String> forbiddenWords) {
        super(forbiddenWords);
    }

    @Override
    public boolean lineIsValid(Matcher matcher) {
        String infectedFile = matcher.group(LineParserImpl.RegExDetails.RegExGroup.INFECTED_FILE.getGroupName());
        for (String forbiddenWord : forbiddenWords) {
            if (infectedFile.startsWith(forbiddenWord))
                return false;
        }
        return true;
    }
}
```
 
 - That's how the helper class parses the line, allowing:
```java
private void parseLine(String line, Map<String, InfectedUser> infectedUsers) {
    lineParser.parseLine(line);
    if (lineParser.lineIsValid()) {
        String username = lineParser.getUsername();
        InfectedUser infectedUser = infectedUsers.get(username);
        if (infectedUser == null) {
            infectedUser = new InfectedUser(username);
            infectedUsers.put(username, infectedUser);
        }
        infectedUser.addFile(lineParser.getInfectedFile());
    }
}
```

### Creating the tickets
 - After the scan.txt is parsed and each user is packed in the Infecteduser POJO they are given to the Output to create the tickets
```java
public void parse() {
    Set<InfectedUser> infectedUsers = malwareParser.parseInfectedUsers();
    output.createTickets(infectedUsers); // <--
    output.notifyOutcome();
}
```
 - Output is an Interface with two methods
```java
public interface Output {
    void createTickets(Set<InfectedUser> infectedUsers);
    void notifyOutcome();
}
```

 - OutputImpl uses two helper classes to parse the template file and then create tickets on the file system
```java
public class OutputImpl implements Output {
    TemplateCreator templateCreator;
    TicketFileCreator ticketFileCreator;

    public OutputImpl(TemplateCreator templateCreator, TicketFileCreator ticketFileCreator) {
        this.templateCreator = templateCreator;
        this.ticketFileCreator = ticketFileCreator;
    }

    @Override
    public void createTickets(Set<InfectedUser> infectedUsers) {
        for (InfectedUser infectedUser : infectedUsers) {
            String replacedTemplate = templateCreator.getReplacedTemplate(infectedUser);
            ticketFileCreator.createTicketFile(infectedUser.getUsername(), replacedTemplate);
        }
    }

    @Override
    public void notifyOutcome() {
        NotifierSingleton.INSTANCE.notifyOutcome();
    }
}
```
 - The two helper classes are TemplateCreator and TicketFileCreator, doing the main logic:
```java
@Override
public void createTickets(Set<InfectedUser> infectedUsers) {
    for (InfectedUser infectedUser : infectedUsers) {
        String replacedTemplate = templateCreator.getReplacedTemplate(infectedUser);
        ticketFileCreator.createTicketFile(infectedUser.getUsername(), replacedTemplate);
    }
}
```
 
 - TemplateCreator is an abstract class, inherited by UserFilesTemplateCreator. It allows for more than just the User and Files to be replaced in a template. The UserFilesTemplateCreator does just that for now
```java
public abstract class TemplateCreator {
    protected InputStream defaultTemplateFileStream;
    protected StringBuilder defaultTemplate;
    FileReplacementStrategyFactory fileReplacementStrategyFactory;

    TemplateCreator(InputStream defaultTemplateFileStream,  FileReplacementStrategyFactory fileReplacementStrategyFactory) {
        this.defaultTemplateFileStream = defaultTemplateFileStream;
        this.fileReplacementStrategyFactory = fileReplacementStrategyFactory;
        defaultTemplate = new StringBuilder();
        loadDefaultTemplate();
    }

    public abstract String getReplacedTemplate(InfectedUser infectedUser);

    private void loadDefaultTemplate() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(defaultTemplateFileStream))) {
               String line;
               while ((line = reader.readLine()) != null) {
                   defaultTemplate.append(line);
                   defaultTemplate.append(System.lineSeparator());
               }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void defaultTemplateReplaceAll(String from, String to) {
        int index = defaultTemplate.indexOf(from);
        while (index != -1) {
            defaultTemplate.replace(index, index + from.length(), to);
            index += to.length();
            index = defaultTemplate.indexOf(from, index);
        }
    }
}
```

 - The UserFilesTemplateCreator takes the Username and the Files strings to be replaced by each cPanel user's respective ones
 - It also takes a ReplacementStrategyFactory to determine what replacement should be done for the files.
 - If a user has more than the allowed maxScanFiles setting an online pastebin site will be used to provide a cleaner ticket. There are some known cases with people having more than 3000 files, good luck reading through it all
```java
public class UserFilesTemplateCreator extends TemplateCreator {
    private String usernameTemplateString;
    private String filesTemplateString;

    public UserFilesTemplateCreator(InputStream defaultTemplateFileStream,
                                    FileReplacementStrategyFactory fileReplacementStrategyFactory,
                                    String usernameTemplateString,
                                    String filesTemplateString) {
        super(defaultTemplateFileStream, fileReplacementStrategyFactory);
        this.usernameTemplateString = usernameTemplateString;
        this.filesTemplateString = filesTemplateString;
    }

    @Override
    public String getReplacedTemplate(InfectedUser infectedUser) {
        String template = defaultTemplate.toString();
        FileReplacementStrategy strategy = fileReplacementStrategyFactory.getStrategy(infectedUser);
        template = template.replaceAll(usernameTemplateString, infectedUser.getUsername());
        template = template.replaceAll(filesTemplateString, strategy.getReplacedFileTemplateString(infectedUser));
        return template.trim();
    }
}
```
 
 - The FileReplacementStrategyFactory
```java
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
```
 
#### After getting a template with the User's specific username and files the template gets saved in a file on the file system
```java
// OutputImpl class
@Override
public void createTickets(Set<InfectedUser> infectedUsers) {
    for (InfectedUser infectedUser : infectedUsers) {
        String replacedTemplate = templateCreator.getReplacedTemplate(infectedUser);
        ticketFileCreator.createTicketFile(infectedUser.getUsername(), replacedTemplate);
    }
}
```

 - The TicketFileCreator is a simple class
```java
public class TicketFileCreator {
    String ticketDir;

    public TicketFileCreator(String ticketDir) {
        this.ticketDir = ticketDir;
    }

    void createTicketFile(String username, String content) {
        String fileFullPath = String.format("%s%s%s-ticket.txt", ticketDir, File.separator, username);
        try (BufferedWriter fileToWrite = new BufferedWriter(new FileWriter(fileFullPath))) {
            fileToWrite.write(content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
```

Finally, there's an output notifier implemented using a Singleton
```java
public interface Notifier {
    void notifyOutcome();
    void addPasteSiteNormal(InfectedUser user);
    void addPasteSiteError(InfectedUser user);
    void addNormal(InfectedUser user);
}

public enum NotifierSingleton implements Notifier {
    INSTANCE;

    private Map<String, Integer> pasteSiteNormals;
    private Map<String, Integer> pasteSiteErrors;
    private Map<String, Integer> normals;


    NotifierSingleton() {
        pasteSiteNormals = new HashMap<>();
        pasteSiteErrors = new HashMap<>();
        normals = new HashMap<>();
    }

    @Override
    public void notifyOutcome() {
        notifyTicketOutcome(pasteSiteErrors, "PasteBin Tickets that didn't get created due to errors:");
        notifyTicketOutcome(pasteSiteNormals, "PasteBin Created Tickets");
        notifyTicketOutcome(normals, "Normal Tickets");
    }

    private void notifyTicketOutcome(Map<String, Integer> map, String msg) {
        if (!map.isEmpty()) {
            System.out.println(msg);
            map.entrySet().stream()
                    .sorted(((o1, o2) -> { // 1st by Val - Desc, then by Key - Asc
                        int valCmp = o2.getValue().compareTo(o1.getValue());
                        if (valCmp == 0) {
                            return o1.getKey().compareTo(o2.getKey());
                        }
                        return valCmp;
                    })) // 16 - cPanel max name len
                    .forEach(e -> System.out.printf("|%16s - %-4d|%n", e.getKey(), e.getValue()));
        }
    }

    @Override
    public void addPasteSiteNormal(InfectedUser user) {
        pasteSiteNormals.put(user.getUsername(), user.getFiles().size());
    }

    @Override
    public void addPasteSiteError(InfectedUser user) {
        pasteSiteErrors.put(user.getUsername(), user.getFiles().size());
    }

    @Override
    public void addNormal(InfectedUser user) {
        normals.put(user.getUsername(), user.getFiles().size());
    }
}
```

Finally, the ConsoleTicketParser initializes all dependencies and returns them for the main method to use.
```java
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

    @Override
    protected Output makeOutput() {
        InputStream defaultTemplateFileStream = getClass().getClassLoader().getResourceAsStream("template.txt");
        FileReplacementStrategyFactory fileReplacementStrategyFactory = new FileReplacementStrategyFactory(appConfig);
        TemplateCreator templateCreator = new UserFilesTemplateCreator(
                defaultTemplateFileStream,
                fileReplacementStrategyFactory,
                appConfig.getTemplateUserString(),
                appConfig.getTemplateMalwareScanString()
        );
        TicketFileCreator ticketFileCreator = new TicketFileCreator(Paths.get("src", "main", "resources", "tickets").toFile().getAbsolutePath());
        return new OutputImpl(templateCreator, ticketFileCreator);
    }
}

public abstract class TicketParser {
    // ...
    public void parse() {
        Set<InfectedUser> infectedUsers = malwareParser.parseInfectedUsers();
        output.createTickets(infectedUsers);
        output.notifyOutcome();
    }
    // ...
}

public class TicketParserMain {
    public static void main(String[] args) {
         TicketParser ticketParser = new ConsoleTicketParser();
         ticketParser.parse();
    }
}
```