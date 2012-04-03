package jeremiaMorling.utils.displayUtils.items;

import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import jeremiaMorling.utils.displayUtils.DispLoc;

public class BooleanSettingViewer extends SettingViewer {

    private boolean value;
    private Image noIcon;
    private Image yesIcon;

    public BooleanSettingViewer( Form form, String label, boolean value, Image noIcon, Image yesIcon, boolean enabled ) {
        super( form, label, DispLoc.getText( "common.change" ), enabled );
        this.noIcon = noIcon;
        this.yesIcon = yesIcon;
        setValue( value );
    }

    public void setValue( boolean value ) {
        this.value = value;
        if ( value ) {
            setValue( DispLoc.getText( "common.yes" ), yesIcon );
        } else {
            setValue( DispLoc.getText( "common.no" ), noIcon );
        }
    }
    
    public void edit() {
        setValue( !value );
    }

    public boolean getValue() {
        return value;
    }
}
