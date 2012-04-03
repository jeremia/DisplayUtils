/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jeremiaMorling.utils.displayUtils.items;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.List;
import jeremiaMorling.utils.displayUtils.DispLoc;
import jeremiaMorling.utils.displayUtils.displays.SortableList;
import jeremiaMorling.utils.managers.DM;

/**
 * 
 *
 * @author Jeremia Mörling
 */
public class MultiChoiceSettingViewer extends SettingViewer {
    private ChoiceVector choices;

    public MultiChoiceSettingViewer( Form form, String label, ChoiceVector choices, Image icon, boolean enabled  ) {
        super( form, label, icon, DispLoc.getText( "common.change" ), enabled );
        setChoices( choices );
    }
    
    public void setChoices( ChoiceVector choices ) {
        this.choices = choices;
        updateSelectedChoices();
    }

    private void updateSelectedChoices() {
        StringBuffer text = new StringBuffer();

        for( int i=0; i<choices.size(); i++ ) {
            ChoiceItem choiceItem = choices.getChoiceItem( i );
            if( choiceItem.isSelected() ) {
                if( text.length() > 0 )
                    text.append( ", " );
                text.append( choiceItem.getText() );
            }
        }

        setValue( text.toString() );
    }
    
    public void edit() {
        DM.add( new MultiChoiceSettingEditor() );
    }

    private class MultiChoiceSettingEditor extends SortableList implements CommandListener {

        public MultiChoiceSettingEditor() {
            super( getLabel(), List.MULTIPLE );

            setItemsAndRefresh( choices );

            addCommand( new Command( DispLoc.getText( "common.done" ), Command.OK, 1 ) );
            addCommand( new Command( DispLoc.getText( "common.cancel" ), Command.CANCEL, 1 ) );
            setCommandListener( this );
        }

        public void commandAction( Command c, Displayable d ) {
            int commandType = c.getCommandType();
            switch ( commandType ) {
                case Command.OK:
                    for( int i=0; i<size(); i++ ) {
                        choices.getChoiceItem( i ).setSelected( isSelected( i ) );
                    }
                    updateSelectedChoices();
                    DM.back( true );
                    break;
                case Command.CANCEL:
                    DM.back();
                    break;
            }
        }
    }
}
