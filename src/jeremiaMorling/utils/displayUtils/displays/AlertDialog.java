/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jeremiaMorling.utils.displayUtils.displays;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Image;

/**
 *
 * @author Jeremia
 */
public class AlertDialog extends Alert {
    public AlertDialog( String title, String alertText, Image alertImage ) {
        super( title, alertText, alertImage, null );
        setTimeout( FOREVER );
    }
}
