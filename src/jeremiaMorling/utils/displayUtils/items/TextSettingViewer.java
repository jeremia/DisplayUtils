/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jeremiaMorling.utils.displayUtils.items;

import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import jeremiaMorling.utils.displayUtils.DispLoc;
import jeremiaMorling.utils.displayUtils.displays.TextEditor;
import jeremiaMorling.utils.managers.DM;

/**
 * 
 *
 * @author Jeremia Mörling
 */
public class TextSettingViewer extends SettingViewer implements IStringReceiver {
    private String value;
    private int maxSize;

    public TextSettingViewer( Form form, String label, String value, Image icon, int maxSize, boolean enabled ) {
        super( form, label, value, icon, DispLoc.getText( "common.edit" ), enabled );
        this.value = value;
        this.maxSize = maxSize;
    }

    public void setValue( String value ) {
        super.setValue( value );
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void edit() {
        DM.add( new TextEditor( getLabel(), value, maxSize, this ) );
    }

    public void receiveString( String string ) {
        setValue( string );
    }
}
