package com.idenchev.io.output;

import com.idenchev.malware.InfectedUser;

import java.util.HashMap;
import java.util.Map;

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
