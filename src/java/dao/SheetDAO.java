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
import java.util.logging.Level;
import java.util.logging.Logger;
import kinds.Jogo;
import kinds.Sheet;
import kinds.SheetPermissao;
import kinds.SheetUsuario;
import kinds.Usuario;
import sistema.ConnectionPooler;

/**
 *
 * @author reddo
 */
public class SheetDAO {
    public static int createSheet (Sheet sheet, int userid, int jogoid) {
        Connection dbh = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            dbh = ConnectionPooler.getConnection();
            
            // Check if able
            stmt = dbh.prepareStatement("SELECT 1 FROM view_jogo_usuario "
                    + "WHERE ID_Usuario = ? AND ID_Jogo = ? AND "
                    + "(Criador = 1 OR Permissao_CriarFichas = 1);");
            stmt.setInt(1, userid);
            stmt.setInt(2, jogoid);
            
            rs = stmt.executeQuery();
            if (!rs.next()) {
                return 1;
            }
            
            ConnectionPooler.closeResultset(rs);
            ConnectionPooler.closeStatement(stmt);
            
            
            stmt = dbh.prepareStatement("SELECT 1 FROM view_styles WHERE ID_Style = ? AND (ID_Jogo = ? OR isPublic = 1);");
            stmt.setInt(1, sheet.getIdstyle());
            stmt.setInt(2, jogoid);
            rs = stmt.executeQuery();
            
            if (!rs.next()) {
                return 0;
            }
            
            
            ConnectionPooler.closeResultset(rs);
            ConnectionPooler.closeStatement(stmt);
            
            
            //Begin Transaction
            dbh.setAutoCommit(false);
            
            // Create Sheet
            stmt = dbh.prepareStatement("INSERT INTO sheet_instance (idgame, name, creator, publicView, idstyle, instanceValues) VALUES (?, ?, ?, ?, ?, \"{}\");",
                                        Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, jogoid);
            stmt.setString(2, sheet.getNome());
            stmt.setInt(3, userid);
            stmt.setBoolean(4, sheet.isPublica());
            stmt.setInt(5, sheet.getIdstyle());
            
            if (stmt.executeUpdate() != 1) {
                dbh.rollback();
                return -1;
            }
            
            rs = stmt.getGeneratedKeys();
            
            if (!rs.next()) {
                dbh.rollback();
                return -1;
            }
            
            sheet.setId(rs.getInt(1));
            
            ConnectionPooler.closeResultset(rs);
            ConnectionPooler.closeStatement(stmt);
            
            dbh.commit();
            
            // Return success
            return 2;
        } catch (SQLException e) {
            try {
                if (dbh != null) {
                    dbh.rollback();
                }
            } catch (SQLException ex) {
                
            }
            return -1;
        } finally {
            try {
                if (dbh != null) {
                    dbh.setAutoCommit (true);
                }
            } catch (SQLException e) {
                
            }
            ConnectionPooler.closeResultset(rs);
            ConnectionPooler.closeStatement(stmt);
            ConnectionPooler.closeConnection(dbh);
        }
    }
    
    public static int updateSheet (Sheet sheet, int userid) {
        Connection dbh = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            dbh = ConnectionPooler.getConnection();
            stmt = dbh.prepareStatement("UPDATE view_sheets SET Sheet_Name = ?, Sheet_Values = ?, Public_Sheet = ? "
                                      + "WHERE ID_Sheet = ? AND ID_Usuario = ? AND Permissao_Editar = 1");
            stmt.setString(1, sheet.getNome());
            stmt.setString(2, sheet.getValues());
            stmt.setBoolean(3, sheet.isPublica());
            stmt.setInt(4, sheet.getId());
            stmt.setInt(5, userid);
            if (stmt.executeUpdate() < 1) {
                return 0;
            }
            return 2;
        } catch (SQLException e) {
            Logger.getLogger(SheetDAO.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            return -1;
        } finally {
            ConnectionPooler.closeResultset(rs);
            ConnectionPooler.closeStatement(stmt);
            ConnectionPooler.closeConnection(dbh);
        }
    }
    
    public static int updatePrivileges (ArrayList<SheetPermissao> sheets, int sheetId, int userid) {
        Connection dbh = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            dbh = ConnectionPooler.getConnection();
            stmt = dbh.prepareStatement("SELECT 1 FROM view_sheets WHERE ID_Sheet = ? AND ID_Usuario = ? AND Permissao_Editar = 1;");
            stmt.setInt(1, sheetId);
            stmt.setInt(2, userid);
            rs = stmt.executeQuery();
            if (!rs.next()) {
                return 0;
            }
            ConnectionPooler.closeResultset(rs);
            ConnectionPooler.closeStatement(stmt);
            
            
            dbh.setAutoCommit(false);
            stmt = dbh.prepareStatement("INSERT INTO sheet_instance_usuario (sheetid, usuarioid, visualizar, editar, deletar, promote) "
                                      + "VALUES (?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE "
                                      + "visualizar = ?, editar = ?, deletar = ?, promote = ?;");
            for (SheetPermissao sheet : sheets) {
                stmt.setInt(1, sheetId);
                stmt.setInt(2, sheet.getUserid());
                stmt.setBoolean(3, sheet.isVisualizar());
                stmt.setBoolean(4, sheet.isEditar());
                stmt.setBoolean(5, sheet.isDeletar());
                stmt.setBoolean(6, sheet.isPromote());
                stmt.setBoolean(7, sheet.isVisualizar());
                stmt.setBoolean(8, sheet.isEditar());
                stmt.setBoolean(9, sheet.isDeletar());
                stmt.setBoolean(10, sheet.isPromote());
                stmt.executeUpdate();
            }
            dbh.commit();
            return 1;
        } catch (SQLException e) {
            try {
                if (dbh != null) {
                    dbh.rollback();
                }
            } catch (SQLException ex) {
                
            }
            return -1;
        } finally {
            try {
                if (dbh != null) {
                    dbh.setAutoCommit(true);
                }
            } catch (SQLException e) {
                
            }
            ConnectionPooler.closeResultset(rs);
            ConnectionPooler.closeStatement(stmt);
            ConnectionPooler.closeConnection(dbh);
        }
    }
    
    public static SheetUsuario getSheet (int id, int userid) {
        Connection dbh = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            dbh = ConnectionPooler.getConnection();
            stmt = dbh.prepareStatement("SELECT * FROM view_sheets WHERE ID_Sheet = ? AND ID_Usuario = ? AND (Public_Sheet = 1 OR Permissao_Visualizar = 1);");
            stmt.setInt(1, id);
            stmt.setInt(2, userid);
            rs = stmt.executeQuery();
            SheetUsuario sheet = new SheetUsuario();
            if (!rs.next()) {
                return sheet;
            }
            
            
            sheet.setCriador(rs.getInt("ID_Owner"));
            sheet.setDeletar(rs.getBoolean("Permissao_Deletar"));
            sheet.setEditar(rs.getBoolean("Permissao_Editar"));
            sheet.setId(rs.getInt("ID_Sheet"));
            sheet.setIdstyle(rs.getInt("ID_Style"));
            sheet.setGameid(rs.getInt("ID_Jogo"));
            sheet.setNome(rs.getString("Sheet_Name"));
            sheet.setSegura(rs.getBoolean("Estilo_Seguro"));
            sheet.setValues(rs.getString("Sheet_Values"));
            sheet.setVisualizar(rs.getBoolean("Permissao_Visualizar"));
            sheet.setPublica(rs.getBoolean("Public_Sheet"));
            
            return sheet;
        } catch (SQLException e) {
            return null;
        } finally {
            ConnectionPooler.closeResultset(rs);
            ConnectionPooler.closeStatement(stmt);
            ConnectionPooler.closeConnection(dbh);
        }
    }
    
    public static int deleteSheet (int id, int userid) {
        Connection dbh = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            dbh = ConnectionPooler.getConnection();
            stmt = dbh.prepareStatement("DELETE sheet FROM sheet_instance sheet " +
                                        "INNER JOIN jogo ON jogo.id = sheet.idgame " +
                                        "INNER JOIN jogo_usuario b ON b.jogoid = jogo.id " +
                                        "LEFT JOIN sheet_instance_usuario a ON a.sheetid = sheet.id AND a.usuarioid = b.userid " +
                                        "WHERE sheet.id = ? AND b.userid = ? AND " +
                                        "(sheet.creator = b.userid OR a.deletar = 1 OR jogo.creator = b.userid OR b.deleteall = 1);");
            stmt.setInt(1, id);
            stmt.setInt(2, userid);
            if (stmt.executeUpdate() < 1) {
                return 0;
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
    
    public static ArrayList<SheetPermissao> getPrivileges (int id, int userid) {
        Connection dbh = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            dbh = ConnectionPooler.getConnection();
            stmt = dbh.prepareStatement("SELECT 1 FROM view_sheetperm WHERE ID_Usuario = ? AND ID_Sheet = ? AND Permissao_Promote = 1;");
            stmt.setInt(1, userid);
            stmt.setInt(2, id);
            rs = stmt.executeQuery();
            ArrayList<SheetPermissao> sp = new ArrayList<SheetPermissao>();
            if (!rs.next()) {
                return sp;
            }
            
            
            
            ConnectionPooler.closeResultset(rs);
            ConnectionPooler.closeStatement(stmt);
            
            stmt = dbh.prepareStatement("SELECT ID_Usuario, Nome_Usuario, Sufix_Usuario, "
                                      + "PermissaoV_Visualizar, PermissaoV_Editar, PermissaoV_Deletar, PermissaoV_Promote FROM view_sheetperm "
                                      + "WHERE ID_Sheet = ? AND isOwner = 0;");
            stmt.setInt(1, id);
            
            rs = stmt.executeQuery();
            
            SheetPermissao sheet;
            while (rs.next()) {
                sheet = new SheetPermissao();
                sheet.setId(id);
                sheet.setUserid(rs.getInt("ID_Usuario"));
                sheet.setNicknamesufix(rs.getString("Sufix_Usuario"));
                sheet.setNickname(rs.getString("Nome_Usuario"));
                sheet.setDeletar(rs.getBoolean("PermissaoV_Deletar"));
                sheet.setEditar(rs.getBoolean("PermissaoV_Editar"));
                sheet.setVisualizar(rs.getBoolean("PermissaoV_Visualizar"));
                sheet.setPromote(rs.getBoolean("PermissaoV_Promote"));
                
                sp.add(sheet);
            }
            
            return sp;
        } catch (SQLException e) {
            return null;
        } finally {
            ConnectionPooler.closeResultset(rs);
            ConnectionPooler.closeStatement(stmt);
            ConnectionPooler.closeConnection(dbh);
        }
    }
}
