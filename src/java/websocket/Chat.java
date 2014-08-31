/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package websocket;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import dao.MessageDAO;
import dao.SalaDAO;
import dao.UsuarioDAO;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.HttpSession;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import kinds.Message;
import kinds.SalaSocket;
import kinds.UsuarioSocket;
import sistema.GsonFactory;
import sistema.ServletAwareConfig;

/**
 *
 * @author reddo
 */
@ServerEndpoint(value="/Chat", configurator=ServletAwareConfig.class)
public class Chat {
    private volatile static ConcurrentHashMap rooms = new ConcurrentHashMap<Integer, SalaSocket>();
    private static Object changingRooms = new Object();
    private Integer roomid;
    private Integer userid;
    
    @OnOpen
    public void onOpen (Session peer, EndpointConfig config) {
        HttpSession session = ((HttpSession) config.getUserProperties().get("httpSession"));
        peer.setMaxIdleTimeout(15000);
        if (session != null) {
            this.userid = (Integer) session.getAttribute("userid");
        }
        if (this.userid == null) {
            try {
                peer.close();
            } catch (IOException ex) { }
        }
    }
    
    @OnMessage
    public String onMessage(String message, Session peer) {
        if (message.equals("0")) {
            return "1";
        }
        int delimiter = message.indexOf(";");
        if (delimiter < 0 || (delimiter + 1) >= message.length()) {
            return null;
        }
        String action = message.substring(0, delimiter);
        message = message.substring(delimiter + 1, message.length());
        if (action.equals("room")) {
            if (this.roomid != null) {
                leaveRoom(this.userid, this.roomid, peer);
            }
            try {
                this.roomid = Integer.parseInt(message);
            } catch (NumberFormatException e) {
                return null;
            }
            if (!enterRoom(this.userid, this.roomid, peer)) {
                try {
                    peer.close();
                } catch (IOException ex) {}
                return null;
            }
            return "[\"getroom\"," + GsonFactory.getFactory().getGsonExposed().toJson(((SalaSocket) rooms.get(this.roomid)).getUsers()) + "," + ((SalaSocket) rooms.get(this.roomid)).getJsonMemory() + "]";
        } else if (action.equals("status")) {
            setStatus(this.userid, this.roomid, message);
        } else if (action.equals("message")) {
            sendMessage(userid, roomid, peer, message);
        } else if (action.equals("persona")) {
            setPersona(userid, roomid, peer, message);
        } else if (action.equals("memory")) {
            setMemory(userid, roomid, peer, message);
        } else if (action.equals("users") && message.equals("1")) {
            return "[\"getroom\"," + GsonFactory.getFactory().getGsonExposed().toJson(((SalaSocket) rooms.get(this.roomid)).getUsers()) + "]";
        }
        if (this.roomid == null) {
            return null;
        }
        
        return null;
    }
    
    @OnClose
    public void OnClose (Session peer) {
        if (this.roomid == null) {
            return;
        }
        leaveRoom (userid, roomid, peer);
    }
    
    public static void leaveRoom (int userid, int roomid, Session peer) {
        SalaSocket room;
        synchronized (changingRooms) {
            room = (SalaSocket) rooms.get(roomid);
            synchronized (room) {
                room.removeSession(userid, peer);
                if (room.sessionSize() < 1) {
                    rooms.remove(roomid);
                } else if (room.getUser(userid).isOnline()) {
                    return;
                }
            }
        }
        Set<Session> sessions = room.getSessions();
        synchronized (sessions) {
            for (Session session : sessions) {
                try {
                    session.getBasicRemote().sendText("[\"left\"," + userid + "]");
                } catch (IOException e) {}
            }
        }
    }
    
