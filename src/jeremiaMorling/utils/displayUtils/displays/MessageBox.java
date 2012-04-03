package jeremiaMorling.utils.displayUtils.displays;

import java.util.Vector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.CustomItem;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.lcdui.Spacer;
import jeremiaMorling.utils.displayUtils.DispLoc;
import jeremiaMorling.utils.graphics.Color;

import jeremiaMorling.utils.managers.DM;

public class MessageBox extends Form implements CommandListener, ItemCommandListener {

    private MessageBoxItem messageBoxItem;
    private Spacer topSpacer;
    
    static final int MARGIN = 5;
    private static final Font DEFAULT_FONT = Font.getFont( Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_SMALL );
    private static final int PADDING = 4;
    private static final int ICON_STRING_SPACE = 4;
    private static final int LINE_SPACE = 0;
    private static final int MESSAGE_ITEM_SPACE = 3;
    private static final int TOP_LEFT = Graphics.TOP | Graphics.LEFT;

    public MessageBox( String label ) {
        super( label );
        topSpacer = new Spacer( getWidth(), MARGIN );
        append( topSpacer );
        messageBoxItem = new MessageBoxItem();
        messageBoxItem.setDefaultCommand( new Command( DispLoc.getText( "common.ok" ), Command.OK, 0 ) );
        messageBoxItem.setItemCommandListener( this );
        append( messageBoxItem );

        //addCommand( new Command( ok, Command.OK, 0 ) );
        //setCommandListener( this );
    }

    public void addMessageItem( Image icon, String string, Font font ) {
        messageBoxItem.addMessageItem( icon, string, font );
    }

    public void addMessageItem( String string, Font font ) {
        addMessageItem( null, string, font );
    }

    public void addMessageItem( String string ) {
        addMessageItem( null, string, DEFAULT_FONT );
    }
    
    public void removeMessageItems() {
        messageBoxItem.removeMessageItems();
    }

    public void repaint() {
        messageBoxItem.callRepaint();
    }
    
    public Item getMessageBoxItem() {
        return messageBoxItem;
    }
    
    public void setFocus() {
        DM.setFocus( topSpacer );
        //DM.setFocus( messageBoxItem );
        //DM.add( this, messageBoxItem );
    }

    public void commandAction( Command c, Displayable d ) {
        int commandType = c.getCommandType();

        switch ( commandType ) {
            case Command.OK:
                DM.back();
                break;
        }
    }

    public static int getEndIndex( String string, int startIndex, Font font, int width ) {
        int lastEndIndex = startIndex;
        int endIndex;

        int newLineIndex = string.indexOf( '\n', startIndex );
        if ( newLineIndex != -1 && (font.stringWidth( string.substring( startIndex, newLineIndex ) ) < width) ) {
            return newLineIndex + 1;
        } else if ( newLineIndex == -1 ) {
            newLineIndex = Integer.MAX_VALUE;
        }

        String substring;
        while (true) {
            /*endIndex = string.indexOf( ' ', lastEndIndex );
            if ( endIndex == -1 ) {
                endIndex = string.length();
            }

            if ( newLineIndex < endIndex ) {
                endIndex = newLineIndex;
            }*/
            endIndex = getPossibleEndIndex( string, lastEndIndex );
            if ( newLineIndex < endIndex ) {
                endIndex = newLineIndex;
            }

            substring = string.substring( startIndex, endIndex );
            if ( font.stringWidth( substring ) > width ) {
                if ( lastEndIndex == startIndex ) {
                    return getFittingEndIndex( string, startIndex, endIndex, width, font );
                } else {
                    return lastEndIndex;
                }
            } else if ( endIndex == string.length() ) {
                return endIndex;
            }

            lastEndIndex = endIndex + 1;
        }
    }
    
    private static int getPossibleEndIndex( String string, int startIndex ) {
        for( int i=startIndex; i<string.length(); i++ ) {
            char testChar = string.charAt( i );
            if( testChar == ' ' || testChar == '-' )
                return i;
        }
        
        return string.length();
    }

    private static int getFittingEndIndex( String string, int startIndex, int tooLongEndIndex, int width, Font font ) {
        for ( int i = tooLongEndIndex; i > startIndex; i-- ) {
            String substring = string.substring( startIndex, i );
            if ( font.stringWidth( substring ) < width ) {
                return i;
            }
        }

        return tooLongEndIndex;
    }

