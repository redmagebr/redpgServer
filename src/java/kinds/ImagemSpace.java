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
public class ImagemSpace {
    private int TotalSpace;
    private int UsedSpace;

    public int getTotalSpace() {
        return TotalSpace;
    }

    public void setTotalSpace(int TotalSpace) {
        this.TotalSpace = TotalSpace;
    }

    public int getUsedSpace() {
        return UsedSpace;
    }

    public void setUsedSpace(int UsedSpace) {
        this.UsedSpace = UsedSpace;
    }
    
    public int getAvailable () {
        return getTotalSpace() - getUsedSpace();
    }
}
