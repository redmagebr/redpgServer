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
    private Set<Session> sessions = Collections.synchronizedSet(new HashSet<Session>());
    
    
    public void addSession (Session session) {
        this.sessions.add(session);
    }
    
    public void removeSession (Session session) {
        this.sessions.remove(session);
    }
    
    public Set<Session> getSessions () {
        return this.sessions;
    }
    
    public int sessionsNumber () {
        return this.sessions.size();
    }
}
