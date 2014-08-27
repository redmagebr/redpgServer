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
public class LingoMap {
    HashMap <String, String> map = new HashMap <String, String>();
    
    public void addTranslation (String index, String value) {
        map.put(index, value);
    }
    
    public String getTranslation (String index) {
        if (map.containsKey(index)) {
            return map.get(index);
        }
        return index;
    }
}
