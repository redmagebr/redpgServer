/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sistema;

import java.util.regex.Pattern;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

/**
 *
 * @author reddo
 */
public class Validation {
    String name_string = "^[a-zA-Z0-9 çÇéÉíÍóÓáÁàÀâÂÊêãÃõÕôÔ]{3,30}$";
    String shortname_string = "^[a-zA-Z0-9 çÇéÉíÍóÓáÁàÀâÂÊêãÃõÕôÔ]{3,12}$";
    String nick_string = "^[a-zA-Z0-9çÇéÉíÍóÓáÁàÀâÂÊêãÃõÕôÔ]{3,12}$";
    String longname_string = "^[a-zA-Z0-9 çÇéÉíÍóÓáÁàÀâÂÊêãÃõÕôÔ]{3,200}$";
    String login_string = "^[a-z0-9]{3,12}$";
    String pass_string = "^[a-zA-Z0-9]{3,12}$";
    String nick_sufix_string = "^[0-9]{4,4}$";
    Pattern nick_pattern;
    Pattern pass_pattern;
    Pattern name_pattern;
    Pattern shortname_pattern;
    Pattern longname_pattern;
    Pattern login_pattern;
    Pattern nick_sufix_pattern;
    
    public boolean validEmail (String email) {
        if (email == null) return false;
        boolean result = true;
        try {
           InternetAddress emailAddr = new InternetAddress(email);
           emailAddr.validate();
        } catch (AddressException ex) {
           result = false;
        }
        return result;
    }
    
    public boolean validPassword (String str) {
        if (str == null) return false;
        if (pass_pattern == null ) {
            pass_pattern = Pattern.compile(pass_string);
        }
        return pass_pattern.matcher(str).matches();
    }
    
    public boolean validLogin (String str) {
        if (str == null) return false;
        if (login_pattern == null ) {
            login_pattern = Pattern.compile(login_string);
        }
        return login_pattern.matcher(str).matches();
    }
    
    public boolean validName (String str) {
        if (str == null) return false;
        if (name_pattern == null) {
            name_pattern = Pattern.compile(name_string);
        }
        return name_pattern.matcher(str).matches();
    }
    
    public boolean validLongName (String str) {
        if (str == null) return false;
        if (longname_pattern == null) {
            longname_pattern = Pattern.compile(longname_string);
        }
        return longname_pattern.matcher(str).matches();
    }
    
    public boolean validShortName (String str) {
        if (str == null) return false;
        if (shortname_pattern == null) {
            shortname_pattern = Pattern.compile(shortname_string);
        }
        return shortname_pattern.matcher(str).matches();
    }
    
    public boolean validNick (String str) {
        if (str == null) return false;
        if (nick_pattern == null) {
            nick_pattern = Pattern.compile(nick_string);
        }
        return nick_pattern.matcher(str).matches();
    }
    
    public boolean validNickSufix (String str) {
        if (str == null) return false;
        if (nick_sufix_pattern == null) {
            nick_sufix_pattern = Pattern.compile(nick_sufix_string);
        }
        return nick_sufix_pattern.matcher(str).matches();
    }
}
