<%-- 
    Document   : ActivateAccount
    Created on : Aug 25, 2014, 10:35:28 PM
    Author     : reddo
--%>
<%@page import="jsp.Lingo"%>
<%
    Lingo lingo = new Lingo();
    lingo.addTranslation("pt_br", "_ACCOUNTACTIVATION_", "Ativação de Conta");
    lingo.addTranslation("pt_br", "_UUID_", "Código de Ativação");
    lingo.addTranslation("pt_br", "_SUBMIT_", "Enviar");
    
    lingo.addTranslation("en", "_ACCOUNTACTIVATION_", "Account Activation");
    lingo.addTranslation("en", "_UUID_", "Activation Code");
    lingo.addTranslation("en", "_SUBMIT_", "Submit");
    
    if (request.getParameter("lang") != null) {
        lingo.setLanguage(request.getParameter("lang"));
    }
    %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>RedPG - <% out.print(lingo.getTranslation("_ACCOUNTACTIVATION_")); %></title>
    </head>
    <body>
        <h1><% out.print(lingo.getTranslation("_ACCOUNTACTIVATION_")); %></h1>
        <form action="Account" method="post">
            <input type="hidden" name="action" value="confirm" />
            <input type="hidden" name="redirect" value="1" />
            <input type="hidden" name="lingo" value="<% out.print(lingo.getLanguage()); %>" />
            <p>
                <label for="uuidInput">
                    <% out.print(lingo.getTranslation("_UUID_")); %>:
                </label>
                <input id="uuidInput" name="uuid" value="" />
                <br />
                <input type="submit" value="<% out.print(lingo.getTranslation("_SUBMIT_")); %>" />
            </p>
        </form>
    </body>
</html>
