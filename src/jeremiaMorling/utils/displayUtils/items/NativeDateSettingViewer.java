/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jeremiaMorling.utils.displayUtils.items;

import java.util.Date;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.DateField;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import jeremiaMorling.utils.displayUtils.DispLoc;
import jeremiaMorling.utils.managers.DM;

/**
 *
 * @author Jeremia
 */
public class NativeDateSettingViewer implements IDateSettingViewer, ItemCommandListener {
    private DateField valueItem;
    private int id = idCounter++;
    
    private static int idCounter;
    private static boolean isSonyEricssonNonSmartphone = false;
    
    public static void setSonyEricssonNonSmartphone( boolean isSonyEricssonNonSmartphone ) {
        NativeDateSettingViewer.isSonyEricssonNonSmartphone = isSonyEricssonNonSmartphone;
    }
    
    public NativeDateSettingViewer( Form form, String label, Date value, boolean dateTimeSupported ) {
        if( dateTimeSupported || isSonyEricssonNonSmartphone )
            valueItem = new DateField( label, DateField.DATE_TIME );
        else
            valueItem = new DateField( label, DateField.DATE );
        
        valueItem.setDate( value );
        
        if( !dateTimeSupported && isSonyEricssonNonSmartphone )
            valueItem.setInputMode( DateField.DATE );
        
        valueItem.addCommand( new DateRemoverCommand() );
        valueItem.setItemCommandListener( this );
        
        form.append( valueItem );
    }
    
    public void setDate( Date date ) {
        valueItem.setDate( date );
    }
    
    public Date getDate() {
        return valueItem.getDate();
    }

    public void setFocus() {
        DM.setFocus( valueItem );
    }

    public void commandAction( Command c, Item item ) {
        if( c.getCommandType() == Command.ITEM )
            valueItem.setDate( null );
    }
    
    public int getId() {
        return id;
    }
    
    public class DateRemoverCommand extends Command {
        private DateRemoverCommand() {
            super( DispLoc.getText( "dateSettingEditor.remove" ), Command.ITEM, 1 );
        }
        
        public int getId() {
            return id;
        }
    }
}
