/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package servlet;

import dao.JogoDAO;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import kinds.Jogo;
import kinds.JogoConvite;
import kinds.JogoUsuarioSala;
import kinds.Usuario;
import sistema.GsonFactory;
import sistema.Validation;

/**
 *
 * @author reddo
 */
@WebServlet(name = "Game", urlPatterns = {"/Game"})
public class Game extends HttpServlet {

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
         * LIST GAMES
         */
        if (action.equals("list")) {
            ArrayList<JogoUsuarioSala> jogos = JogoDAO.getJogos(userid);
            if (jogos == null) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }
            response.setContentType("application/json;charset=UTF-8");
            GsonFactory.getFactory().getGson().toJson(jogos, response.getWriter());
            return;
        }
            
        /**
         * CREATE GAME
         */
        if (action.equals("create")) {
            Validation valid = new Validation();
            String name = request.getParameter("name");
            if (!valid.validName(name)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            String description = request.getParameter("desc");
            if (description == null) {
                description = "";
            }
            boolean freejoin = request.getParameter("freejoin").equalsIgnoreCase("true");
            Jogo game = new Jogo();
            
            game.setName(name);
            game.setDescription(description);
            game.setFreejoin(freejoin);
            game.setCreatorid(userid);
            
            int result = JogoDAO.criaJogo(game);
            if (result == -1) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            } else {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            }
            
            return;
        }
        
        /**
         * DELETE GAME
         */
        if (action.equals("delete")) {
            try {
                int gameid = Integer.parseInt(request.getParameter("id"));
                int result = JogoDAO.deleteGame(gameid, userid);
                if (result == 1) {
                    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                } else if (result == -1) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
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
