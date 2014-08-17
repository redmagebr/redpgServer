/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package servlet;

import dao.SalaDAO;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import kinds.Sala;
import sistema.Validation;

/**
 *
 * @author reddo
 */
@WebServlet(name = "Room", urlPatterns = {"/Room"})
public class Room extends HttpServlet {

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
         * CREATE ROOM
         */
        if (action.equals("create")) {
            Validation valid = new Validation();
            String name = request.getParameter("name");
            if (!valid.validName(name)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            try {
                Sala sala = new Sala();
                sala.setName(name);
                sala.setCreatorid((Integer) session.getAttribute("userid"));
                sala.setDescription(request.getParameter("description"));
                sala.setPrivateRoom(request.getParameter("private").equalsIgnoreCase("true"));
                sala.setStreamable(request.getParameter("streamable").equalsIgnoreCase("true"));
                sala.setPlayByPost(request.getParameter("playbypost").equalsIgnoreCase("true"));
                int gameid = Integer.parseInt(request.getParameter("gameid"));
                int result = SalaDAO.createRoom(sala, gameid);
                if (result < 0) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                } else if (result == 0) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                } else {
                    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
            return;
        }
        
        
        /**
         * DELETE ROOM
         */
        if (action.equals("delete")) {
            try {
                Sala sala = new Sala();
                sala.setCreatorid(userid);
                sala.setId(Integer.parseInt(request.getParameter("id")));
                int result = SalaDAO.deleteRoom(sala);
                if (result == 1) {
                    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                } else if (result == -1) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                } else {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
            return;
        }
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
