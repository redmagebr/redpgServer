/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package kinds;

import com.google.gson.annotations.Expose;

/**
 *
 * @author reddo
 */
public class UsuarioChat extends Usuario {
    @Expose private Boolean storyteller;
    @Expose private String avatar;
    @Expose private String persona;
    @Expose private Boolean typing;
    @Expose private Boolean focused;

    public Boolean isStoryteller() {
        return storyteller;
    }

    public void setStoryteller(Boolean storyteller) {
        this.storyteller = storyteller;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPersona() {
        return persona;
    }

    public void setPersona(String persona) {
        this.persona = persona;
    }

    public Boolean isTyping() {
        return typing;
    }

    public void setTyping(Boolean typing) {
        this.typing = typing;
    }

    public Boolean isFocused() {
        return focused;
    }

    public void setFocused(Boolean focused) {
        this.focused = focused;
    }
    
    
}
