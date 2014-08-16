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
public class Usuario {
    @Expose private Integer id;
    @Expose private String nickname;
    @Expose private String nicknamesufix;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNicknamesufix() {
        return nicknamesufix;
    }

    public void setNicknamesufix(String nicknamesufix) {
        this.nicknamesufix = nicknamesufix;
    }
    
    
}