    public static boolean enterRoom (int userid, int roomid, Session peer) {
        SalaSocket room = (SalaSocket) rooms.get(roomid);
        
        if (room == null) {
            room = SalaDAO.getSalaSocket(roomid);
        }
        
        synchronized (changingRooms) {
            if (rooms.putIfAbsent(roomid, room) != null) room = (SalaSocket) rooms.get(roomid);
            synchronized (room) {
                if (!room.addSession(userid, peer)) {
                    if (room.sessionSize() < 1) {
                        rooms.remove(roomid);
                    }
                    return false;
                }
                if (room.getUser(userid).sessionsSize() != 1) {
                    return true;
                }
            }
        }
        
        String userJson = GsonFactory.getFactory().getGsonExposed().toJson(room.getUser(userid));
        Set<Session> sessions = room.getSessions();
        synchronized (sessions) {
            for (Session other : sessions) {
                if (other.equals(peer)) continue;
                try {
                    other.getBasicRemote().sendText("[\"joined\"," + userJson + "]");
                } catch (IOException e) {}
            }
        }
        
        return true;
    }
    
    public static void setStatus (int userid, int roomid, String message) {
        String[] status = message.split(",");
        if (status.length != 3) {
            return;
        }
        UsuarioSocket user = ((SalaSocket) rooms.get(roomid)).getUser(userid);
        user.setTyping(status[0].equals("1"));
        user.setIdle(status[1].equals("1"));
        user.setFocused(status[2].equals("1"));
        Set<Session> sessions = ((SalaSocket) rooms.get(roomid)).getSessions();
        synchronized (sessions) {
            for (Session other : sessions) {
                try {
                    other.getBasicRemote().sendText("[\"status\"," + userid + "," + 
                                                    (user.isTyping() ? "1" : "0") + "," + 
                                                    (user.isIdle() ? "1" : "0") + "," + 
                                                    (user.isFocused() ? "1" : "0") + "]");
                } catch (IOException ex) { }
            }
        }
    }
    
    public static void setPersona (int userid, int roomid, Session peer, String message) {
        try {
            Gson gson = GsonFactory.getFactory().getGson();
            JsonObject personaJSON = gson.fromJson(message, JsonObject.class);
            
            SalaSocket room = (SalaSocket) rooms.get(roomid);
            UsuarioSocket user = room.getUser(userid);
            
            String persona = null;
            String avatar = null;
            
            if (personaJSON.has("persona") && !personaJSON.get("persona").isJsonNull()) {
                persona = (personaJSON.get("persona").getAsString());
            }
            
            if (personaJSON.has("avatar") && !personaJSON.get("avatar").isJsonNull()) {
                avatar = (personaJSON.get("avatar").getAsString());
            }
            
            user.setAvatar(avatar);
            user.setPersona(persona);
            
            String stringified = "[\"persona\","
                    + userid + ",{\"persona\":" + gson.toJson(persona)
                    + ",\"avatar\":" + gson.toJson(avatar) + "}]";
            
            Set<Session> sessions = room.getSessions();
            synchronized (sessions) {
                for (Session other : sessions) {
                    try {
                        other.getBasicRemote().sendText(stringified);
                    } catch (IOException ex) { }
                }
            }
        } catch (JsonSyntaxException e) {
            
        }
    }
    
