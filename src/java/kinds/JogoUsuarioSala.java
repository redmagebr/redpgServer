/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package kinds;

import java.util.ArrayList;

/**
 *
 * @author reddo
 */
public class JogoUsuarioSala extends JogoUsuario {
    private ArrayList<SalaUsuario> rooms;

    public ArrayList<SalaUsuario> getRooms() {
        return rooms;
    }

    public void setRooms(ArrayList<SalaUsuario> rooms) {
        this.rooms = rooms;
    }
}
