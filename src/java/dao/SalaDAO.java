/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import kinds.Sala;
import kinds.SalaSocket;
import kinds.UsuarioSocket;
import sistema.ConnectionPooler;

/**
 *
 * @author reddo
 */
public class SalaDAO {
    public static int createRoom (Sala sala, int gameid) {
        Connection dbh = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            dbh = ConnectionPooler.getConnection();
            
            stmt = dbh.prepareStatement("SELECT Permissao_CriarSala FROM view_jogos WHERE ID_Usuario = ? AND ID_Jogo = ?;");
            
            stmt.setInt(1, sala.getCreatorid());
            stmt.setInt(2, gameid);
            
            rs = stmt.executeQuery();
            
            if (!rs.next() || !rs.getBoolean("Permissao_CriarSala")) {
                return 0;
            }
            
            ConnectionPooler.closeResultset(rs);
            ConnectionPooler.closeStatement(stmt);
            
            stmt = dbh.prepareStatement("INSERT INTO chat_sala (jogoid, `name`, creator, descroom, `private`, streamable, playbypost) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?);");
            
            stmt.setInt(1, gameid);
            stmt.setString(2, sala.getName());
            stmt.setInt(3, sala.getCreatorid());
            stmt.setString(4, sala.getDescription());
            stmt.setBoolean(5, sala.isPrivateRoom());
            stmt.setBoolean(6, sala.isStreamable());
            stmt.setBoolean(7, sala.isPlayByPost());
            
            if (stmt.executeUpdate() <= 0) {
                return -1;
            }
            
            return 1;
        } catch (SQLException e) {
            return -1;
        } finally {
            ConnectionPooler.closeResultset(rs);
            ConnectionPooler.closeStatement(stmt);
            ConnectionPooler.closeConnection(dbh);
        }
    }
    
    public static int clearRoom (int roomid, int userid) {
        Connection dbh = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            dbh = ConnectionPooler.getConnection();
            stmt = dbh.prepareStatement("SELECT 1 FROM chat_sala WHERE id = ? AND creator = ?;");
            stmt.setInt(1, roomid);
            stmt.setInt(2, userid);
            
            rs = stmt.executeQuery();
            if (!rs.next()) {
                return 0;
            }
            
            ConnectionPooler.closeResultset(rs);
            rs = null;
            ConnectionPooler.closeStatement(stmt);
            stmt = null;
            
            stmt = dbh.prepareStatement("DELETE FROM chat_sala_msg WHERE roomid = ?;");
            stmt.setInt(1, roomid);
            
            if (stmt.executeUpdate() > 0) {
                return 1;
            }
            return -1;
        } catch (SQLException e) {
            return -1;
        } finally {
            ConnectionPooler.closeResultset(rs);
            ConnectionPooler.closeStatement(stmt);
            ConnectionPooler.closeConnection(dbh);
        }
    }
    
    public static int deleteRoom (Sala sala) {
        Connection dbh = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            dbh = ConnectionPooler.getConnection();
            stmt = dbh.prepareStatement("DELETE FROM chat_sala WHERE id = ? AND creator = ?;");
            stmt.setInt(1, sala.getId());
            stmt.setInt(2, sala.getCreatorid());
            
            if (stmt.executeUpdate() == 1) {
                return 1;
            }
            return 0;
        } catch (SQLException e) {
            return -1;
        } finally {
            ConnectionPooler.closeResultset(rs);
            ConnectionPooler.closeStatement(stmt);
            ConnectionPooler.closeConnection(dbh);
        }
    }
    
    public static SalaSocket getSalaSocket (int roomid) {
        Connection dbh = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            dbh = ConnectionPooler.getConnection();
            stmt = dbh.prepareStatement("SELECT Room_Memory FROM view_jogosalas WHERE ID_Sala = ?;");
            stmt.setInt(1, roomid);
            rs = stmt.executeQuery();
            
            if (!rs.next()) {
                return new SalaSocket();
            }
            
            SalaSocket room = new SalaSocket();
            room.setJsonMemory(rs.getString("Room_Memory"));
            ConnectionPooler.closeResultset(rs);
            ConnectionPooler.closeStatement(stmt);
            ConnectionPooler.closeConnection(dbh);
            rs = null;
            stmt = null;
            dbh = null;
            
            ArrayList<UsuarioSocket> users = UsuarioDAO.getUsuarioSockets(roomid);
            
            for (UsuarioSocket user : users) {
                room.getUsers().put(user.getId(), user);
            }
            
            return room;
        } catch (SQLException ex) {
            return null;
        } finally {
            ConnectionPooler.closeResultset(rs);
            ConnectionPooler.closeStatement(stmt);
            ConnectionPooler.closeConnection(dbh);
        }
    }
}
