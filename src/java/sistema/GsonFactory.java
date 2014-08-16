package sistema;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 *
 * @author reddo
 */
public class GsonFactory {
    private static GsonFactory factory;
    private final Gson gson = new Gson();
    private final Gson gsonExposed = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    
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
