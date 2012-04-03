package jeremiaMorling.utils.displayUtils.items;

import javax.microedition.lcdui.Image;
import jeremiaMorling.utils.vector.IClonable;
import jeremiaMorling.utils.vector.ListItem;

public class ChoiceItem extends ListItem {

    private Image smallIcon;

    public ChoiceItem() {}

    public ChoiceItem( String value ) {
        this( value, null, null );
    }

    public ChoiceItem( String value, Image smallIcon, Image largeIcon ) {
        super( value, largeIcon );
        this.smallIcon = smallIcon;
    }

    public Image getSmallIcon() {
        return smallIcon;
    }
    
    public void setSmallIcon( Image smallIcon ) {
        this.smallIcon = smallIcon;
    }

    public IClonable clone() {
        try {
            //ChoiceItem clone = new ChoiceItem( value, smallIcon, largeIcon );
            ChoiceItem clone = (ChoiceItem) getClass().newInstance();
            clone.setText( getText() );
            clone.smallIcon = smallIcon;
            clone.setIcon( getIcon() );
            return clone;
        } catch ( Exception e ) {
            return null;
        }
    }
}