    public void commandAction( Command c, Item item ) {
        switch ( c.getCommandType() ) {
            case Command.OK:
                DM.back();
                break;
        }
    }

    /*protected void sizeChanged( int w, int h ) {
        setFocus();
    }*/

    class MessageBoxItem extends CustomItem {

        private Vector messageItems = new Vector();
        private int defaultHeight = getDefaultMinContentHeight();
        private int height = -1;
        private int width = calculateWidth();
        private int backgroundColor = Color.WHITE.getRGB();
        private int fontColor = Color.BLACK.getRGB();

        MessageBoxItem() {
            super( null );
            //calculateWidth();
            //calculateHeight();
            //messageItems = new Vector();
            setLayout( LAYOUT_CENTER );
        }

        private int calculateWidth() {
            return MessageBox.this.getWidth() - 2 * MessageBox.MARGIN;
        }

        private int getDefaultMinContentHeight() {
            return MessageBox.this.getHeight() - 2 * MessageBox.MARGIN - 2;
        }
        
        private int calculateHeight() {
            int y = PADDING;
            for ( int i = 0; i < messageItems.size(); i++ ) {
                y = printMessageItem( null, y, getMessageItem( i ) ) - LINE_SPACE + MESSAGE_ITEM_SPACE;
            }
            y -= MESSAGE_ITEM_SPACE;
            y += PADDING;
                
            return Math.max( defaultHeight, y );
        }
        
        protected int getMinContentWidth() {
            return width;
        }

        protected int getMinContentHeight() {
            if( height == -1 )
                height = calculateHeight();
            
            return height;
        }

        protected int getPrefContentWidth( int height ) {
            return width;
        }

        protected int getPrefContentHeight( int width ) {
            return getMinContentHeight();
        }

        private void setBackgroundColor( int backgroundColor ) {
            this.backgroundColor = backgroundColor;
        }
        
        private void callRepaint() {
            height = calculateHeight();
            repaint();
        }

        protected void paint( Graphics g, int w, int h ) {
            // Paint background
            g.setColor( backgroundColor );
            g.fillRect( 0, 0, w, h );

            // Paint message items
            g.setColor( fontColor );
            g.translate( PADDING, 0 );
            int y = PADDING;
            //int width = getMinContentWidth();
            for ( int i = 0; i < messageItems.size(); i++ ) {
                y = printMessageItem( g, y, getMessageItem( i ) ) - LINE_SPACE + MESSAGE_ITEM_SPACE;
            }

            /*y = y - MESSAGE_ITEM_SPACE + PADDING;

            if( y > height ) {
                height = y;
                invalidate();
            }*/
        }

        private MessageItem getMessageItem( int index ) {
            return (MessageItem) messageItems.elementAt( index );
        }

        private int printMessageItem( Graphics g, int y, /*int width,*/ MessageItem messageItem ) {
            //g.translate( 0, y );
            //y = 0;
            int startY = y;
            int stringX = 0;
            if ( messageItem.icon != null ) {
                stringX = messageItem.icon.getWidth() + ICON_STRING_SPACE;
            }
            int stringWidth = width - stringX - 2 * PADDING;
            if( g != null )
                g.setFont( messageItem.font );

            int startindex = 0;
            int endIndex;
            do {
                endIndex = getEndIndex( messageItem.string, startindex, messageItem.font, stringWidth );
                String line = messageItem.string.substring( startindex, endIndex );
                if( g != null )
                    g.drawString( line, stringX, y, TOP_LEFT );
                y += messageItem.font.getHeight() + LINE_SPACE;
                startindex = endIndex;
            } while (endIndex < messageItem.string.length());

            if ( messageItem.icon != null && g != null ) {
                g.drawImage( messageItem.icon, 0, (y + startY - LINE_SPACE - messageItem.icon.getHeight()) / 2, TOP_LEFT );
            }

            return y;
        }

        private void addMessageItem( Image icon, String string, Font font ) {
            messageItems.addElement( new MessageItem( icon, string, font ) );
        }
        
        private void removeMessageItems() {
            messageItems.removeAllElements();
        }

        private class MessageItem {

            private Image icon;
            private String string;
            private Font font;

            private MessageItem( Image icon, String string, Font font ) {
                this.icon = icon;
                this.string = string;
                this.font = font;
            }
        }
    }
}
