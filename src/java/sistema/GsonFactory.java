/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sistema;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 *
 * @author reddo
 */
public class GsonFactory {
    private static GsonFactory factory;
    private Gson gson = new Gson();
    private Gson gsonExposed = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    
    public static GsonFactory getFactory () {
        if (factory == null) {
            synchronized (factory) {
                if (factory == null) {
                    factory = new GsonFactory();
                }
            }
        }
        return factory;
    }
    
    public Gson getGson () {
        return gson;
    }
    
    public Gson getGsonExposed () {
        return gsonExposed;
    }
}
