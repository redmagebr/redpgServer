/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package servlet;

import dao.JogoDAO;
import dao.SheetDAO;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import kinds.JogoUsuarioSheet;
import kinds.SheetPermissao;
import kinds.SheetUsuario;
import sistema.GsonFactory;

/**
 *
 * @author reddo
 */
@WebServlet(name = "Sheet", urlPatterns = {"/Sheet"})
public class Sheet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        
        HttpSession session = request.getSession();
        Integer userid = (Integer) session.getAttribute("userid");
        if (userid == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        
        /**
         * LOAD SHEET
         */
        if (action.equals("request")) {
            try {
                int id = Integer.parseInt(request.getParameter("id"));
                SheetUsuario sheet = SheetDAO.getSheet(id, userid);
                if (sheet != null) {
                    if (sheet.getNome() != null) {
                        response.setContentType("application/json;charset=UTF-8");
                        response.getWriter().print("[{"
                                + "\"id\":" + sheet.getId()
                                + ",\"criador\":" + sheet.getCriador()
                                + ",\"idstyle\":" + sheet.getIdstyle()
                                + ",\"gameid\":" + sheet.getGameid()
                                + ",\"segura\":" + (sheet.isSegura() ? "true" : "false")
                                + ",\"visualizar\":" + (sheet.isVisualizar()? "true" : "false")
                                + ",\"editar\":" + (sheet.isEditar()? "true" : "false")
                                + ",\"deletar\":" + (sheet.isDeletar()? "true" : "false")
                                + ",\"values\":" + sheet.getValues()
                                + ",\"nome\":" + GsonFactory.getFactory().getGson().toJson(sheet.getNome())
                                + "}]");
                    } else {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
            return;
        }
        
        
        /**
         * LIST SHEETS
         */
        if (action.equals("list")) {
            ArrayList<JogoUsuarioSheet> jogos = JogoDAO.getJogosSheets(userid);
            response.setContentType("application/json;charset=UTF-8");
            GsonFactory.getFactory().getGson().toJson(jogos, response.getWriter());
            return;
        }
        
        /**
         * LIST SHEET PERMISSIONS
         */
        if (action.equals("listPerm")) {
            try {
                ArrayList<SheetPermissao> sp = SheetDAO.getPrivileges(Integer.parseInt(request.getParameter("id")), userid);
                if (sp == null) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                } else if (sp.size() == 0) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                } else {
                    response.setContentType("application/json;charset=UTF-8");
                    GsonFactory.getFactory().getGson().toJson(sp, response.getWriter());
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
            return;
        }
        
        
        response.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
