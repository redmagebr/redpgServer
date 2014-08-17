/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import kinds.Message;
import sistema.ConnectionPooler;

/**
 *
 * @author reddo
 */
public class MessageDAO {
    public static boolean addMessage (Message message, int userid, int roomid) {
        Connection dbh = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            dbh = ConnectionPooler.getConnection();
            dbh.setAutoCommit(false);
            int k;
            Integer destination;
            ArrayList<Integer> destinations;
            stmt = dbh.prepareStatement("INSERT INTO chat_sala_msg (message, module, roomid, sender, special, destination, clone, sentDate) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, message.getMsg());
            stmt.setString(2, message.getModule());
            stmt.setInt(3, roomid);
            stmt.setInt(4, userid);
            stmt.setString(5, message.getSpecial());
            stmt.setDate(8, message.getSendDate());
            destination = message.getDestination();
            destinations = message.getDestinations();
            if (destinations != null) {
                for (k = 0; k < destinations.size(); k++) {
                    stmt.setInt(6, destinations.get(k));
                    stmt.setBoolean(7, !((k + 1) == destinations.size()));
                    if (stmt.executeUpdate() < 1) {
                        dbh.rollback();
                        return false;
                    }
                }
            }  else {
                if (destination != null) {
                    stmt.setInt(6, destination);
                } else {
                    stmt.setNull(6, java.sql.Types.INTEGER);
                }

                stmt.setBoolean(7, false);

                if (stmt.executeUpdate() < 1) {
                    dbh.rollback();
                    return false;
                }
            }
            
            rs = stmt.getGeneratedKeys();
            if (!rs.next()) {
                return false;
            }
            
            message.setId(rs.getBigDecimal(1));
            
            dbh.commit();
            
            return true;
        } catch (SQLException e) {
            if (dbh != null) {
                try {
                    dbh.rollback();
                    dbh.setAutoCommit(true);
                } catch (SQLException ex) {
                    Logger.getLogger(MessageDAO.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            return false;
        } finally {
            if (dbh != null) {
                try {
                    dbh.setAutoCommit(true);
                } catch (SQLException ex) {
                    Logger.getLogger(MessageDAO.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            ConnectionPooler.closeResultset(rs);
            ConnectionPooler.closeStatement(stmt);
            ConnectionPooler.closeConnection(dbh);
        }
    }
    
    public static ArrayList<Message> getMessages (BigDecimal lastReceived, int roomid, int userid) {
        Connection dbh = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ArrayList<Message> messages = new ArrayList<Message>();
        try {
            dbh = ConnectionPooler.getConnection();
            stmt = dbh.prepareStatement("SELECT * FROM view_chatmensagens WHERE ID_Mensagem > ? AND ID_Sala = ? AND (ID_Destination = ? OR ID_Destination IS NULL OR (ID_Origin = ? AND NOT isClone));");
            stmt.setBigDecimal(1, lastReceived);
            stmt.setInt(2, roomid);
            stmt.setInt(3, userid);
            stmt.setInt(4, userid);
            
            rs = stmt.executeQuery();
            
            Message message;
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");  
            
            while (rs.next()) {
                message = new Message();
                message.setId(rs.getBigDecimal("ID_Mensagem"));
                message.setModule(rs.getString("Module"));
                message.setMessage(rs.getString("Message"));
                message.setSpecial(rs.getString("Special"));
                message.setOrigin(rs.getInt("ID_Origin"));
                message.setDestination(rs.getInt("ID_Destination"));
                message.setDate(df.format(rs.getDate("Date_Sent")));
                messages.add(message);
            }
            
            return messages;
        } catch (SQLException e) {
            return null;
        } finally {
            ConnectionPooler.closeResultset(rs);
            ConnectionPooler.closeStatement(stmt);
            ConnectionPooler.closeConnection(dbh);
        }
    }
}
