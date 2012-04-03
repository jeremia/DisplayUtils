package jeremiaMorling.utils.displayUtils.items;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Image;

public class Button extends SingleLineWithIconItem {
	public Button( String label, Image icon, boolean enabled ) {
		this( label, label, icon, enabled );
	}
	
	public Button( String label, String commandText, Image icon, boolean enabled ) {
		super( null, label, icon, enabled );
		setButtonAppearance( true );
		setDefaultCommand( new Command( commandText, Command.OK, 1 ) );
	}
	
	public void setLabel( String label, Image icon ) {
		setValue( label, icon );
	}
}
