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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import kinds.Usuario;
import kinds.UsuarioChat;
import kinds.UsuarioSistema;
import kinds.UsuarioSocket;
import sistema.ConnectionPooler;

/**
 *
 * @author reddo
 */
public class UsuarioDAO {
    
    /**
     * Gets UsuarioSistema from String email and String password
     * @param email
     * @param password
     * @return 
     * Filled UsuarioSistema : Success <br/>
     * Empty UsuarioSistema : Not Found <br />
     * null : Internal Error
     */
    public static UsuarioSistema getUsuarioSistema (String email, String password) {
        Connection dbh = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        UsuarioSistema user = new UsuarioSistema();
        try {
            dbh = ConnectionPooler.getConnection();
            
            stmt = dbh.prepareStatement("SELECT level, id, nickname, nicknamesufix, name, config FROM usuario WHERE email = ? AND password = ?");
            stmt.setString(1, email);
            stmt.setString(2, password);
            rs = stmt.executeQuery();
            if (rs.next()) {
                user = new UsuarioSistema();
                user.setEmail(email);
                user.setPassword(password);
                user.setId(rs.getInt("id"));
                user.setNickname(rs.getString("nickname"));
                user.setNicknamesufix(rs.getString("nicknamesufix"));
                user.setLevel(rs.getInt("level"));
                user.setName(rs.getString("name"));
                user.setConfig(rs.getString("config"));
            }
            return user;
        } catch (SQLException e) {
            return null;
        } finally {
            ConnectionPooler.closeResultset(rs);
            ConnectionPooler.closeStatement(stmt);
            ConnectionPooler.closeConnection(dbh);
        }
    }
    
    /**
     * Gets information on specific user through Nickname and Sufix.
     * @param nick
     * @param nicksufix
     * @return 
     * Usuario <br />
     * Empty Usuario if not found <br />
     * null if internal error
     */
    public static Usuario getUsuario (String nick, String nicksufix) {
        Connection dbh = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Usuario user = new Usuario();
        try {
            dbh = ConnectionPooler.getConnection();
            
            stmt = dbh.prepareStatement("SELECT id, nickname, nicknamesufix FROM usuario WHERE nickname = ? AND nicknamesufix = ?");
            stmt.setString(1, nick);
            stmt.setString(2, nicksufix);
            rs = stmt.executeQuery();
            if (rs.next()) {
                user = new UsuarioSistema();
                user.setId(rs.getInt("id"));
                user.setNickname(rs.getString("nickname"));
                user.setNicknamesufix(rs.getString("nicknamesufix"));
            }
            return user;
        } catch (SQLException e) {
            return null;
        } finally {
            ConnectionPooler.closeResultset(rs);
            ConnectionPooler.closeStatement(stmt);
            ConnectionPooler.closeConnection(dbh);
        }
    }
    
