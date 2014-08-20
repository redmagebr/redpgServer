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
    @Expose private boolean storyteller;
    @Expose private String avatar;
    @Expose private String persona;
    @Expose private boolean typing;
    @Expose private boolean focused = true;
    @Expose private boolean idle;

    public boolean isIdle() {
        return idle;
    }

    public void setIdle(boolean idle) {
        this.idle = idle;
    }
    
    public boolean isStoryteller() {
        return storyteller;
    }

    public void setStoryteller(boolean storyteller) {
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

    public boolean isTyping() {
        return typing;
    }

    public void setTyping(boolean typing) {
        this.typing = typing;
    }

    public boolean isFocused() {
        return focused;
    }

    public void setFocused(boolean focused) {
        this.focused = focused;
    }
    
    
}
