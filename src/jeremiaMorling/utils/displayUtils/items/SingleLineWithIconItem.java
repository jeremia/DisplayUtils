package jeremiaMorling.utils.displayUtils.items;

import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CustomItem;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import jeremiaMorling.utils.graphics.Color;
import jeremiaMorling.utils.graphics.Padding;
import jeremiaMorling.utils.graphics.Point;
import jeremiaMorling.utils.graphics.Size;

public abstract class SingleLineWithIconItem extends CustomItem implements Runnable {

    protected String value;
    private Image printedValue;
    protected Image icon;
    private boolean enabled;
    private boolean fitToScreen = false;
    private Point iconAnchor;
    private Point valueAnchor;
    private Size minSize;
    private Size prefSize;
    private int borderWidth;
    private Padding padding = new Padding( 2, 2, 0, 0 );
    private boolean buttonAppearance;
    private Font font;
    private int textColor;
    private int backgroundColor;
    private Vector commands = new Vector();
    private static final int SPACE = 1;
    private static final Font FONT_SELECTED = Font.getFont( Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_SMALL );
    private int printedValueX = 0;
    private int widthDifference;
    private boolean isTickerAlive = false;
    private Thread tickerThread;
    private static final int PIXEL_STEP = 3;
    private static final long TICKER_START_SLEEP = 1000;
    private static final long TICKER_SLEEP = 70;
    private static final long TICKER_END_SLEEP = 1500;
    private static final Font FONT_NORMAL = Font.getFont( Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_SMALL );
    private static final Font FONT_BUTTON = Font.getFont( Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_SMALL );
    private static final int BACKGROUND_BUTTON = new Color( 212, 208, 200 ).getRGB();
    private static final int BACKGROUND_NORMAL = Color.WHITE.getRGB();
    private static final int ENABLED_COLOR = Color.BLACK.getRGB();
    private static final int DISABLED_COLOR = new Color( 128, 128, 128 ).getRGB();
    private static final int BLACK = Color.BLACK.getRGB();
    private static final int WHITE = Color.WHITE.getRGB();
    private static final int TOP_LEFT = Graphics.TOP | Graphics.LEFT;
    private static final int screenWidth = new Form( null ).getWidth();

    protected SingleLineWithIconItem( String label, boolean enabled ) {
        super( label );
        setEnabled( enabled, true );
        setButtonAppearance( false, true );
    }

    public SingleLineWithIconItem( String label, String value, Image icon, boolean enabled ) {
        this( label, enabled );
        setValue( value, icon, true );
    }

    public void addCommand( Command command ) {
        if ( command == null ) {
            return;
        }

        commands.addElement( command );
        if ( enabled ) {
            super.addCommand( command );
        }
    }

    public void setDefaultCommand( Command command ) {
        if ( command == null ) {
            return;
        }

        int index = commands.indexOf( command );
        if ( index == -1 ) {
            commands.insertElementAt( command, 0 );
        } else {
            commands.removeElementAt( index );
            commands.insertElementAt( command, 0 );
        }

        if ( enabled ) {
            super.setDefaultCommand( command );
        }
    }

    private Command getCommand( int index ) {
        return (Command) commands.elementAt( index );
    }

    public void setEnabled( boolean enabled ) {
        setEnabled( enabled, false );
    }

    private void setEnabled( boolean enabled, boolean init ) {
        if ( !init && enabled == this.enabled ) {
            return;
        }

        this.enabled = enabled;
        if ( enabled ) {
            textColor = ENABLED_COLOR;

            for ( int i = 0; i < commands.size(); i++ ) {
                super.addCommand( getCommand( i ) );
            }

            if ( commands.size() > 0 ) {
                setDefaultCommand( getCommand( 0 ) );
            }

        } else {
            textColor = DISABLED_COLOR;

            for ( int i = 0; i < commands.size(); i++ ) {
                removeCommand( getCommand( i ) );
            }
        }

        refreshPrintedValue( printedValueX, true );
    }

    public void setValue( String value ) {
        setValue( value, false );
    }

    protected void setValue( String value, boolean init ) {
        setValue( value, icon, init );
    }

    public void setValue( String value, Image icon ) {
        setValue( value, icon, false );
    }

    protected void setValue( String value, Image icon, boolean init ) {
        this.value = value;
        this.icon = icon;

        if ( !init ) {
            resetPrintedValue( false );
        }
        updateAnchorsAndSizes();

        if ( !init ) {
            notifyStateChanged();
        }
    }

    private void resetPrintedValue( boolean shouldRepaint ) {
        if ( printedValue == null || value == null ) {
            return;
        }

        refreshPrintedValue( 0, shouldRepaint );
        widthDifference = FONT_SELECTED.stringWidth( value ) - printedValue.getWidth();

        if ( isTickerAlive ) {
            startTicker();
        }
    }

