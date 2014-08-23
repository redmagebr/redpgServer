/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import kinds.Jogo;
import kinds.JogoConvite;
import kinds.JogoPermissao;
import kinds.JogoUsuario;
import kinds.JogoUsuarioSala;
import kinds.JogoUsuarioSheet;
import kinds.SalaUsuario;
import kinds.SheetUsuario;
import kinds.Usuario;
import sistema.ConnectionPooler;

/**
 *
 * @author reddo
 */
public class JogoDAO {
    public static ArrayList<JogoUsuarioSala> getJogos (int userid) {
        ArrayList<JogoUsuarioSala> jogos = new ArrayList<JogoUsuarioSala>();
        HashMap<Integer, JogoUsuarioSala> jogoMap = new HashMap<Integer, JogoUsuarioSala>();
        JogoUsuarioSala jogo;
        Connection dbh = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            dbh = ConnectionPooler.getConnection();
            stmt = dbh.prepareStatement("SELECT * FROM view_jogos WHERE ID_Usuario = ?;");
            stmt.setInt(1, userid);
            rs = stmt.executeQuery();
            while (rs.next()) {
                jogo = new JogoUsuarioSala();
                jogo.setCreatorid(rs.getInt("Criador_Jogo"));
                jogo.setId(rs.getInt("ID_Jogo"));
                jogo.setCreatornick(rs.getString("Nick_Criador"));
                jogo.setCreatorsufix(rs.getString("Nicksufix_Criador"));
                jogo.setFreejoin(rs.getBoolean("Jogo_Publico"));
                jogo.setName(rs.getString("Nome_Jogo"));
                jogo.setDescription(rs.getString("Descricao_Jogo"));
                
                jogo.setCreateSheet(rs.getBoolean("Permissao_CriarFichas"));
                jogo.setViewSheet(rs.getBoolean("Permissao_VisualizarFichas"));
                jogo.setEditSheet(rs.getBoolean("Permissao_EditarFichas"));
                jogo.setDeleteSheet(rs.getBoolean("Permissao_DeletarFichas"));
                jogo.setCreateRoom(rs.getBoolean("Permissao_CriarSala"));
                jogo.setInvite(rs.getBoolean("Permissao_Invite"));
                jogo.setPromote(rs.getBoolean("Permissao_Promote"));
                
                jogo.setRooms(new ArrayList<SalaUsuario>());
                jogoMap.put(jogo.getId(), jogo);
                jogos.add(jogo);
            }
            
            ConnectionPooler.closeResultset(rs);
            ConnectionPooler.closeStatement(stmt);
            
            stmt = dbh.prepareStatement("SELECT ID_Sala, ID_Criador_Sala, Descricao_Sala, ID_Jogo, isLogger, Nome_Sala, isCleaner, isPbP FROM view_jogosalas WHERE ID_Usuario = ?;");
            stmt.setInt(1, userid);
            rs = stmt.executeQuery();
            SalaUsuario sala;
            while (rs.next()) {
                sala = new SalaUsuario();
                sala.setId(rs.getInt("ID_Sala"));
                sala.setCreatorid(rs.getInt("ID_Criador_Sala"));
                sala.setDescription(rs.getString("Descricao_Sala"));
                sala.setLogger(rs.getBoolean("isLogger"));
                sala.setName(rs.getString("Nome_Sala"));
                sala.setCleaner(rs.getBoolean("isCleaner"));
                sala.setPlayByPost(rs.getBoolean("isPbP"));
                
                jogoMap.get(rs.getInt("ID_Jogo")).getRooms().add(sala);
            }
            return jogos;
        } catch (SQLException e) {
            return null;
        } finally {
            ConnectionPooler.closeResultset(rs);
            ConnectionPooler.closeStatement(stmt);
            ConnectionPooler.closeConnection(dbh);
        }
    }
    
    public static JogoUsuario getJogo (int userid, int gameid) {
        Connection dbh = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            dbh = ConnectionPooler.getConnection();
            stmt = dbh.prepareStatement("SELECT * FROM view_jogos WHERE ID_Usuario = ? AND ID_Jogo = ?;");
            stmt.setInt(1, userid);
            stmt.setInt(2, gameid);
            rs = stmt.executeQuery();
            JogoUsuario jogo = new JogoUsuario();
            if (rs.next()) {
                jogo.setCreatorid(rs.getInt("Criador_Jogo"));
                jogo.setId(rs.getInt("ID_Jogo"));
                jogo.setCreatornick(rs.getString("Nick_Criador"));
                jogo.setCreatorsufix(rs.getString("Nicksufix_Criador"));
                jogo.setFreejoin(rs.getBoolean("Jogo_Publico"));
                jogo.setName(rs.getString("Nome_Jogo"));
                jogo.setDescription(rs.getString("Descricao_Jogo"));
                
                jogo.setCreateSheet(rs.getBoolean("Permissao_CriarFichas"));
                jogo.setViewSheet(rs.getBoolean("Permissao_VisualizarFichas"));
                jogo.setEditSheet(rs.getBoolean("Permissao_EditarFichas"));
                jogo.setDeleteSheet(rs.getBoolean("Permissao_DeletarFichas"));
                jogo.setCreateRoom(rs.getBoolean("Permissao_CriarSala"));
                jogo.setInvite(rs.getBoolean("Permissao_Invite"));
                jogo.setPromote(rs.getBoolean("Permissao_Promote"));
            }
            return jogo;
        } catch (SQLException e) {
            return null;
        } finally {
            ConnectionPooler.closeResultset(rs);
            ConnectionPooler.closeStatement(stmt);
            ConnectionPooler.closeConnection(dbh);
        }
    }
    
    public static ArrayList<JogoUsuarioSheet> getJogosSheets (int userid) {
        ArrayList<JogoUsuarioSheet> jogos = new ArrayList<JogoUsuarioSheet>();
        HashMap<Integer, JogoUsuarioSheet> jogoMap = new HashMap<Integer, JogoUsuarioSheet>();
        JogoUsuarioSheet jogo;
        SheetUsuario sheet;
        
        Connection dbh = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            dbh = ConnectionPooler.getConnection();
            stmt = dbh.prepareStatement("SELECT * FROM view_jogos WHERE ID_Usuario = ?;");
            stmt.setInt(1, userid);
            rs = stmt.executeQuery();
            while (rs.next()) {
                jogo = new JogoUsuarioSheet();
                jogo.setCreatorid(rs.getInt("Criador_Jogo"));
                jogo.setId(rs.getInt("ID_Jogo"));
                jogo.setCreatornick(rs.getString("Nick_Criador"));
                jogo.setCreatorsufix(rs.getString("Nicksufix_Criador"));
                jogo.setFreejoin(rs.getBoolean("Jogo_Publico"));
                jogo.setName(rs.getString("Nome_Jogo"));
                jogo.setDescription(rs.getString("Descricao_Jogo"));
                
                jogo.setCreateSheet(rs.getBoolean("Permissao_CriarFichas"));
                jogo.setViewSheet(rs.getBoolean("Permissao_VisualizarFichas"));
                jogo.setEditSheet(rs.getBoolean("Permissao_EditarFichas"));
                jogo.setDeleteSheet(rs.getBoolean("Permissao_DeletarFichas"));
                jogo.setCreateRoom(rs.getBoolean("Permissao_CriarSala"));
                jogo.setInvite(rs.getBoolean("Permissao_Invite"));
                jogo.setPromote(rs.getBoolean("Permissao_Promote"));
                
                jogo.setSheets(new ArrayList<SheetUsuario>());
                
                jogoMap.put(jogo.getId(), jogo);
                
                jogos.add(jogo);
            }
            ConnectionPooler.closeResultset(rs);
            ConnectionPooler.closeStatement(stmt);
            
            stmt = dbh.prepareStatement("SELECT ID_Sheet, ID_Jogo, ID_Style, Estilo_Seguro, ID_Owner, "
                    + "Public_Sheet, Sheet_Name, Permissao_Visualizar, Permissao_Editar, Permissao_Deletar, Permissao_Promote FROM view_sheets WHERE ID_Usuario = ? AND Permissao_Visualizar = 1;");
            stmt.setInt(1, userid);
            rs = stmt.executeQuery();
            while (rs.next()) {
                sheet = new SheetUsuario();
                sheet.setId(rs.getInt("ID_Sheet"));
                sheet.setIdstyle(rs.getInt("ID_Style"));
                sheet.setCriador(rs.getInt("ID_Owner"));
                sheet.setSegura(rs.getBoolean("Estilo_Seguro"));
                sheet.setPublica(rs.getBoolean("Public_Sheet"));
                sheet.setNome(rs.getString("Sheet_Name"));
                sheet.setVisualizar(rs.getBoolean("Permissao_Visualizar"));
                sheet.setEditar(rs.getBoolean("Permissao_Editar"));
                sheet.setDeletar(rs.getBoolean("Permissao_Deletar"));
                sheet.setPromote(rs.getBoolean("Permissao_Promote"));
                jogoMap.get(rs.getInt("ID_Jogo")).getSheets().add(sheet);
            }
            return jogos;
        } catch (SQLException e) {
            return null;
        } finally {
            ConnectionPooler.closeResultset(rs);
            ConnectionPooler.closeStatement(stmt);
            ConnectionPooler.closeConnection(dbh);
        }
    }
    
    public static int deleteGame (int jogoid, int userid) {
        Connection dbh = null;
        PreparedStatement stmt = null;
        try {
            dbh = ConnectionPooler.getConnection();
            stmt = dbh.prepareStatement("UPDATE jogo SET deleted = 1, deletedDate = ? WHERE id = ? AND creator = ?;");
            stmt.setDate(1, new Date(System.currentTimeMillis()));
            stmt.setInt(2, jogoid);
            stmt.setInt(3, userid);
            
            return stmt.executeUpdate();
        } catch (SQLException e) {
            return -1;
        } finally {
            ConnectionPooler.closeStatement(stmt);
            ConnectionPooler.closeConnection(dbh);
        }
    }
    
    public static ArrayList<JogoConvite> getListaConvitesPendentes (int userid) {
        ArrayList<JogoConvite> jogos = new ArrayList<JogoConvite>();
        
        Connection dbh = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            dbh = ConnectionPooler.getConnection();
            
            stmt = dbh.prepareStatement("SELECT * FROM view_jogosconvites WHERE ID_Usuario = ?;");
            stmt.setInt(1, userid);
            
            rs = stmt.executeQuery();
            
            JogoConvite jogo;
            while (rs.next()) {
                jogo = new JogoConvite();
                jogo.setCreatorid(rs.getInt("ID_Criador"));
                jogo.setId(rs.getInt("ID_Jogo"));
                jogo.setName(rs.getString("Nome_Jogo"));
                jogo.setCreatornick(rs.getString("Nick_Criador"));
                jogo.setCreatorsufix(rs.getString("NickSufix_Criador"));
                jogo.setMensagemConvite(rs.getString("Convite_Mensagem"));
                
                jogos.add(jogo);
            }
            return jogos;
        } catch (SQLException e) {
            return null;
        } finally {
            ConnectionPooler.closeResultset(rs);
            ConnectionPooler.closeStatement(stmt);
            ConnectionPooler.closeConnection(dbh);
        }
    }
    
    /**
     * 
     * @param jogoid
     * @param player
     * @param ownerid
     * @param mensagem
     * @return 
     * -4: Already accepted invitation <br />
     * -3: Already invited <br />
     * -2: no permission <br />
     * -1: internal error <br />
     *  0: player not found <br />
     * 
     */
    public static int enviaConvite (int jogoid, Usuario player, int ownerid, String mensagem) {
        Connection dbh = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            dbh = ConnectionPooler.getConnection();
            
            stmt = dbh.prepareStatement("SELECT 1 FROM view_jogos WHERE ID_Usuario = ? AND Permissao_Invite = ? AND ID_Jogo = ?;");
            
            stmt.setInt(1, ownerid);
            stmt.setBoolean(2, true);
            stmt.setInt(3, jogoid);
            
            rs = stmt.executeQuery();
            
            if (!rs.next()) return -2;
            
            ConnectionPooler.closeResultset(rs);
            ConnectionPooler.closeStatement(stmt);
            
            
            
            stmt = dbh.prepareStatement("SELECT Convite_Aceito, ID_Usuario FROM view_usuario_convite "
                    + "WHERE ID_Jogo = ? AND Nick_Usuario = ? AND Nicksufix_Usuario = ?");
            
            stmt.setInt(1, jogoid);
            stmt.setString(2, player.getNickname());
            stmt.setString(3, player.getNicknamesufix());
            
            rs = stmt.executeQuery();
            
            if (!rs.next()) return 0;
            
            String conviteAceito = rs.getString("Convite_Aceito");
            if (conviteAceito != null) {
                if (conviteAceito.equals("0")) {
                    return -3;
                } else if (conviteAceito.equals("1")) {
                    return -4;
                }
            }
            
            player.setId(rs.getInt("ID_Usuario"));
            
            ConnectionPooler.closeResultset(rs);
            ConnectionPooler.closeStatement(stmt);
            
            stmt = dbh.prepareStatement("INSERT INTO jogo_convite (idjogo, userid, mensagem) VALUES (?, ?, ?);");
            stmt.setInt(1, jogoid);
            stmt.setInt(2, player.getId());
            stmt.setString(3, mensagem);
            
            if (stmt.executeUpdate() <= 0) { return -1; }
            
            return 1;
        } catch (SQLException e) {
            return -1;
        } finally {
            ConnectionPooler.closeResultset(rs);
            ConnectionPooler.closeStatement(stmt);
            ConnectionPooler.closeConnection(dbh);
        }
    }
    
    public static int aceitarConvite (int jogoid, int userid) {
        Connection dbh = null;
        PreparedStatement stmt = null;
        try {
            dbh = ConnectionPooler.getConnection();
            dbh.setAutoCommit(false);
            stmt = dbh.prepareStatement("UPDATE jogo_convite SET accepted = 1 WHERE idjogo = ? AND userid = ?;");
            stmt.setInt(1, jogoid);
            stmt.setInt(2, userid);
            if(stmt.executeUpdate() < 1) {
                dbh.rollback();
                return 0;
            }
            ConnectionPooler.closeStatement(stmt);
            
            stmt = dbh.prepareStatement("INSERT INTO jogo_usuario (jogoid, userid) VALUES (?, ?);");
            stmt.setInt(1, jogoid);
            stmt.setInt(2, userid);
            
            if (stmt.executeUpdate() <= 0) {
                dbh.rollback();
                return 0;
            }
            
            dbh.commit();
            
            return 1;
        } catch (SQLException e) {
            if (dbh != null) {
                try {
                    dbh.rollback();
                } catch (SQLException ex) { }
            }
            return -1;
        } finally {
            if (dbh != null) {
                try {
                    dbh.setAutoCommit(true);
                } catch (SQLException ex) {}
            }
            ConnectionPooler.closeStatement(stmt);
            ConnectionPooler.closeConnection(dbh);
        }
    }
    
    public static int criaJogo (Jogo jogo) {
        Connection dbh = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            dbh = ConnectionPooler.getConnection();
            dbh.setAutoCommit(false);
            stmt = dbh.prepareStatement(
                "INSERT INTO jogo (name, description, creator, freejoin) "
                + "VALUES (?, ?, ?, ?);",
                Statement.RETURN_GENERATED_KEYS
            );
            
            stmt.setString(1, jogo.getName());
            stmt.setString(2, jogo.getDescription());
            stmt.setInt(3, jogo.getCreatorid());
            stmt.setBoolean(4, jogo.isFreejoin());
            
            if (stmt.executeUpdate() <= 0) {
                dbh.rollback();
                return -1;
            }
            
            rs = stmt.getGeneratedKeys();
            
            if (!rs.next()) {
                dbh.rollback();
                return -1;
            }
            
            int jogoid = rs.getInt(1);
            
            ConnectionPooler.closeResultset(rs);
            ConnectionPooler.closeStatement(stmt);
            
            stmt = dbh.prepareStatement("INSERT INTO jogo_usuario (jogoid, userid) VALUES (?, ?);");
            stmt.setInt(1, jogoid);
            stmt.setInt(2, jogo.getCreatorid());
            
            if (stmt.executeUpdate() <= 0) {
                dbh.rollback();
                return -1;
            }
            
            ConnectionPooler.closeStatement(stmt);
            
            stmt = dbh.prepareStatement("INSERT INTO jogo_convite (idjogo, userid, accepted) VALUES (?, ?, ?);");
            stmt.setInt(1, jogoid);
            stmt.setInt(2, jogo.getCreatorid());
            stmt.setBoolean(3, true);
            
            if (stmt.executeUpdate() <= 0) {
                dbh.rollback();
                return -1;
            }
            
            dbh.commit();
            return 1;
        } catch (SQLException e) {
            try {
                if (dbh != null) {
                    dbh.rollback();
                }
            } catch (SQLException ex) {
                Logger.getLogger(JogoDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
            return -1;
        } finally {
            if (dbh != null) {
                try {
                    dbh.setAutoCommit(false);
                } catch (SQLException ex) {
                    Logger.getLogger(JogoDAO.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            ConnectionPooler.closeResultset(rs);
            ConnectionPooler.closeStatement(stmt);
            ConnectionPooler.closeConnection(dbh);
        }
    }
    
    
    public static ArrayList<JogoPermissao> getPrivileges (int gameid, int userid) {
        Connection dbh = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            JogoUsuario perms = getJogo(userid, gameid);
            ArrayList<JogoPermissao> jogos = new ArrayList<JogoPermissao>();
            if (!perms.isPromote()) {
                return jogos;
            }
            dbh = ConnectionPooler.getConnection();
            
            stmt = dbh.prepareStatement("SELECT ID_Usuario, Nick_Usuario, Nicksufix_Usuario, "
                                      + "Permissao_CriarFichas, Permissao_VisualizarFichas, Permissao_EditarFichas, "
                                      + "Permissao_DeletarFichas, Permissao_CriarSala, Permissao_Invite, Permissao_Promote "
                                      + "FROM view_jogos "
                                      + "WHERE ID_Jogo = ? AND Criador_Jogo != ID_Usuario;");
            stmt.setInt(1, gameid);
            
            rs = stmt.executeQuery();
            
            JogoPermissao jogo;
            while (rs.next()) {
                jogo = new JogoPermissao();
                jogo.setId(gameid);
                jogo.setUserid(rs.getInt("ID_Usuario"));
                jogo.setNicknamesufix(rs.getString("Nicksufix_Usuario"));
                jogo.setNickname(rs.getString("Nick_Usuario"));
                jogo.setDeleteSheet(rs.getBoolean("Permissao_DeletarFichas"));
                jogo.setEditSheet(rs.getBoolean("Permissao_EditarFichas"));
                jogo.setViewSheet(rs.getBoolean("Permissao_VisualizarFichas"));
                jogo.setPromote(rs.getBoolean("Permissao_Promote"));
                jogo.setCreateRoom(rs.getBoolean("Permissao_CriarSala"));
                jogo.setCreateSheet(rs.getBoolean("Permissao_CriarFichas"));
                jogo.setInvite(rs.getBoolean("Permissao_Invite"));
                
                jogos.add(jogo);
            }
            
            return jogos;
        } catch (SQLException e) {
            return null;
        } finally {
            ConnectionPooler.closeResultset(rs);
            ConnectionPooler.closeStatement(stmt);
            ConnectionPooler.closeConnection(dbh);
        }
    }
}
