package jeremiaMorling.utils.displayUtils.items;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.List;
import jeremiaMorling.utils.displayUtils.DispLoc;
import jeremiaMorling.utils.displayUtils.displays.SortableList;

import jeremiaMorling.utils.managers.DM;

public class ChoiceSettingViewer extends SettingViewer {

    protected ChoiceItem choices[];
    protected int selectedIndex;

    public ChoiceSettingViewer( Form form, String label, ChoiceItem choices[], int selectedIndex, boolean enabled ) throws IllegalArgumentException {
        super( form, label, DispLoc.getText( "common.change" ), enabled );
        this.choices = choices;
        setSelectedIndex( selectedIndex );
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex( int index ) throws IllegalArgumentException {
        if ( index < 0 ) {
            throw new IllegalArgumentException( "index < 0" );
        } else if ( index >= choices.length ) {
            throw new IllegalArgumentException( "index <= choices.length" );
        }

        this.selectedIndex = index;
        for( int i=0; i<choices.length; i++ )
            choices[i].setSelected( false );
        choices[selectedIndex].setSelected( true );

        setValue( choices[index].getText(), choices[index].getSmallIcon() );
    }
    
    public void edit() {
        DM.add( new ChoiceSettingEditor() );
    }

    private class ChoiceSettingEditor extends SortableList implements CommandListener {

        public ChoiceSettingEditor() {
            super( getLabel(), List.IMPLICIT );

            for ( int i = 0; i < choices.length; i++ ) {
                append( choices[i] );
            }

            this.setSelectedIndex( selectedIndex, true );

            Command chooseCommand = new Command( DispLoc.getText( "common.choose" ), Command.OK, 1 );
            addCommand( chooseCommand );
            addCommand( new Command( DispLoc.getText( "common.cancel" ), Command.CANCEL, 2 ) );
            setSelectCommand( chooseCommand );
            setCommandListener( this );
        }

        public void commandAction( Command c, Displayable d ) {
            int commandType = c.getCommandType();
            switch ( commandType ) {
                case Command.OK:
                    ChoiceSettingViewer.this.setSelectedIndex( this.getSelectedIndex() );
                    DM.back( true );
                    break;
                case Command.CANCEL:
                    DM.back();
                    break;
            }
        }
    }
}