    private void updateAnchorsAndSizes() {
        int spaceLeft = borderWidth + padding.left;
        int spaceRigth = borderWidth + padding.right;
        int spaceTop = borderWidth + padding.top;
        int spaceBottom = borderWidth + padding.bottom;

        // Update anchors
        valueAnchor = null;
        if ( icon != null ) {
            iconAnchor = null;
            iconAnchor = new Point();
            iconAnchor.x = spaceLeft;
            valueAnchor = new Point( (iconAnchor.x + icon.getWidth() + SPACE), spaceTop );
        } else {
            valueAnchor = new Point( spaceLeft, spaceTop );
        }

        // Update min size
        Font measureFont = FONT_SELECTED;
        minSize = null;
        minSize = new Size();

        if ( icon != null && icon.getHeight() > measureFont.getHeight() ) {
            minSize.height = icon.getHeight() + spaceTop + spaceBottom;
        } else {
            minSize.height = measureFont.getHeight() + spaceTop + spaceBottom;
        }
        int spaceIndex = value.lastIndexOf( ' ' );
        if ( spaceIndex == -1 ) {
            minSize.width = valueAnchor.x + measureFont.stringWidth( value ) + spaceRigth;
        } else {
            minSize.width = valueAnchor.x + measureFont.stringWidth( value.substring( 0, spaceIndex ) ) + spaceRigth;
        }
        
        // update pref size
        prefSize = null;
        prefSize = new Size();
        prefSize.height = minSize.height;
        if ( fitToScreen ) {
            prefSize.width = screenWidth;
        } else {
            prefSize.width = valueAnchor.x + measureFont.stringWidth( value ) + spaceRigth;
        }

        // Centralize icon y anchor
        if ( icon != null ) {
            iconAnchor.y = (prefSize.height - icon.getHeight()) / 2;
        }

        invalidate();
        repaint();
    }

    protected void sizeChanged( int w, int h ) {
        printedValue = Image.createImage( w - valueAnchor.x - borderWidth - padding.right, font.getHeight() );
        resetPrintedValue( true );
    }

    protected int getMinContentHeight() {
        return minSize.height;
    }

    protected int getMinContentWidth() {
        return minSize.width;
    }

    protected int getPrefContentHeight( int width ) {
        return prefSize.height;
    }

    protected int getPrefContentWidth( int height ) {
        return prefSize.width;
    }

    public void setFitToScreen( boolean fitToScreen ) {
        this.fitToScreen = fitToScreen;
        updateAnchorsAndSizes();
    }

    public void setButtonAppearance( boolean buttonAppearance ) {
        setButtonAppearance( buttonAppearance, false );
    }

    private void setButtonAppearance( boolean buttonAppearance, boolean init ) {
        if ( !init && this.buttonAppearance == buttonAppearance ) {
            return;
        }

        this.buttonAppearance = buttonAppearance;

        if ( buttonAppearance ) {
            borderWidth = 3;
            font = FONT_BUTTON;
            backgroundColor = BACKGROUND_BUTTON;
            fitToScreen = false;
        } else {
            borderWidth = 0;
            font = FONT_NORMAL;
            backgroundColor = BACKGROUND_NORMAL;
        }

        if ( value != null ) {
            resetPrintedValue( false );
            updateAnchorsAndSizes();
        }
    }

    protected void paint( Graphics g, int w, int h ) {
        g.setColor( backgroundColor );
        g.fillRect( 0, 0, w, h );
        
        if( buttonAppearance ) {
            g.setColor( WHITE );
            g.fillRect( 0, 0, w, borderWidth );
            g.fillRect( 0, borderWidth, borderWidth, h - borderWidth );
            g.setColor( BLACK );
            g.fillRect( borderWidth, h - borderWidth, w - borderWidth, borderWidth );
            g.fillTriangle( 0, h, borderWidth, h - borderWidth, borderWidth, h );
            g.fillRect( w - borderWidth, borderWidth, borderWidth, h - 2 * borderWidth );
            g.fillTriangle( w - borderWidth, borderWidth, w, 0, w, borderWidth );
        }

        if ( icon != null ) {
            g.drawImage( icon, iconAnchor.x, iconAnchor.y, TOP_LEFT );
        }
        g.setColor( textColor );
        g.setFont( font );
        g.drawImage( printedValue, valueAnchor.x, valueAnchor.y, TOP_LEFT );
    }

    protected boolean traverse( int dir, int viewportWidth, int viewportHeight, int[] visRect_inout ) {
        font = FONT_SELECTED;
        refreshPrintedValue( 0, true );

        isTickerAlive = true;
        startTicker();

        return false;
    }

    private void startTicker() {
        if ( tickerThread == null && widthDifference > 0 ) {
            tickerThread = new Thread( this );
            tickerThread.start();
        }
    }

    protected void traverseOut() {
        if ( buttonAppearance ) {
            font = FONT_BUTTON;
        } else {
            font = FONT_NORMAL;
        }

        isTickerAlive = false;
        refreshPrintedValue( 0, true );
        tickerThread = null;
    }

    public void run() {
        try {
            while (isTickerAlive) {
                printedValueX = 0;
                Thread.sleep( TICKER_START_SLEEP - TICKER_SLEEP );
                while (isTickerAlive && (-printedValueX) < widthDifference) {
                    Thread.sleep( TICKER_SLEEP );
                    if ( !isTickerAlive ) {
                        break;
                    }
                    refreshPrintedValue( printedValueX - PIXEL_STEP, true );
                }

                if ( !isTickerAlive ) {
                    break;
                }
                Thread.sleep( TICKER_END_SLEEP );
                if ( !isTickerAlive ) {
                    break;
                }
                refreshPrintedValue( 0, true );
            }
        } catch ( InterruptedException e ) {
        }
    }

    private void refreshPrintedValue( int printedValueX, boolean shouldRepaint ) {
        this.printedValueX = printedValueX;

        if ( printedValue == null || value == null ) {
            return;
        }

        Graphics g = printedValue.getGraphics();
        g.setColor( backgroundColor );
        g.fillRect( 0, 0, printedValue.getWidth(), printedValue.getHeight() );
        g.setColor( textColor );
        g.setFont( font );
        g.drawString( value, printedValueX, 0, TOP_LEFT );

        if ( shouldRepaint ) {
            repaint();
        }
    }
}
