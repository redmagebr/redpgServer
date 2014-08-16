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
public class Jogo {
    private int id;
    private String name;
    private String description;
    private int creatorid;
    private String creatornick;
    private String creatorsufix;
    private boolean freejoin;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCreatorid() {
        return creatorid;
    }

    public void setCreatorid(int creatorid) {
        this.creatorid = creatorid;
    }

    public String getCreatornick() {
        return creatornick;
    }

    public void setCreatornick(String creatornick) {
        this.creatornick = creatornick;
    }

    public String getCreatorsufix() {
        return creatorsufix;
    }

    public void setCreatorsufix(String creatorsufix) {
        this.creatorsufix = creatorsufix;
    }

    public boolean isFreejoin() {
        return freejoin;
    }

    public void setFreejoin(boolean freejoin) {
        this.freejoin = freejoin;
    }
    
    
}
