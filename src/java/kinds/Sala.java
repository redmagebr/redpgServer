/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package kinds;

/**
 *
 * @author reddo
 */
public class Sala {
    private int id;
    private String name;
    private int creatorid;
    private String description;
    private boolean privateRoom;
    private boolean streamable;
    private boolean playByPost;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCreatorid() {
        return creatorid;
    }

    public void setCreatorid(int creatorid) {
        this.creatorid = creatorid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isPrivateRoom() {
        return privateRoom;
    }

    public void setPrivateRoom(boolean privateRoom) {
        this.privateRoom = privateRoom;
    }

    public boolean isStreamable() {
        return streamable;
    }

    public void setStreamable(boolean streamable) {
        this.streamable = streamable;
    }

    public boolean isPlayByPost() {
        return playByPost;
    }

    public void setPlayByPost(boolean playByPost) {
        this.playByPost = playByPost;
    }
    
    
}
