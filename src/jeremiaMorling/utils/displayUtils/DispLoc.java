package jeremiaMorling.utils.displayUtils;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import jeremiaMorling.utils.managers.Loc;

/**
 * @author Jeremia
 */
public class DispLoc extends Loc {
    private static DispLoc instance;
    
    private static DispLoc getInstance() {
        if( instance == null )
            instance = new DispLoc();
        
        return instance;
    }
    
    private DispLoc() {
        super( "/jeremiaMorling/utils/displayUtils/messages.properties" );
    }
    
    public static String getText( String key ) {
        return getInstance().internalGetText( key );
    }
}