/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package kinds;

/**
 *
 * @author reddo
 */
public class SalaUsuario extends Sala {
    private boolean logger;
    private boolean storyteller;
    private boolean cleaner;

    public boolean isCleaner() {
        return cleaner;
    }

    public void setCleaner(boolean cleaner) {
        this.cleaner = cleaner;
    }

    public boolean isLogger() {
        return logger;
    }

    public void setLogger(boolean logger) {
        this.logger = logger;
    }

    public boolean isStoryteller() {
        return storyteller;
    }

    public void setStoryteller(boolean storyteller) {
        this.storyteller = storyteller;
    }
}
