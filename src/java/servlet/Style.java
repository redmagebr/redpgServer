/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package servlet;

import com.google.gson.Gson;
import dao.SheetStyleDAO;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import kinds.SheetStyle;
import sistema.GsonFactory;

/**
 *
 * @author reddo
 */
@WebServlet(name = "Style", urlPatterns = {"/Style"})
public class Style extends HttpServlet {

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
         * REQUEST STYLE
         */
        if (action.equals("request")) {
            try {
                int requestId = Integer.parseInt(request.getParameter("id"));
                SheetStyle style = SheetStyleDAO.getStyle(requestId, userid);
                if (style != null) {
                    if (style.getName() != null) {
                        response.setContentType("application/json;charset=UTF-8");
                        response.getWriter().print("{\"id\":" + style.getId() + ","
                                + "\"html\":" + style.getHtml() + ","
                                + "\"css\":" + style.getCss() + ","
                                + "\"beforeProcess\":" + style.getBeforeProcess() + ","
                                + "\"afterProcess\":" + style.getAfterProcess()
                                + "}");
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
         * LIST STYLES
         */
        
        if (action.equals("list")) {
            try {
                int gameid = Integer.parseInt(request.getParameter("id"));
                ArrayList<SheetStyle> ss = SheetStyleDAO.getStyles(gameid, userid);
                if (ss != null) {
                    if (ss.size() > 0) {
                        response.setContentType("application/json;charset=UTF-8");
                        GsonFactory.getFactory().getGson().toJson(ss, response.getWriter());
                    } else {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
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
