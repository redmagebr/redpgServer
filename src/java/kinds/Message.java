/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package kinds;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Reddo
 */
public class Message {
    private BigDecimal id;
    private BigDecimal localid = null;
    private Integer destination;
    private ArrayList<Integer> destinations;
    private Integer origin;
    private Integer roomid;
    private String module;
    private String msg;
    private String special;
    private JsonObject specialObj;
    private Date sendDate;
    private String date;
    private boolean clone;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }
    
    

    public ArrayList<Integer> getDestinations() {
        return destinations;
    }

    public void setDestinations(ArrayList<Integer> destinations) {
        this.destinations = destinations;
    }
    
    public Integer getDestination() {
        return destination;
    }

    public void setDestination(int destination) {
        this.destination = destination;
    }
    
    public void prepareSaved () {
        this.destination = null;
        this.origin = null;
        this.roomid = null;
        this.module = null;
        this.msg = null;
        this.special = null;
        this.specialObj = null;
    }

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public BigDecimal getLocalid() {
        return localid;
    }

    public void setLocalid(BigDecimal localid) {
        this.localid = localid;
    }

    public int getOrigin() {
        return origin;
    }

    public void setOrigin(int origin) {
        this.origin = origin;
    }

    public int getRoomid() {
        return roomid;
    }

    public void setRoomid(int roomid) {
        this.roomid = roomid;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getMsg() {
        return msg;
    }
    
    public void setMessage (String msg) {
        this.msg = msg;
    }

    public boolean setMsg(String msg, boolean isStoryteller) {
        if (this.module.equals("roleplay") || this.module.equals("action")) {
            if (this.specialObj.has("persona") && this.specialObj.get("persona").getAsString().length() > 0) {
                this.msg = msg;
                return true;
            }
        } else if (this.module.equals("story")) {
            if (isStoryteller) {
                this.msg = msg;
                return true;
            }
        } else if (this.module.equals("dice")) {
            return this.rollDice(msg, isStoryteller);
        } else {
            this.msg = msg;
            return true;
        }
        return false;
    }

    public String getSpecial() {
        return special;
    }

    public void setSpecial(String special) {
        this.special = special;
    }

    public JsonObject getSpecialObj() {
        return specialObj;
    }

    public void setSpecialObj(JsonObject specialObj) {
        this.specialObj = specialObj;
    }
    
    public void unsetSpecialObj() {
        this.specialObj = null;
    }
    
    public boolean rollDice (String msg, boolean isStoryteller) {
        try {
            if (!this.specialObj.has("dice") || !this.specialObj.get("dice").isJsonArray()) {
                return false;
            }
            this.msg = msg;
            JsonArray dice = this.specialObj.get("dice").getAsJsonArray();
            JsonArray rolls = new JsonArray();
            if (rolls.size() > 99) {
                return false;
            }
            Integer result;
            Random random = new Random();
            Gson gson = new Gson();
            int faces;
            for (int i = 0; i < dice.size(); i++) {
                faces = dice.get(i).getAsInt();
                result = random.nextInt(faces) + 1;
                rolls.add(gson.fromJson(result.toString(), JsonElement.class));
            }
            this.specialObj.add("rolls", rolls);
        } catch (JsonSyntaxException e ) {
            return false;
        }
        return true;
    }
    
    public boolean needsStored () {
        if (this.module.equals("sheetup")) {
            return false;
        }
        return true;
    }

    public boolean isClone() {
        return clone;
    }

    public void setClone(boolean clone) {
        this.clone = clone;
    }
}
