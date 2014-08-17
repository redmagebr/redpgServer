/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sistema;

import kinds.UsuarioSistema;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

/**
 *
 * @author reddo
 */
public class Mailer {
    public static void mailConfirmation (UsuarioSistema user, String uuid) throws EmailException {
        HtmlEmail email = new HtmlEmail();
        email.setHostName("localhost");
        email.setSmtpPort(25);
        email.setAuthentication("sistema@redpg.com.br", "");
        email.setSSLOnConnect(false);
        email.setFrom("sistema@redpg.com.br", "RedPG");
        email.setSubject("RedPG - Confirmação de e-mail");
        email.addTo(user.getEmail());
        
        email.setTextMsg(
                "Olá, " + user.getName() + ".\n\n"
              + "Obrigado por se registrar no sistema!\n\n"
              + "Para começar a utilizar o sistema, é necessário ativar a sua conta.\n"
              + "Você pode fazer isso seguindo o link a seguir:\n\n"
              + "http://redpg.com.br/index.html?confirm#" + uuid
              + "\n\n\nObrigado por ter registrado sua conta!");
        
        
        email.setHtmlMsg(
"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n" +
"<html>\n" +
"    <head>\n" +
"        <meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\">\n" +
"    </head>\n" +
"    <body style=\"margin: 0px;\">\n" +
"        <div style=\"width: 100%; height: 100%; display: table\">\n" +
"            <div style=\"display: table-cell; width: 100%; height: 100%; background-color: #b2b9cf; border: solid 1px #000; text-align: center; vertical-align: middle\">\n" +
"                <div style='display: block; width: 100%; text-align: center'>\n" +
"                    <p style='font-size: 3em; margin: 20px; font-variant: small-caps'>RedPG</p>\n" +
"                </div>\n" +
"                <div style=\"display:inline-block; width: 80%; height: 300px; margin-bottom: 20px; background-color: white; border: solid 2px black;\">\n" +
"                    <p>Olá, " + user.getName() + ".</p>\n" +
"                    <p>&nbsp;</p>\n" +
"                    <p>Obrigado por se registrar no sistema!</p>\n" +
"                    <p>Para começar a utilizar o sistema, é necessário ativar a sua conta.\n" +
"                    <p>Você pode fazer isso seguindo o link a seguir:</p>\n" +
"                    <p><a href='http://redpg.com.br/index.html?confirm#" + uuid + "' target='_blank'>http://redpg.com.br/app/index.html?confirm#" + uuid + "</a></p>\n" +
"                    <p style='margin-top: 20px;'>Obrigado por ter registrado sua conta!</p>\n" +
"                </div>\n" +
"            </div>\n" +
"        </div>\n" +
"    </body>\n" +
"</html>");
        
        email.send();
    }
}
