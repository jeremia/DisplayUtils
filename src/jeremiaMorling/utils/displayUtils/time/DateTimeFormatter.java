/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jeremiaMorling.utils.displayUtils.time;

import java.util.Calendar;
import java.util.Date;
import javax.microedition.global.Formatter;

/**
 * 
 *
 * @author Jeremia Mörling
 */
public class DateTimeFormatter {
    private static Formatter formatter = new Formatter();

    private DateTimeFormatter(){}

    public static String formatShortDateTime( Date dateTime ) {
        if( dateTime == null )
            return "";

        Calendar cDateTime = Calendar.getInstance();
        cDateTime.setTime( dateTime );
        return formatter.formatDateTime( cDateTime, Formatter.DATETIME_SHORT );
    }
    
    public static String formatShortDate( Date date ) {
        if( date == null )
            return "";
        
        Calendar cDate = Calendar.getInstance();
        cDate.setTime( date );
        return formatter.formatDateTime( cDate, Formatter.DATE_SHORT );
    }
}
