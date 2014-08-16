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
public class JogoUsuario extends Jogo {
    private boolean createSheet;
    private boolean editSheet;
    private boolean deleteSheet;
    private boolean createRoom;
    private boolean invite;
    private boolean promote;

    public boolean isCreateSheet() {
        return createSheet;
    }

    public void setCreateSheet(boolean createSheet) {
        this.createSheet = createSheet;
    }

    public boolean isEditSheet() {
        return editSheet;
    }

    public void setEditSheet(boolean editSheet) {
        this.editSheet = editSheet;
    }

    public boolean isDeleteSheet() {
        return deleteSheet;
    }

    public void setDeleteSheet(boolean deleteSheet) {
        this.deleteSheet = deleteSheet;
    }

    public boolean isCreateRoom() {
        return createRoom;
    }

    public void setCreateRoom(boolean createRoom) {
        this.createRoom = createRoom;
    }

    public boolean isInvite() {
        return invite;
    }

    public void setInvite(boolean invite) {
        this.invite = invite;
    }

    public boolean isPromote() {
        return promote;
    }

    public void setPromote(boolean promote) {
        this.promote = promote;
    }
    
    
}
