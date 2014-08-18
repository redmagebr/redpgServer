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
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private Integer roomid;
    private Integer userid;
    
    @OnOpen
    public void onOpen (Session peer, EndpointConfig config) {
        HttpSession session = ((HttpSession) config.getUserProperties().get("httpSession"));
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
            try {
                peer.close();
            } catch (IOException ex) { }
        }
        String action = message.substring(0, delimiter);
        message = message.substring(delimiter + 1, message.length());
        if (action.equals("room") && this.roomid == null) {
            try {
                this.roomid = Integer.parseInt(message);
            } catch (NumberFormatException e) {
                try {
                    peer.close();
                } catch (IOException ex) {}
                return null;
            }
            if (!enterRoom(this.userid, this.roomid, peer)) {
                try {
                    peer.close();
                } catch (IOException ex) {}
                return null;
            }
            return "[\"inroom\"," + GsonFactory.getFactory().getGsonExposed().toJson(((SalaSocket) rooms.get(this.roomid)).getUsers()) + "]";
        } else if (action.equals("typing")) {
            setTyping(this.userid, this.roomid, message);
        } else if (action.equals("focused")) {
            setFocused(this.userid, this.roomid, message);
        } else if (action.equals("message")) {
            sendMessage(userid, roomid, peer, message);
        } else if (action.equals("persona")) {
            setPersona(userid, roomid, peer, message);
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
        synchronized (rooms) {
            SalaSocket room = (SalaSocket) rooms.get(this.roomid);
            if (room == null) {
                return;
            }
            synchronized (room) {
                room.removeSession(this.userid, peer);
                if (room.sessionSize() < 1) {
                    rooms.remove(this.roomid);
                } else {
                    if (!room.getUser(this.userid).isOnline()) {
                        for (Session other : room.getSessions()) {
                            try {
                                other.getBasicRemote().sendText("[\"left\"," + this.userid + "]");
                            } catch (IOException ex) { }
                        }
                    }
                    return;
                }
            }
        }
    }
    
    public static boolean enterRoom (int userid, int roomid, Session peer) {
        SalaSocket room;
        synchronized (rooms) {
            if (!rooms.containsKey(roomid) || rooms.get(roomid) == null) {
                room = SalaDAO.getSalaSocket(roomid);
                SalaSocket placed = (SalaSocket) rooms.putIfAbsent(roomid, room);
                if (placed != null) {
                    room = placed;
                }
            } else {
                room = (SalaSocket) rooms.get(roomid);
            }
            synchronized (room) {
                if (!room.addSession(userid, peer)) {
                    if (room.sessionSize() < 1) {
                        rooms.remove(roomid);
                    }
                    return false;
                }
                if (room.getUser(userid).sessionsSize() == 1) {
                    String userJson = GsonFactory.getFactory().getGsonExposed().toJson(room.getUser(userid));
                    for (Session other : room.getSessions()) {
                        if (other.equals(peer)) continue;
                        try {
                            other.getBasicRemote().sendText("[\"joined\"," + userJson + "]");
                        } catch (IOException e) {}
                    }
                }
                return true;
            }
        }
    }
    
    
    public static void setTyping (int userid, int roomid, String message) {
        UsuarioSocket user = ((SalaSocket) rooms.get(roomid)).getUser(userid);
        if (user != null) {
            user.setTyping(message.equals("1"));
            for (Session other : ((SalaSocket) rooms.get(roomid)).getSessions()) {
                try {
                    other.getBasicRemote().sendText("[\"typing\"," + userid + "," + (user.isTyping() ? "1" : "0") + "]");
                } catch (IOException ex) { }
            }
        }
    }
    
    public static void setFocused (int userid, int roomid, String message) {
        UsuarioSocket user = ((SalaSocket) rooms.get(roomid)).getUser(userid);
        if (user != null) {
            user.setFocused(message.equals("1"));
            for (Session other : ((SalaSocket) rooms.get(roomid)).getSessions()) {
                try {
                    other.getBasicRemote().sendText("[\"focused\"," + userid + "," + (user.isFocused()? "1" : "0") + "]");
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
            
            for (Session other : room.getSessions()) {
                try {
                    other.getBasicRemote().sendText(stringified);
                } catch (IOException ex) { }
            }
        } catch (JsonSyntaxException e) {
            try {
                peer.close();
            } catch (IOException ex) { }
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
            try {
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
                
                if (message.getDestinations() == null && message.getDestination() == null) {
                    for (Session other : ((SalaSocket) rooms.get(roomid)).getSessions()) {
                        if (other.equals(peer)) { continue; }
                        other.getBasicRemote().sendText(stringifiedOthers);
                    }
                } else {
                    if (message.getDestinations() == null) {
                        UsuarioSocket target = (UsuarioSocket) room.getUser(message.getDestination());
                        for (Session other : target.getSessions()) {
                            if (other.equals(peer)) { continue; }
                            other.getBasicRemote().sendText(stringifiedOthers);
                        }
                    } else {
                        UsuarioSocket target;
                        for (int id : message.getDestinations()) {
                            if (room.getUsers().containsKey(id)) {
                                target = (UsuarioSocket) room.getUser(id);
                                for (Session other : target.getSessions()) {
                                    if (other.equals(peer)) { continue; }
                                    other.getBasicRemote().sendText(stringifiedOthers);
                                }
                            }
                        }
                    }
                }
            } catch (JsonSyntaxException e) {
                peer.close();
                return;
            }
        } catch (IOException e) { return; }
    }
    
}
