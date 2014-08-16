/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package kinds;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.websocket.Session;

/**
 *
 * @author reddo
 */
public class UsuarioSocket extends UsuarioChat {
    private final Set<Session> sessions = Collections.synchronizedSet(new HashSet<Session>());
    private boolean online;

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }
    
    public synchronized void addSession (Session session) {
        this.sessions.add(session);
        if (!this.online) {
            this.online = true;
        }
    }
    
    public synchronized void removeSession (Session session) {
        this.sessions.remove(session);
        if (this.sessions.size() < 1) {
            this.online = false;
        }
    }
    
    public Set<Session> getSessions () {
        return this.sessions;
    }
}
