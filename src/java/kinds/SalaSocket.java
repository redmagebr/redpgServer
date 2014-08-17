/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package kinds;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import javax.websocket.Session;

/**
 *
 * @author reddo
 */
public class SalaSocket {
    private volatile ConcurrentHashMap<Integer, UsuarioSocket> users = new ConcurrentHashMap<Integer, UsuarioSocket>();
    private volatile AtomicInteger fakeCount = new AtomicInteger();
    private volatile Set<Session> sessions = Collections.synchronizedSet(new HashSet<Session>());
    private volatile String jsonMemory;
    
    private void addUser (UsuarioSocket user) {
        users.put(user.getId(), user);
    }
    
    private void removeUser (int userid) {
        users.remove(userid);
    }
    
    private boolean addSession (int userid, Session session) {
        if (!users.containsKey(userid)) {
            return false;
        }
        UsuarioSocket user = users.get(userid);
        user.addSession(session);
        sessions.add(session);
        return true;
    }
    
    private boolean removeSession (int userid, Session session) {
        if (!users.containsKey(userid)) {
            return false;
        }
        UsuarioSocket user = users.get(userid);
        user.removeSession(session);
        sessions.remove(session);
        return true;
    }
    
    private UsuarioSocket getUser (int userid) {
        return users.get(userid);
    }
    
    private ConcurrentHashMap<Integer, UsuarioSocket> getUsers () {
        return users;
    }
    
    private Set<Session> getSessions () {
        return sessions;
    }
    
    public int getFakeId () {
        return fakeCount.decrementAndGet();
    }

    public String getJsonMemory() {
        return jsonMemory;
    }

    public void setJsonMemory(String jsonMemory) {
        this.jsonMemory = jsonMemory;
    }
}