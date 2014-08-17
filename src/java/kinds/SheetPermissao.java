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
public class SheetPermissao extends SheetUsuario {
    private int userid;
    private String nickname;
    private String nicknamesufix;

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
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
