# Ticket Parser
Ticket Parser is a small program to parse monthly server clamscans into separate tickets for each infected cPanel user

## Requirements
 - Java 8 & git installed
 - network connection to maven central repo

## Getting Started
 - Pull repo locally
```bash
git clone https://github.com/i-den/ticket-parser.git
```
 - Place scan file as
```bash
ticket-parser/src/main/resources/scan.txt
```
 - Compile using Maven Wrapper and run
```bash
cd ticket-parser
chmod +x mvnw
./mvnw clean compile assembly:single
java -jar target/ticket-parser-1.0-jar-with-dependencies.jar
``` 

## Running tests
```bash
./mvnw test
```

## Configuration
Currently supports yml file configuration
```bash
File: ticket-parser/src/main/resources/conf/config.yml

ignoreIfBeginsWithAny:
  - /.cagefs/
  - /quarantine_clamavconnector/
  - /tmp/analog/

ignoreIfContainsAny:
  - backupbuddy

ignoreIfEndsWithAny:
  - .apk
  - .wpress
  - .gz
  - .tgz
  - .tar.gz
  - .zip
  - .exe

ignoreCPanelUsers:
  - .jbm
  - solr

shouldUsePasteBinSite: yes
maxScanFiles: 30
apiKey: 

cPanelUserTemplateString: INFECTED_USER_NAME
cPanelUserMalwareFilesString: INFECTED_USER_ALL_FILES
```

# Additional Information
The ticket parser does:
 1. Reads the configuration
 2. Reads the scan.txt file
 3. Parses and groups each cPanel user that has valid (non-filtered by the config) malware files with its files
 4. Replaces the default template with each user's specific username and files
 5. Creates a txt file for each user in ticket-parser/src/main/resources/tickets
 
See docs.md for more