    public static void sendMessage (int userid, int roomid, Session peer, String msgJSON) {
        try {
            SalaSocket room = (SalaSocket) rooms.get(roomid);
            if (room == null) {
                peer.close();
                return;
            }
            UsuarioSocket user = (UsuarioSocket) (room.getUser(userid));
            
            Date today = new Date(System.currentTimeMillis());
            Gson gson = GsonFactory.getFactory().getGson();

            Message message = new Message();
            JsonObject messageJson = gson.fromJson(msgJSON, JsonObject.class);
            if (!messageJson.has("message") || !messageJson.has("module") || !messageJson.has("special")) {
                peer.close();
            }

            if (messageJson.has("localid") && !messageJson.get("localid").isJsonNull()) {
                message.setLocalid(messageJson.get("localid").getAsBigDecimal());
            }
            message.setModule(messageJson.get("module").getAsString());
            message.setOrigin(userid);
            if (messageJson.has("destination") && !messageJson.get("destination").isJsonNull()) {
                if (messageJson.get("destination").isJsonArray()) {
                    ArrayList<Integer> destinations = new ArrayList<Integer>();
                    JsonArray destinationArray = messageJson.get("destination").getAsJsonArray();
                    for (int roller = 0; roller < destinationArray.size(); roller++) {
                        destinations.add(destinationArray.get(roller).getAsInt());
                    }
                    message.setDestinations(destinations);
                } else {
                    message.setDestination(messageJson.get("destination").getAsInt());
                }
            }
            message.setSpecialObj(messageJson.get("special").getAsJsonObject());

            if (!message.setMsg(messageJson.get("message").getAsString(), user.isStoryteller())) {
                peer.close();
                return;
            }

            message.setClone(messageJson.get("clone").getAsBoolean());

            message.setSpecial(gson.toJson(message.getSpecialObj()));
            message.unsetSpecialObj();
            message.setSendDate(today);

            if (!message.needsStored()) {
                message.setId(BigDecimal.valueOf(room.getFakeId()));
            } else {
                if (!MessageDAO.addMessage(message, userid, roomid)) {
                    peer.getBasicRemote().sendText("[\"notsaved\"," + msgJSON + "]");
                    return;
                }
            }

            if (!message.isClone()) {
                String stringified = "[\"message\"," + gson.toJson(message) + "]";
                try {
                    peer.getBasicRemote().sendText(stringified);
                } catch (IOException e) {}
            }
            message.setLocalid(null);

            String stringifiedOthers = "[\"message\"," + gson.toJson(message) + "]";

            Set<Session> sessions;
            
            if (message.getDestinations() == null && message.getDestination() == null) {
                sessions = ((SalaSocket) rooms.get(roomid)).getSessions();
                synchronized (sessions) {
                    for (Session other : sessions) {
                        if (other.equals(peer)) { continue; }
                        other.getBasicRemote().sendText(stringifiedOthers);
                    }
                }
            } else {
                if (message.getDestinations() == null) {
                    UsuarioSocket target = (UsuarioSocket) room.getUser(message.getDestination());
                    sessions = target.getSessions();
                    synchronized (sessions) {
                        for (Session other : target.getSessions()) {
                            if (other.equals(peer)) { continue; }
                            other.getBasicRemote().sendText(stringifiedOthers);
                        }
                    }
                } else {
                    UsuarioSocket target;
                    for (int id : message.getDestinations()) {
                        if (room.getUsers().containsKey(id)) {
                            target = (UsuarioSocket) room.getUser(id);
                            sessions = target.getSessions();
                            synchronized (sessions) {
                                for (Session other : sessions) {
                                    if (other.equals(peer)) { continue; }
                                    other.getBasicRemote().sendText(stringifiedOthers);
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException | JsonSyntaxException e) { }
    }

    private static void setMemory(Integer userid, Integer roomid, Session peer, String message) {
        SalaSocket room = (SalaSocket) rooms.get(roomid);
        UsuarioSocket user = room.getUser(userid);
        if (!user.isStoryteller()) {
            try {
                peer.close();
            } catch (IOException ex) { }
            return;
        }
        
        try {
            Gson gson = GsonFactory.getFactory().getGson();
            String memory = gson.toJson(gson.fromJson(message, JsonObject.class));
            if (memory == null || memory.equals(null) || memory.charAt(0) != '{') {
                memory = "{}";
            }
            room.setJsonMemory(memory);
            SalaDAO.storeMemory(room, roomid);
            Set<Session> sessions = room.getSessions();
            synchronized (sessions) {
                for (Session other : sessions) {
                    if (other.equals(peer)) continue;
                    other.getBasicRemote().sendText("[\"memory\"," + roomid + "," + memory + "]");
                }
            }
        } catch (IOException e) { }
    }
    
    public static void updateRooms (int gameid, int userid) {
        SalaSocket room;
        HashMap<Integer, UsuarioSocket> usuariosala = UsuarioDAO.getUsuarioSockets(gameid, userid);
        for (Entry entry : usuariosala.entrySet()) {
            room = (SalaSocket) rooms.get(entry.getKey());
            if (room != null) {
                room.addUser((UsuarioSocket) entry.getValue());
            }
        }
    }
    
}