    /**
     * Gets uuid for UsuarioSistema usuario. Requires the object to have e-mail set.
     * Will set name on usuario when done.
     * @param usuario
     * @return 
     *  String : Success <br />
     *  Empty string: Not found <br />
     *  null   : Internal
     */
    public static String getActivationUUID (UsuarioSistema usuario) {
        Connection dbh = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            dbh = ConnectionPooler.getConnection();
            stmt = dbh.prepareStatement(
                  "SELECT a.uuid AS uuid, usuario.name AS name "
                + "FROM usuario_activation a "
                + "INNER JOIN usuario ON a.userid = usuario.id "
                + "WHERE "
                + "a.used = 0 AND usuario.level = 0 "
                + "AND usuario.email = ?"
            );
            stmt.setString(1, usuario.getEmail());
            rs = stmt.executeQuery();
            if (!rs.next()) {
                return "";
            }
            usuario.setName(rs.getString("name"));
            return rs.getString("uuid");
        } catch (SQLException ex) {
            return null;
        } finally {
            ConnectionPooler.closeResultset(rs);
            ConnectionPooler.closeStatement(stmt);
            ConnectionPooler.closeConnection(dbh);
        }
    }
    
    /**
     * Confirms account of UUID String uuid
     * @param uuid
     * @return 
     * -1 : Internal Error <br />
     *  0 : Not found <br />
     *  1 : Success
     */
    public static int confirmAccount (String uuid) {
        Connection dbh = null;
        PreparedStatement stmt = null;
        try {
            dbh = ConnectionPooler.getConnection();
            dbh.setAutoCommit(false);
            stmt = dbh.prepareStatement("UPDATE usuario "
                                      + "INNER JOIN usuario_activation a ON a.userid = usuario.id "
                                      + "SET usuario.level = 1 "
                                      + "WHERE a.uuid = ? AND a.used = 0 AND usuario.level = 0");
            stmt.setString(1, uuid);
            
            if (stmt.executeUpdate() == 1) {
                ConnectionPooler.closeStatement(stmt);
                stmt = dbh.prepareStatement("UPDATE usuario_activation SET used = 1 "
                                          + "WHERE uuid = ?");
                stmt.setString(1, uuid);
                if (stmt.executeUpdate() != 1) {
                    dbh.rollback();
                    return -1;
                }
                dbh.commit();
                return 1;
            } else {
                return 0;
            }
        } catch (SQLException ex) {
            try {
                if (dbh != null) dbh.rollback();
            } catch (SQLException e) { }
            return -1;
        } finally {
            ConnectionPooler.closeStatement(stmt);
            try {
                if (dbh != null) dbh.setAutoCommit(true);
            } catch (SQLException e) { }
            ConnectionPooler.closeConnection(dbh);
        }
    }
    
    /**
     * Attempts to create user account.
     * @param usuario
     * @return int <br/>
     * -3 : E-mail conflict <br />
     * -2 : No numbers available for this nickname <br />
     * -1 : Internal error <br />
     *  1 : Creation Successful
     */
    public static int createAccount (UsuarioSistema usuario) {
        Connection dbh = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            dbh = ConnectionPooler.getConnection();
            
            int maxRandom;
            
            Random random = new Random();
            
            stmt = dbh.prepareStatement("SELECT COUNT(*) AS Available FROM usuario_sufix " +
                                        "LEFT JOIN usuario ON usuario.nickname = ? AND usuario_sufix.sufix = usuario.nicknamesufix " +
                                        "WHERE usuario.nicknamesufix IS NULL;");
            stmt.setString(1, usuario.getNickname());
            rs = stmt.executeQuery();
            if (rs.next()) {
                maxRandom = rs.getInt("Available");
                if (maxRandom < 1) {
                    return -2;
                }
            } else {
                return -1;
            }
            
            ConnectionPooler.closeResultset(rs);
            ConnectionPooler.closeStatement(stmt);
            
            stmt = dbh.prepareStatement("SELECT usuario_sufix.sufix AS RandomAvailable FROM usuario_sufix " +
                                        "LEFT JOIN usuario ON usuario.nickname = ? AND usuario_sufix.sufix = usuario.nicknamesufix " +
                                        "WHERE usuario.nicknamesufix IS NULL " +
                                        "LIMIT 1 OFFSET " + random.nextInt(maxRandom) +  ";");
            
            stmt.setString(1, usuario.getNickname());
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                usuario.setNicknamesufix(rs.getString("RandomAvailable"));
            } else {
                return -2;
            }
            
            dbh.setAutoCommit(false);
            stmt = dbh.prepareStatement("INSERT INTO usuario " +
                                        "(email, password, name, nickname, nicknamesufix, config, level) " +
                                        "VALUES (?, ?, ?, ?, ?, ?, 1);", Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, usuario.getEmail());
            stmt.setString(2, usuario.getPassword());
            stmt.setString(3, usuario.getName());
            stmt.setString(4, usuario.getNickname());
            stmt.setString(5, usuario.getNicknamesufix());
            stmt.setString(6, "{}");
            
            try {
                if (stmt.executeUpdate() == 0) {
                    return -1;
                }
            } catch (SQLException e) {
                if (e.getSQLState().equals("23000")) {
                    dbh.rollback();
                    return -3;
                } else {
                    throw e;
                }
            }

            rs = stmt.getGeneratedKeys();
            if (!rs.next()) {
                dbh.rollback();
                return -1;
            }
            usuario.setId(rs.getInt(1));

            ConnectionPooler.closeStatement(stmt);

            stmt = dbh.prepareStatement("INSERT INTO usuario_activation " +
                                        "(uuid, userid) " +
                                        "VALUES (?, ?);");
            String uuid = java.util.UUID.randomUUID().toString();

            stmt.setString(1, uuid);
            stmt.setInt(2, usuario.getId());

            if (stmt.executeUpdate() == 0) {
                dbh.rollback();
                return -1;
            }
            dbh.commit();
            return 1;
        } catch (SQLException ex) {
            return -1;
        } finally {
            ConnectionPooler.closeResultset(rs);
            ConnectionPooler.closeStatement(stmt);
            try {
                if (dbh != null) {
                    dbh.setAutoCommit(true);
                }
            } catch (SQLException e) {}
            ConnectionPooler.closeConnection(dbh);
        }
    }
    
    
    /**
     * Informs whether room roomid can be accessed by user userid
     * @param roomid int
     * @param userid int
     * @return boolean
     */
    public static boolean canAccess (int roomid, int userid) {
        Connection dbh = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            dbh = ConnectionPooler.getConnection();
            stmt = dbh.prepareStatement("SELECT ID_Criador_Sala FROM view_jogosalas WHERE ID_Usuario = ? AND ID_Sala = ?;");
            stmt.setInt(1, userid);
            stmt.setInt(2, roomid);
            
            rs = stmt.executeQuery();
            
            return rs.next();
        } catch (SQLException e) {
            return false;
        } finally {
            ConnectionPooler.closeResultset(rs);
            ConnectionPooler.closeStatement(stmt);
            ConnectionPooler.closeConnection(dbh);
        }
    }
    
    /**
     * Gets all UsuarioSala in a room
     * @param roomid
     * @return 
     * ArrayList of all found users; <br />
     * null on internal error
     */
    public static ArrayList<UsuarioChat> getRoomUsers (int roomid) {
        Connection dbh = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ArrayList<UsuarioChat> users = new ArrayList<UsuarioChat>();
        try {
            dbh = ConnectionPooler.getConnection();
            stmt = dbh.prepareStatement("SELECT * FROM view_salausuario WHERE ID_Sala = ?;");
            stmt.setInt(1, roomid);
            
            rs = stmt.executeQuery();
            
            UsuarioChat user;
            
            while (rs.next()) {
                user = new UsuarioChat();
                user.setId(rs.getInt("ID_Usuario"));
                user.setNickname(rs.getString("Nick_Usuario"));
                user.setNicknamesufix(rs.getString("Nicksufix_Usuario"));
                user.setStoryteller(rs.getBoolean("isStoryteller"));
                users.add(user);
            }
            
            return users;
        } catch (SQLException e) {
            return null;
        } finally {
            ConnectionPooler.closeResultset(rs);
            ConnectionPooler.closeStatement(stmt);
            ConnectionPooler.closeConnection(dbh);
        }
    }
    
    /**
     * Gets user permissions for a room.
     * @param userid
     * @param roomid
     * @return UsuarioSala on success <br />
     * null on not found or internal error.
     */
    public static UsuarioChat getRoomUser (int userid, int roomid) {
        Connection dbh = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            dbh = ConnectionPooler.getConnection();
            stmt = dbh.prepareStatement("SELECT * FROM view_salausuario WHERE ID_Sala = ? AND ID_Usuario = ?;");
            stmt.setInt(1, roomid);
            stmt.setInt(2, userid);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                UsuarioChat user = new UsuarioChat();
                user.setId(rs.getInt("ID_Usuario"));
                user.setNickname(rs.getString("Nick_Usuario"));
                user.setNicknamesufix(rs.getString("Nicksufix_Usuario"));
                user.setStoryteller(rs.getBoolean("isStoryteller"));
                
                return user;
            }
            return null;
        } catch (SQLException e) {
            return null;
        } finally {
            ConnectionPooler.closeResultset(rs);
            ConnectionPooler.closeStatement(stmt);
            ConnectionPooler.closeConnection(dbh);
        }
    }
    
    public static int storeConfig (UsuarioSistema usuario) {
        Connection dbh = null;
        PreparedStatement stmt = null;
        try {
            dbh = ConnectionPooler.getConnection();
            stmt = dbh.prepareStatement("UPDATE usuario SET config = ? WHERE id = ?;");
            stmt.setString(1, usuario.getConfig());
            stmt.setInt(2, usuario.getId());
            if (stmt.executeUpdate() > 0) {
                return 1;
            }
            return 0;
        } catch (SQLException ex) {
            return -1;
        } finally {
            ConnectionPooler.closeStatement(stmt);
            ConnectionPooler.closeConnection(dbh);
        }
    }
    
    public static ArrayList<UsuarioSocket> getUsuarioSockets (int roomid) {
        Connection dbh = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            dbh = ConnectionPooler.getConnection();
            stmt = dbh.prepareStatement("SELECT ID_Usuario, Nick_Usuario, Nicksufix_Usuario, isStoryteller FROM view_salausuario WHERE ID_Sala = ?;");
            stmt.setInt(1, roomid);
            rs = stmt.executeQuery();
            ArrayList<UsuarioSocket> users = new ArrayList<UsuarioSocket>();
            UsuarioSocket user;
            
            while (rs.next()) {
                user = new UsuarioSocket();
                user.setId(rs.getInt("ID_Usuario"));
                user.setNickname(rs.getString("Nick_Usuario"));
                user.setNicknamesufix(rs.getString("Nicksufix_Usuario"));
                user.setStoryteller(rs.getBoolean("isStoryteller"));
                users.add(user);
            }
            
            return users;
        } catch (SQLException ex) {
            Logger.getLogger(UsuarioDAO.class.getName()).log(Level.SEVERE, ex.getMessage());
            return null;
        } finally {
            ConnectionPooler.closeResultset(rs);
            ConnectionPooler.closeStatement(stmt);
            ConnectionPooler.closeConnection(dbh);
        }
    }
    
    public static UsuarioSocket getUsuarioSocket (int roomid, int userid) {
        Connection dbh = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            dbh = ConnectionPooler.getConnection();
            stmt = dbh.prepareStatement("SELECT Nick_Usuario, Nicksufix_Usuario, isStoryteller FROM view_salausuario WHERE ID_Sala = ? AND ID_Usuario = ?;");
            stmt.setInt(1, roomid);
            stmt.setInt(2, userid);
            rs = stmt.executeQuery();
            ArrayList<UsuarioSocket> users = new ArrayList<UsuarioSocket>();
            UsuarioSocket user = new UsuarioSocket();
            
            if (rs.next()) {
                user.setId(userid);
                user.setNickname(rs.getString("Nick_Usuario"));
                user.setNicknamesufix(rs.getString("Nicksufix_Usuario"));
                user.setStoryteller(rs.getBoolean("isStoryteller"));
            }
            
            return user;
        } catch (SQLException ex) {
            Logger.getLogger(UsuarioDAO.class.getName()).log(Level.SEVERE, ex.getMessage());
            return null;
        } finally {
            ConnectionPooler.closeResultset(rs);
            ConnectionPooler.closeStatement(stmt);
            ConnectionPooler.closeConnection(dbh);
        }
    }
    
    public static HashMap<Integer, UsuarioSocket> getUsuarioSockets (int gameid, int userid) {
        Connection dbh = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            dbh = ConnectionPooler.getConnection();
            stmt = dbh.prepareStatement("SELECT ID_Sala, Nick_Usuario, Nicksufix_Usuario, isStoryteller FROM view_salausuario WHERE ID_Jogo = ? AND ID_Usuario = ?;");
            stmt.setInt(1, gameid);
            stmt.setInt(2, userid);
            rs = stmt.executeQuery();
            HashMap<Integer, UsuarioSocket> users = new HashMap<Integer, UsuarioSocket>();
            UsuarioSocket user = new UsuarioSocket();
            
            if (rs.next()) {
                user.setId(userid);
                user.setNickname(rs.getString("Nick_Usuario"));
                user.setNicknamesufix(rs.getString("Nicksufix_Usuario"));
                user.setStoryteller(rs.getBoolean("isStoryteller"));
                
                users.put(rs.getInt("ID_Sala"), user);
            }
            
            return users;
        } catch (SQLException ex) {
            Logger.getLogger(UsuarioDAO.class.getName()).log(Level.SEVERE, ex.getMessage());
            return null;
        } finally {
            ConnectionPooler.closeResultset(rs);
            ConnectionPooler.closeStatement(stmt);
            ConnectionPooler.closeConnection(dbh);
        }
    }
}
