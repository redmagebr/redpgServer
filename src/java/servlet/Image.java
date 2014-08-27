/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package servlet;

import com.google.gson.Gson;
import dao.ImagemDAO;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import kinds.Imagem;
import kinds.ImagemSpace;
import sistema.GsonFactory;

/**
 *
 * @author reddo
 */
@WebServlet(name = "Image", urlPatterns = {"/Image"})
public class Image extends HttpServlet {

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
         * LIST IMAGES
         */
        if (action.equals("list")) {
            ImagemSpace space = ImagemDAO.getSpace(userid);
            ArrayList<Imagem> imagens = ImagemDAO.getImages(userid);
            if (space.getTotalSpace() == -1 || imagens == null) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }
            Gson gson = GsonFactory.getFactory().getGson();
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().print("{\"space\":" + gson.toJson(space)
                                     + "\"images\":" + gson.toJson(imagens) + "}");
            return;
        }
        
        /**
         * UPLOAD IMAGE
         */
        if (action.equals("upload")) {
            Imagem imagem = new Imagem();
            imagem.setName(request.getParameter("name"));
            imagem.setFolder(request.getParameter("folder"));
            imagem.setUploader(userid);
            
            
            boolean result = ImagemDAO.addImage(imagem);
            if (!result) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }
            // Move files
        }
        
        /**
         * DELETE IMAGE
         */
        if (action.equals("delete")) {
            String uuid = request.getParameter("uuid");
            boolean result = ImagemDAO.removeImage(uuid, userid);
            if (!result) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }
            // Delete files
        }
        
        /**
         * UPDATE IMAGE
         */
        if (action.equals("update")) {
            Imagem imagem = new Imagem();
            imagem.setName(request.getParameter("name"));
            imagem.setFolder(request.getParameter("folder"));
            imagem.setUploader(userid);
            
            boolean result = ImagemDAO.updateImage(imagem);
            if (!result) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            } else {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
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
