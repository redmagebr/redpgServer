<%-- 
    Document   : ActivateAccountResult
    Created on : Aug 25, 2014, 10:41:21 PM
    Author     : reddo
--%>
<%@page import="jsp.Lingo"%>
<%
    Lingo lingo = new Lingo();
    
    lingo.addTranslation("pt_br", "_ACCOUNTACTIVATION_", "Ativação de Conta");
    lingo.addTranslation("pt_br", "_ERROR_", "Houve um erro. Tente novamente.");
    lingo.addTranslation("pt_br", "_INVALID_", "Código Inválido. Tente novamente.");
    lingo.addTranslation("pt_br", "_SUCCESS_", "Conta ativada com sucesso! Você já pode utilizar o sistema.");
    
    
    lingo.addTranslation("en", "_ACCOUNTACTIVATION_", "Account Activation");
    lingo.addTranslation("en", "_ERROR_", "An error occurred. Please try again.");
    lingo.addTranslation("en", "_INVALID_", "The code provided was invalid. Try again.");
    lingo.addTranslation("en", "_SUCCESS_", "Account activated! You may log in now.");
    
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
        <% int result;
           String parameter = request.getParameter("result");
           if (parameter != null) {
                try {
                    result = Integer.parseInt(parameter);
                } catch (NumberFormatException e) {
                    result = -1;
                }
                
                if (result < 0) {
                %>
                <p><% out.print(lingo.getTranslation("_ERROR_")); %></p>
                <p><a href="http://redpg.com.br/service/ActivateAccount.jsp">http://redpg.com.br/service/ActivateAccount.jsp</a></p>
           
                <% } else if (result == 0) { %>
                
                <p><% out.print(lingo.getTranslation("_INVALID_")); %></p>
                <p><a href="http://redpg.com.br/service/ActivateAccount.jsp">http://redpg.com.br/service/ActivateAccount.jsp</a></p>
            
                <% } else { %>
                
                <p><% out.print(lingo.getTranslation("_SUCCESS_")); %></p>
                <p><a href="http://redpg.com.br/">http://redpg.com.br/</a></p>
                
                
        <%         }
           }%>
    </body>
</html>
