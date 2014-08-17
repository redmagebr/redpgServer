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
public class JogoUsuarioSheet extends JogoUsuario {
    private ArrayList<SheetUsuario> sheets;

    public ArrayList<SheetUsuario> getSheets() {
        return sheets;
    }

    public void setSheets(ArrayList<SheetUsuario> sheets) {
        this.sheets = sheets;
    }

}
