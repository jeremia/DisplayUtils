/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jeremiaMorling.utils.displayUtils.displays;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;
import jeremiaMorling.utils.displayUtils.DispLoc;
import jeremiaMorling.utils.displayUtils.items.IStringReceiver;
import jeremiaMorling.utils.managers.DM;

/**
 * 
 *
 * @author Jeremia Mörling
 */
public class TextEditor extends TextBox implements CommandListener, IStringReceiver {
    private IStringReceiver resultReceiver;
    
    public TextEditor( String label, int maxSize ) {
        this( label, maxSize, null );
    }
    
    public TextEditor( String label, String value, int maxSize ) {
        this( label, value, maxSize, null );
    }
    
    public TextEditor( String label, int maxSize, IStringReceiver resultReceiver ) {
        this( label, "", maxSize, resultReceiver );
    }
    
    public TextEditor( String label, String value, int maxSize, IStringReceiver resultReceiver ) {
        this( label, value, maxSize, TextField.ANY, resultReceiver );
    }
    
    public TextEditor( String label, int maxSize, int constraints, IStringReceiver resultReceiver ) {
        this( label, "", maxSize, constraints, resultReceiver );
    }

    public TextEditor( String label, String value, int maxSize, int constraints, IStringReceiver resultReceiver ) {
        super( label, value, maxSize, constraints );
        this.resultReceiver = resultReceiver;

        // Add commands
        addCommand( new Command( DispLoc.getText( "common.ok" ), Command.OK, 1 ) );
        addCommand( new Command( DispLoc.getText( "common.cancel" ), Command.CANCEL, 1 ) );
        setCommandListener( this );
    }

    public void commandAction( Command c, Displayable d ) {
        int commandType = c.getCommandType();
        switch ( commandType ) {
            case Command.OK:
                if( resultReceiver != null ) {
                    resultReceiver.receiveString( getString() );
                    DM.back( true );
                }
                else 
                    receiveString( getString() );
                break;
            case Command.CANCEL:
                DM.back();
                break;
        }
    }

    public void receiveString( String string ) {
    }
}
