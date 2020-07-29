package com.idenchev.io.output;

import com.idenchev.io.output.template.TemplateCreator;
import com.idenchev.malware.InfectedUser;

import java.util.Set;

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
