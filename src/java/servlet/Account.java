/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package servlet;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import dao.UsuarioDAO;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import kinds.UsuarioSistema;
import org.apache.commons.mail.EmailException;
import sistema.GsonFactory;
import sistema.Mailer;
import sistema.Validation;

/**
 *
 * @author reddo
 */
@WebServlet(name = "Account", urlPatterns = {"/Account"})
public class Account extends HttpServlet {

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
        
        
        if (action.equals("requestSession")) {
            response.setContentType("application/json;charset=UTF-8");
            HttpSession session = request.getSession();
            response.getWriter().print("{\"logged\":" +
                    (session.getAttribute("userid") != null ? "true" : "false") + "}");
            return;
        }
        
        if (action.equals("login")) {
            String login = request.getParameter("login");
            String password = request.getParameter("password");
            if (login == null || password == null) {
                HttpSession session = request.getSession();
                if (session.getAttribute("userid") == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }
                login = (String) session.getAttribute("userlogin");
                password = (String) session.getAttribute("userpassword");
            }
            
            UsuarioSistema user = UsuarioDAO.getUsuarioSistema(login, password);
            
            if (user == null) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            } else if (user.getEmail() == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            } else {
                if (user.getLevel() < 1) {
                    resendUuid(user.getEmail());
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
                response.setStatus(HttpServletResponse.SC_OK);
                HttpSession session = request.getSession();
                session.setAttribute("userid", user.getId());
                session.setAttribute("userlogin", user.getEmail());
                session.setAttribute("userpassword", user.getPassword());
                session.setAttribute("userlevel", user.getLevel());
                
                Gson gson = GsonFactory.getFactory().getGson();
                
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().print("{\"user\":"
                        + "{"
                        + "\"id\":" + user.getId()
                        + ",\"nickname\":" + gson.toJson(user.getNickname())
                        + ",\"nicknamesufix\":" + gson.toJson(user.getNicknamesufix())
                        + ",\"config\":" + user.getConfig()
                        + "}"
                        + ", \"session\":"
                        + "\"" + session.getId() + "\"}"
                );
            }
            return;
        }
        
        if (action.equals("StoreConfig")) {
            HttpSession session = request.getSession();

            if (session.getAttribute("userid") == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            } 
            String cfg = request.getParameter("config");
            if (cfg == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            try {
                Gson gson = GsonFactory.getFactory().getGson();
                cfg = gson.toJson(gson.fromJson(cfg, JsonObject.class));
                UsuarioSistema user = new UsuarioSistema();
                user.setId((Integer) session.getAttribute("userid"));
                user.setEmail((String) session.getAttribute("userlogin"));
                user.setPassword((String) session.getAttribute("userpassword"));
                user.setConfig(cfg);
                int result = UsuarioDAO.storeConfig(user);
                if (result == 1) {
                    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                } else if (result == 0) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
            } catch (JsonSyntaxException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
            return;
        }
        
        if (action.equals("confirm")) {
            String uuid = request.getParameter("uuid");
            int result = UsuarioDAO.confirmAccount(uuid);
            if (result < 0) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            } else if (result < 1) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            } else {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            }
            return;
        }
        
        if (action.equals("newAccount")) {
            Validation validator = new Validation();
        
            String email = request.getParameter("email");
            String name = request.getParameter("name");
            String nickname = request.getParameter("nickname");
            String password = request.getParameter("password");

            if (email == null || name == null || nickname == null || password == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            if (!validator.validEmail(email) || !validator.validName(name) || !validator.validNick(nickname) || !validator.validPassword(password)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            
            UsuarioSistema usuario = new UsuarioSistema ();
            usuario.setEmail(email);
            usuario.setName(name);
            usuario.setNickname(nickname);
            usuario.setPassword(password);
            
            int result = UsuarioDAO.createAccount(usuario);
            if (result == 1) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                resendUuid(usuario.getEmail());
            } else if (result == -3) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
            } else if (result == -2) {
                response.setStatus(420);
            } else if (result == -1) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            } 
            return;
        }
        
        if (action.equals("resendUUID")) {
            String email = request.getParameter("email");
            resendUuid(email);
            return;
        }
        
        if (action.equals("logout")) {
            request.getSession().invalidate();
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
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

    private int resendUuid(String email) {
            Validation validator = new Validation();
        
            
            if (!validator.validEmail(email)) {
                return -2;
            }
            
            UsuarioSistema user = new UsuarioSistema();
            user.setEmail(email);
            
            String uuid = UsuarioDAO.getActivationUUID(user);
            if (uuid == null) {
                return -1;
            } else if (uuid.isEmpty()) {
                return 0;
            } else {
                try {
                    Mailer.mailConfirmation(user, uuid);
                    return 1;
                } catch (EmailException ex) {
                    return -1;
                }
            }
    }

}
