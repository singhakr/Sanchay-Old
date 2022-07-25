/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sanchay.servers.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 *
 * @author User
 */
public enum AuthSessionStorage {

    INSTANCE;
    //Used two maps for faster searching and preventing multi-user logins
    //<session id, login>
    private final Map<UUID, String> sessions;
    //<login, session id>
    private final Map<String, UUID> users;

    AuthSessionStorage() {
        sessions = new HashMap<UUID, String>();
        users = new HashMap<String, UUID>();
    }

    public synchronized boolean sessionIdExists(UUID sessionId) {
        return sessions.containsKey(sessionId);
    }

    public synchronized UUID generateSessionId(String login) {
        UUID sessionId = UUID.randomUUID();
        if (users.containsKey(login)) {
            UUID prevSessionId = users.get(login);
            sessions.remove(prevSessionId);
        }
        users.put(login, sessionId);
        sessions.put(sessionId, login);
        return sessionId;
    }
    
    public UUID getSessionId(String username)
    {
        UUID sessionId = users.get(username);

        return sessionId;
    }

    public synchronized void removeSessionId(UUID sessionId) {
        String login = sessions.remove(sessionId);
        users.remove(login);
    }   
}
