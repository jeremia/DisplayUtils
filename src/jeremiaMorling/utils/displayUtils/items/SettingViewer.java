/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jeremiaMorling.utils.displayUtils.items;

import javax.microedition.lcdui.*;
import jeremiaMorling.utils.managers.DM;

/**
 * 
 *
 * @author Jeremia Mörling
 */
public abstract class SettingViewer implements ItemCommandListener {
    protected StringItem labelItem;
    protected ImageItem iconItem;
    private Spacer iconValueSpace;
    protected StringItem valueItem;

    private int valueMaxWidth;
    private int valueMaxWidthWithDots;
    
    private int id = idCounter++;
    private static int idCounter = 0;
    
    private IEditListener editListener;

    private static final int PADDING = 5;
    private static final int ICON_VALUE_SPACE = 1;
    private static final Font LABEL_FONT = Font.getFont( Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_MEDIUM );
    private static final Font VALUE_FONT = Font.getFont( Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_SMALL );
    private static final int PRINTED_VALUE_TEST_INDEX = 28;
    private static final int DOTS_WIDTH = VALUE_FONT.stringWidth( "..." );

    protected SettingViewer( Form form, String label, String commandLabel, boolean enabled ) {
        this( form, label, null, commandLabel, enabled );
    }

    protected SettingViewer( Form form, String label, Image icon, String commandLabel, boolean enabled ) {
        this( form, label, "", icon, commandLabel, enabled );
    }

    protected SettingViewer( Form form, String label, String value, Image icon, String commandLabel, boolean enabled ) {
        form.append( new Spacer( PADDING, 1 ) );
        labelItem = new StringItem( label, null );
        //labelItem.setFont( LABEL_FONT );
        labelItem.setLayout( Item.LAYOUT_LEFT | Item.LAYOUT_NEWLINE_AFTER );
        form.append( labelItem );

        form.append( new Spacer( PADDING, 1 ) );
        iconItem = new ImageItem( null, icon, Item.LAYOUT_LEFT | Item.LAYOUT_TOP, null );
        form.append( iconItem );

        if( icon != null ) {
            iconValueSpace = new Spacer( ICON_VALUE_SPACE, 0 );
        } else {
            iconValueSpace = new Spacer( 0, 0 );
        }

        valueMaxWidth = form.getWidth() - 2*PADDING - ICON_VALUE_SPACE - 12;
        if( icon != null ) {
            valueMaxWidth -= icon.getWidth();
        }
        valueMaxWidthWithDots = valueMaxWidth - DOTS_WIDTH;
        
        valueItem = new StringItem( null, getPrintedValue( value ) );
        valueItem.setFont( VALUE_FONT );
        valueItem.setLayout( Item.LAYOUT_LEFT | Item.LAYOUT_BOTTOM | Item.LAYOUT_NEWLINE_AFTER );

        if( enabled ) {
            valueItem.setDefaultCommand( new Command( commandLabel, Command.OK, 1 ) );
            valueItem.setItemCommandListener( this );
        }
        else {
            valueItem.setDefaultCommand( new Command( " ", Command.OK, 1 ) );
        }
        form.append( valueItem );
    }
    
    public int getId() {
        return id;
    }
    
    public void setEditListener( IEditListener editListener ) {
        this.editListener = editListener;
    }

    protected void setValue( String value ) {
        valueItem.setText( getPrintedValue( value ) );
    }

    protected void setValue( String value, Image icon ) {
        setValue( value );
        setIcon( icon );
    }
    
    protected void setIcon( Image icon ) {
        iconItem.setImage( icon );
        if( icon != null ) {
            iconValueSpace.setMinimumSize( ICON_VALUE_SPACE, 0 );
        }
        else {
            iconValueSpace.setMinimumSize( 0, 0 );
        }
    }
    
    private static int getNewLineIndex( String value ) {
        for( int i=0; i<value.length(); i++ ) {
            switch( value.charAt( i ) ) {
                case '\n':
                case '\r':
                    return i;
            }
        }
        
        return -1;
    }

    private String getPrintedValue( String value ) {
        if( value == null || value.equals( "" ) )
            return " ";

        int newLineIndex = getNewLineIndex( value );
        if( newLineIndex != -1 ) {
            //newLineIndex--;
            String newLineSubstring = value.substring( 0, newLineIndex );
            if( VALUE_FONT.stringWidth( newLineSubstring ) < valueMaxWidthWithDots ) {
                return getPrintedReturnValue( newLineSubstring );
            } else if( VALUE_FONT.stringWidth( newLineSubstring ) < valueMaxWidth ) {
                return newLineSubstring;
            }
        }

        if( VALUE_FONT.stringWidth( value ) < valueMaxWidth )
            return value;

        String substring;
        if( value.length() > PRINTED_VALUE_TEST_INDEX )
            substring = value.substring( 0, PRINTED_VALUE_TEST_INDEX );
        else
            substring = value;
        
        int subStringWidth = VALUE_FONT.stringWidth( substring );
        if( subStringWidth > valueMaxWidthWithDots ) {
            for( int i=PRINTED_VALUE_TEST_INDEX; i>1; i-- ) {
                substring = value.substring( 0, i );
                if( VALUE_FONT.stringWidth( substring ) < valueMaxWidthWithDots ) {
                    return getPrintedReturnValue( substring );
                }
            }
        }
        else if( subStringWidth < valueMaxWidthWithDots ) {
            for( int i=PRINTED_VALUE_TEST_INDEX; i<value.length(); i++ ) {
                substring = value.substring( 0, i );
                if( VALUE_FONT.stringWidth( substring ) > valueMaxWidthWithDots ) {
                    return getPrintedReturnValue( value.substring( 0, i-1 ) );
                }
            }
        }
        else {
            return getPrintedReturnValue( value.substring( 0, PRINTED_VALUE_TEST_INDEX ) );
        }

        return value;
    }

    private String getPrintedReturnValue( String substring ) {
        return substring + "...";
    }

    protected String getLabel() {
        return labelItem.getLabel();
    }
    
    public void commandAction( Command c, Item item ) {
        if( editListener != null )
            editListener.editing( id );
        edit();
    }
    
    public void setFocus() {
        DM.setFocus( valueItem );
    }
    
    public abstract void edit();
}
