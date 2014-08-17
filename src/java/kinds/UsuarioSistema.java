/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package kinds;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;

/**
 *
 * @author reddo
 */
public class UsuarioSistema extends Usuario {
    private String email;
    private String password;
    private String name;
    private int level;
    @Expose private String config;
    
    private void prepareJSON () {
        Gson gson = new Gson();
        
        this.setEmail(gson.toJson(this.getEmail()));
        this.setNickname(gson.toJson(this.getNickname()));
        this.setNicknamesufix(gson.toJson(this.getNicknamesufix()));
    }
    
    private void setConfig (JsonObject object) {
        Gson gson = new Gson();
        this.config = gson.toJson(object);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
