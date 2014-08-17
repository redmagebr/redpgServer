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
import kinds.SheetStyle;
import kinds.Usuario;
import sistema.ConnectionPooler;

/**
 *
 * @author reddo
 */
public class SheetStyleDAO {
        public static SheetStyle getStyle (int id, int userid) {
        Connection dbh = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            dbh = ConnectionPooler.getConnection();
            stmt = dbh.prepareStatement("SELECT * FROM view_styles WHERE ID_Style = ? AND (ID_Usuario = ? || isPublic = 1);");
            stmt.setInt(1, id);
            stmt.setInt(2, userid);
            rs = stmt.executeQuery();
            SheetStyle ss = new SheetStyle();
            if (!rs.next()) {
                return ss;
            }
            ss.setId(rs.getInt("ID_Style"));
            ss.setPublicStyle(rs.getBoolean("isPublic"));
            ss.setAfterProcess(rs.getString("afterProcess"));
            ss.setBeforeProcess(rs.getString("beforeProcess"));
            ss.setCss(rs.getString("CSS"));
            ss.setHtml(rs.getString("HTML"));
            ss.setName(rs.getString("Nome_Estilo"));
            ss.setSeguro(rs.getBoolean("isValidated"));
            ss.setCriador(rs.getInt("ID_Criador"));
            return ss;
        } catch (SQLException e) {
            return null;
        } finally {
            ConnectionPooler.closeResultset(rs);
            ConnectionPooler.closeStatement(stmt);
            ConnectionPooler.closeConnection(dbh);
        }
        
    }
    
    public static ArrayList<SheetStyle> getStyles (int id, int userid) throws SQLException {
        Connection dbh = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ArrayList<SheetStyle> styles = new ArrayList<SheetStyle>();
        try {
            dbh = ConnectionPooler.getConnection();
            stmt = dbh.prepareStatement("SELECT styles.ID_Style AS ID_Style, styles.Nome_Estilo as Nome_Estilo FROM view_styles styles "
                    + "WHERE (ID_Jogo = ? AND ID_Usuario = ? AND sheetCreator = 1) OR isPublic = 1;");
            stmt.setInt(1, id);
            stmt.setInt(2, userid);
            rs = stmt.executeQuery();
            SheetStyle ss;
            while (rs.next()) {
                ss = new SheetStyle();
                ss.setId(rs.getInt("ID_Style"));
                ss.setName(rs.getString("Nome_Estilo"));
                styles.add(ss);
            }
            return styles;
        } catch (SQLException e) {
            throw e;
        } finally {
            ConnectionPooler.closeResultset(rs);
            ConnectionPooler.closeStatement(stmt);
            ConnectionPooler.closeConnection(dbh);
        }
        
    }
}
