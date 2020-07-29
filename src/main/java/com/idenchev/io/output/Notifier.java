package com.idenchev.io.output;

import com.idenchev.malware.InfectedUser;

public interface Notifier {
    void notifyOutcome();
    void addPasteSiteNormal(InfectedUser user);
    void addPasteSiteError(InfectedUser user);
    void addNormal(InfectedUser user);
}
