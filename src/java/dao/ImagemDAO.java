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
import kinds.Imagem;
import kinds.ImagemSpace;
import sistema.ConnectionPooler;

/**
 *
 * @author reddo
 */
public class ImagemDAO {
    public static ImagemSpace getSpace (int userid) {
        Connection dbh = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ImagemSpace result = new ImagemSpace();
        try {
            dbh = ConnectionPooler.getConnection();
            stmt = dbh.prepareStatement("SELECT Espaco_Total FROM view_usuario_spacegranted WHERE ID_Usuario = ?;");
            stmt.setInt(1, userid);
            rs = stmt.executeQuery();
            if (rs.next()) {
                result.setTotalSpace(rs.getInt("Espaco_Total"));
            }
            ConnectionPooler.closeResultset(rs);
            ConnectionPooler.closeStatement(stmt);
            stmt = dbh.prepareStatement("SELECT Espaco_Usado FROM view_usuario_spaceused WHERE ID_Usuario = ?;");
            stmt.setInt(1, userid);
            rs = stmt.executeQuery();
            if (rs.next()) {
                result.setUsedSpace(rs.getInt("Espaco_Usado"));
            }
            
            return result;
        } catch (SQLException e) {
            result.setTotalSpace(-1);
            return result;
        } finally {
            ConnectionPooler.closeResultset(rs);
            ConnectionPooler.closeStatement(stmt);
            ConnectionPooler.closeConnection(dbh);
        }
    }
    
    public static ArrayList<Imagem> getImages (int userid) {
        Connection dbh = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            dbh = ConnectionPooler.getConnection();
            stmt = dbh.prepareStatement("SELECT uuid, name, folder, ext, size FROM image WHERE uploader = ?;");
            stmt.setInt(1, userid);
            rs = stmt.executeQuery();
            ArrayList<Imagem> imagens = new ArrayList<Imagem>();
            Imagem imagem;
            while (rs.next()) {
                imagem = new Imagem();
                imagem.setExt(rs.getString("ext"));
                imagem.setName(rs.getString("name"));
                imagem.setFolder(rs.getString("folder"));
                imagem.setUuid(rs.getString("uuid"));
                imagem.setSize(rs.getInt("size"));
                imagens.add(imagem);
            }
            return imagens;
        } catch (SQLException e) {
            return null;
        } finally {
            ConnectionPooler.closeResultset(rs);
            ConnectionPooler.closeStatement(stmt);
            ConnectionPooler.closeConnection(dbh);
        }
    }
    
    public static boolean addImage (Imagem imagem) {
        Connection dbh = null;
        PreparedStatement stmt = null;
        try {
            dbh = ConnectionPooler.getConnection();
            stmt = dbh.prepareStatement("INSERT INTO image (uuid, name, folder, ext, size, uploader) VALUES (?, ?, ?, ?, ?, ?);");
            stmt.setString(1, imagem.getUuid());
            stmt.setString(2, imagem.getName());
            stmt.setString(3, imagem.getFolder());
            stmt.setString(4, imagem.getExt());
            stmt.setInt(5, imagem.getSize());
            stmt.setInt(6, imagem.getUploader());
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            return false;
        } finally {
            ConnectionPooler.closeStatement(stmt);
            ConnectionPooler.closeConnection(dbh);
        }
    }
    
    public static boolean removeImage (String uuid, int userid) {
        Connection dbh = null;
        PreparedStatement stmt = null;
        try {
            dbh = ConnectionPooler.getConnection();
            stmt = dbh.prepareStatement("DELETE FROM image WHERE uuid = ? AND uploader = ?;");
            stmt.setString(1, uuid);
            stmt.setInt(2, userid);
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            return false;
        } finally {
            ConnectionPooler.closeStatement(stmt);
            ConnectionPooler.closeConnection(dbh);
        }
    }
    
    public static boolean updateImage (Imagem imagem) {
        Connection dbh = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            dbh = ConnectionPooler.getConnection();
            stmt = dbh.prepareStatement("UPDATE image SET name = ?, folder = ? WHERE uuid = ? AND uploader = ?;");
            stmt.setString(1, imagem.getName());
            stmt.setString(2, imagem.getFolder());
            stmt.setString(3, imagem.getUuid());
            stmt.setInt(4, imagem.getUploader());
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            return false;
        } finally {
            ConnectionPooler.closeResultset(rs);
            ConnectionPooler.closeStatement(stmt);
            ConnectionPooler.closeConnection(dbh);
        }
    }
}
