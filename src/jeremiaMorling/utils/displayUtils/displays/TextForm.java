/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jeremiaMorling.utils.displayUtils.displays;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.Spacer;
import javax.microedition.lcdui.StringItem;
import jeremiaMorling.utils.displayUtils.DispLoc;
import jeremiaMorling.utils.managers.DM;

/**
 * 
 *
 * @author Jeremia Mörling
 */
public class TextForm extends Form implements CommandListener {
    private Font defaultFont = Font.getFont( Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_SMALL );

    public TextForm( String title ) {
        super( title );
        addCommand( new Command( DispLoc.getText( "common.ok" ), Command.OK, 0 ) );
        setCommandListener( this );
    }

    public TextForm( String title, String text ) {
        this( title );
        appendString( text );
    }
    
    protected void newLine() {
        newLine( 1 );
    }
    
    protected void newLine( int lines ) {
        if( lines < 1 )
            return;
        
        Spacer newLineSpacer = new Spacer( 1, defaultFont.getHeight()*lines );
        newLineSpacer.setLayout( Item.LAYOUT_NEWLINE_BEFORE | Item.LAYOUT_NEWLINE_AFTER );
        append( newLineSpacer );
    }

    protected void appendString( String text ) {
        appendString( text, defaultFont );
    }

    protected void appendString( String text, Font font )
    {
	StringItem line = new StringItem( null, text );
	line.setFont( font );
	line.setLayout( StringItem.LAYOUT_LEFT + StringItem.LAYOUT_NEWLINE_AFTER );
	super.append(line);
    }

    protected void setDefaultFont( Font defaultFont ) {
        this.defaultFont = defaultFont;
    }

    protected Font getDefaultFont() {
        return defaultFont;
    }

    public void commandAction( Command c, Displayable d ) {
        DM.back();
    }
}
