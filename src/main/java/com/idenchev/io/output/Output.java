package com.idenchev.io.output;

import com.idenchev.malware.InfectedUser;

import java.util.Set;

public interface Output {
    void createTickets(Set<InfectedUser> infectedUsers);
    void notifyOutcome();
}
