/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package servlet;

import dao.JogoDAO;
import dao.SalaDAO;
import dao.UsuarioDAO;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import kinds.JogoConvite;
import kinds.Usuario;
import sistema.GsonFactory;
import sistema.Validation;
import websocket.Chat;

/**
 *
 * @author reddo
 */
@WebServlet(name = "Invite", urlPatterns = {"/Invite"})
public class Invite extends HttpServlet {

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
         * SEND INVITE
         */
        if (action.equals("send")) {
            Validation valid = new Validation();
            String name = request.getParameter("name");
            if (!valid.validShortName(request.getParameter("nickname")) ||
                   !valid.validNickSufix(request.getParameter("nicksufix"))
                  ) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            try {
                int jogoid = Integer.parseInt(request.getParameter("gameid"));
                String mensagem = request.getParameter("message");
                Usuario player = new Usuario();
                player.setNickname(request.getParameter("nickname"));
                player.setNicknamesufix(request.getParameter("nicksufix"));
                int result = JogoDAO.enviaConvite(jogoid, player, userid, mensagem);
                // -4: Already accepted invitation -3: Already invited -2: no permission -1: internal error 0: player not found
                if (result == 1) {
                    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                } else if (result == -4) {
                    response.setStatus(423);
                } else if (result == -3) {
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                } else if (result == -2) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                } else if (result == -1) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                } else if (result == 0) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
            return;
        }
        
        /**
         * LIST INVITES
         */
        if (action.equals("list")) {
            ArrayList<JogoConvite> convites = JogoDAO.getListaConvitesPendentes(userid);
            if (convites == null) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }
            GsonFactory.getFactory().getGson().toJson(convites, response.getWriter());
            return;
        }
        
        /**
         * ACCEPT INVITE
         */
        if (action.equals("accept")) {
            try {
                int gameid = Integer.parseInt(request.getParameter("gameid"));
                int result = JogoDAO.aceitarConvite(gameid, userid);
                if (result == 1) {
                    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                    response.getWriter().close();
                    /**
                     * WARN CHAT
                     */
                    Chat.updateRooms (gameid, userid);
                } else if (result == -1) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                } else if (result == 0) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
            return;
        }
        
        /**
         * REJECT INVITE
         */
        if (action.equals("reject")) {
            try {
                int gameid = Integer.parseInt(request.getParameter("gameid"));
                int result = JogoDAO.aceitarConvite(gameid, userid);
                if (result == 1) {
                    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                } else if (result == -1) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                } else if (result == 0) {
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
