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
public class SheetPermissoes {
    private ArrayList<Usuario> users;
    private ArrayList<SheetUsuario> permissoes;

    public ArrayList<Usuario> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<Usuario> users) {
        this.users = users;
    }

    public ArrayList<SheetUsuario> getPermissoes() {
        return permissoes;
    }

    public void setPermissoes(ArrayList<SheetUsuario> permissoes) {
        this.permissoes = permissoes;
    }
}
