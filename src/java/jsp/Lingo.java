/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package jsp;

import java.util.HashMap;

/**
 *
 * @author reddo
 */
public class Lingo {
    HashMap<String, LingoMap> lingos = new HashMap<String, LingoMap>();
    String currentLanguage;
    
    public void addTranslation (String lang, String index, String translation) {
        if (!lingos.containsKey(lang)) {
            lingos.put(lang, new LingoMap());
        }
        lingos.get(lang).addTranslation(index, translation);
        if (currentLanguage == null) {
            currentLanguage = lang;
        }
    }
    
    public String getTranslation (String index) {
        return lingos.get(currentLanguage).getTranslation(index);
    }
    
    public void setLanguage (String lang) {
        if (lingos.containsKey(lang)) currentLanguage = lang;
    }
    
    public String getLanguage () {
        return currentLanguage;
    }
}
