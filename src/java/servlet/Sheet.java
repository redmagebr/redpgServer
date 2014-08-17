/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package servlet;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
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
import sistema.Validation;

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
         * UPDATE SHEET
         */
        if (action.equals("update")) {
            Validation valid = new Validation();
            String name = request.getParameter("name");
            String values = request.getParameter("values");
            if (!valid.validName(name) || values == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            try {
                int id = Integer.parseInt(request.getParameter("id"));
                Gson gson = GsonFactory.getFactory().getGson();
                JsonObject jvalues = gson.fromJson(values, JsonObject.class);

                kinds.Sheet sheet = new kinds.Sheet();
                sheet.setId(id);
                sheet.setNome(name);
                sheet.setValues(gson.toJson(jvalues));

                SheetDAO dao = new SheetDAO();
                int result = dao.updateSheet(sheet, userid);
                if (result == 2) {
                    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                } else if (result == 1) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                } else if (result == 0) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                } else if (result == -1) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
            } catch (NumberFormatException | JsonSyntaxException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
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
        
        /**
         * UPDATE PERMISSIONS
         */
        if (action.equals("updatePerm")) {
            if (request.getParameter("id") == null || request.getParameter("privileges") == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            try {
                int id = Integer.parseInt(request.getParameter("id"));
                Gson gson = GsonFactory.getFactory().getGson();
                JsonArray privilegesJson = gson.fromJson(request.getParameter("privileges"), JsonArray.class);
                SheetPermissao sp;
                ArrayList<SheetPermissao> sheets = new ArrayList<SheetPermissao>();
                JsonObject privilegeJson;
                for (int i = 0; i < privilegesJson.size(); i++) {
                    privilegeJson = privilegesJson.get(i).getAsJsonObject();
                    if (!privilegeJson.has("userid") || !privilegeJson.has("visualizar") || !privilegeJson.has("editar")
                        || !privilegeJson.has("deletar")) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        return;
                    }
                    sp = new SheetPermissao();

                    sp.setUserid(privilegeJson.get("userid").getAsInt());
                    sp.setVisualizar(privilegeJson.get("visualizar").getAsBoolean());
                    sp.setEditar(privilegeJson.get("editar").getAsBoolean());
                    sp.setDeletar(privilegeJson.get("deletar").getAsBoolean());
                    sp.setPromote(privilegeJson.get("promote").getAsBoolean());

                    sheets.add(sp);
                }
                int result = SheetDAO.updatePrivileges(sheets, id, userid);
                if (result == 1) {
                    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                } else if (result == 0) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
                }
            } catch (NumberFormatException | JsonSyntaxException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
            return;
        }
        
        /**
         * CREATE SHEET
         */
        if (action.equals("create")) {
            try {
                Validation valid = new Validation();
                String name = request.getParameter("name");
                if (!valid.validName(name)) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }
                int idJogo = Integer.parseInt(request.getParameter("gameid"));
                int idStyle = Integer.parseInt(request.getParameter("idstyle"));
                boolean publica = request.getParameter("publica") != null && request.getParameter("publica").equalsIgnoreCase("true");
                kinds.Sheet sheet = new kinds.Sheet();
                sheet.setCriador(userid);
                sheet.setIdstyle(idStyle);
                sheet.setNome(name);
                sheet.setValues("{}");
                sheet.setPublica(publica);
                
                int creation = SheetDAO.createSheet(sheet, userid, idJogo);
                if (creation == 2) {
                    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                } else if (creation == 1) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                } else if (creation == 0) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
            return;
        }
        
        /**
         * DELETE SHEET
         */
        if (action.equals("delete")) {
            try {
                int id = Integer.parseInt(request.getParameter("id"));
                int result = SheetDAO.deleteSheet(id, userid);
                if (result == 1) {
                    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                } else if (result == 0) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
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
