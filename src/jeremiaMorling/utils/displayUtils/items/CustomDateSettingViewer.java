package jeremiaMorling.utils.displayUtils.items;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import javax.microedition.lcdui.Canvas;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.CustomItem;
import javax.microedition.lcdui.DateField;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import jeremiaMorling.utils.displayUtils.DispLoc;
import jeremiaMorling.utils.graphics.Color;
import jeremiaMorling.utils.graphics.Rectangle;

import jeremiaMorling.utils.managers.DM;
import jeremiaMorling.utils.displayUtils.time.DateTimeFormatter;

public class CustomDateSettingViewer extends SettingViewer implements IDateSettingViewer {

    private Date date;
    private boolean dateTimeSupported;
    
    private static String[] monthLabels = new String[]{
        DispLoc.getText( "dateSettingEditor.month.january" ),
        DispLoc.getText( "dateSettingEditor.month.february" ),
        DispLoc.getText( "dateSettingEditor.month.march" ),
        DispLoc.getText( "dateSettingEditor.month.april" ),
        DispLoc.getText( "dateSettingEditor.month.may" ),
        DispLoc.getText( "dateSettingEditor.month.june" ),
        DispLoc.getText( "dateSettingEditor.month.july" ),
        DispLoc.getText( "dateSettingEditor.month.august" ),
        DispLoc.getText( "dateSettingEditor.month.september" ),
        DispLoc.getText( "dateSettingEditor.month.october" ),
        DispLoc.getText( "dateSettingEditor.month.november" ),
        DispLoc.getText( "dateSettingEditor.month.december" )
    };
    
    private static String[] weekdayLabels = new String[]{
        DispLoc.getText( "dateSettingEditor.twoCharDay.monday" ),
        DispLoc.getText( "dateSettingEditor.twoCharDay.tuesday" ),
        DispLoc.getText( "dateSettingEditor.twoCharDay.wednesday" ),
        DispLoc.getText( "dateSettingEditor.twoCharDay.thursday" ),
        DispLoc.getText( "dateSettingEditor.twoCharDay.friday" ),
        DispLoc.getText( "dateSettingEditor.twoCharDay.saturday" ),
        DispLoc.getText( "dateSettingEditor.twoCharDay.sunday" )
    };
    
    private static boolean isSonyEricssonNonSmartPhone = false;
    
    public static void setSonyEricssonNonSmartphone( boolean isSonyEricssonNonSmartphone ) {
        CustomDateSettingViewer.isSonyEricssonNonSmartPhone = isSonyEricssonNonSmartphone;
    }

    public CustomDateSettingViewer(
            Form form,
            String label,
            Date date,
            Image icon,
            boolean dateTimeSupported,
            boolean enabled ) {
        super( form, label, icon, DispLoc.getText( "common.edit" ), enabled );
        this.dateTimeSupported = dateTimeSupported;
        setDate( date );
    }

    public void setDate( Date date ) {
        this.date = date;
        if( dateTimeSupported )
            setValue( DateTimeFormatter.formatShortDateTime( date ) );
        else
            setValue( DateTimeFormatter.formatShortDate( date ) );
    }

    public Date getDate() {
        return date;
    }
    
    public void edit() {
        DM.add( new DateSettingEditor() );
    }

    public class DateSettingEditor extends Form implements CommandListener {

        private DatePicker datePicker;
        private DateField timeChooser;
        private Command doneCommand;
        private Command removeCommand;

        public DateSettingEditor() {
            super( getLabel() );

            datePicker = new DatePicker( null, date );
            datePicker.setLayout( Item.LAYOUT_DEFAULT );
            append( datePicker );

            if( dateTimeSupported ) {
                if( !isSonyEricssonNonSmartPhone )
                    timeChooser = new DateField( DispLoc.getText( "dateSettingEditor.time" ), DateField.TIME );
                else
                    timeChooser = new DateField( DispLoc.getText( "dateSettingEditor.time" ), DateField.DATE_TIME );
                
                if( date == null )
                    timeChooser.setDate( new Date() );
                else
                    timeChooser.setDate( date );
                
                if( isSonyEricssonNonSmartPhone )
                    timeChooser.setInputMode( DateField.TIME );
                timeChooser.setLayout( Item.LAYOUT_DEFAULT );
                append( timeChooser );
                
                doneCommand = new Command( DispLoc.getText( "common.done" ), Command.OK, 1 );
            }
            
            addCommand( new Command( DispLoc.getText( "common.cancel" ), Command.CANCEL, 2 ) );
            removeCommand = new Command( DispLoc.getText( "dateSettingEditor.remove" ), Command.BACK, 1 );
            setCommandListener( this );
            //repaint();
        }

        public void commandAction( Command c, Displayable d ) {
            int commandType = c.getCommandType();

            switch( commandType ) {
                case Command.OK:
                    save();
                    break;
                case Command.BACK:
                    setDate( null );
                    DM.back( true );
                    break;
                case Command.CANCEL:
                    DM.back();
                    break;
            }
        }

        private void save() {
            Calendar newDate = Calendar.getInstance();
            newDate.setTime( datePicker.getSelectedDate() );
            if( dateTimeSupported ) {
                Calendar newTime = Calendar.getInstance();
                newTime.setTime( timeChooser.getDate() );
                newDate.set( Calendar.HOUR_OF_DAY, newTime.get( Calendar.HOUR_OF_DAY ) );
                newDate.set( Calendar.MINUTE, newTime.get( Calendar.MINUTE ) );
            }
            setDate( newDate.getTime() );
            DM.back( true );
        }

        private class DatePicker extends CustomItem implements ItemCommandListener {

            private CalendarWidget calendar;
            private boolean editing = false;
            //private Command changeChooseCommand;

            public DatePicker( String label, Date date ) {
                super( label );

                calendar = new CalendarWidget( date );

                setDefaultCommand( new Command( DispLoc.getText( "common.choose" ), Command.OK, 0 ) );
                /*changeChooseCommand = new Command( change, Command.OK, 1 );
                addCommand( changeChooseCommand );
                setDefaultCommand( changeChooseCommand );*/
                setItemCommandListener( this );
            }

            /*public void changeEditing() {
                removeCommand( changeChooseCommand );
                editing = !editing;
                if( editing ) {
                    changeChooseCommand = new Command( choose, Command.OK, 1 );
                    calendar.selectedBgColorThread = new Thread( calendar );
                    calendar.selectedBgColorThread.start();
                } else {
                    changeChooseCommand = new Command( change, Command.OK, 1 );
                    calendar.selectedBgColorThread.interrupt();
                    calendar.selectedBgColorThread = null;
                }

                addCommand( changeChooseCommand );
            }*/

            public void commandAction( Command c, Item item ) {
                if( c.getCommandType() == Command.OK ) {
                    //changeEditing();
                    editing = false;
                    calendar.editingDateThread.interrupt();
                    calendar.editingDateThread = null;
                    if( dateTimeSupported ) {
                        DM.setFocus( timeChooser );
                        DateSettingEditor.this.removeCommand( removeCommand );
                        DateSettingEditor.this.addCommand( doneCommand );
                    }
                    else
                        save();
                }
            }

            protected void keyRepeated( int keyCode ) {
                keyPressed( keyCode );
            }

            protected void keyPressed( int keyCode ) {
                //if( editing ) {
                    boolean isKeyUsed;
                    isKeyUsed = calendar.keyPressed( getGameAction( keyCode ) );
                    if( isKeyUsed ) {
                        repaint();
                    }
                /*} else {
                    super.keyPressed( keyCode );
                }*/
            }

            protected boolean traverse( int dir, int viewportWidth, int viewportHeight, int[] visRect_inout ) {
                if( !editing ) {
                    editing = true;
                    calendar.editingDateThread = new Thread( calendar );
                    calendar.editingDateThread.start();
                    DateSettingEditor.this.removeCommand( doneCommand );
                    DateSettingEditor.this.addCommand( removeCommand );
                }
                
                return true;
            }

            public Date getSelectedDate() {
                return calendar.getSelectedDate();
            }

            protected int getMinContentWidth() {
                return calendar.width;
            }

            protected int getMinContentHeight() {
                return calendar.height;
            }

            protected int getPrefContentWidth( int height ) {
                return calendar.width;
            }

            protected int getPrefContentHeight( int width ) {
                return calendar.height;
            }

            protected void paint( Graphics g, int w, int h ) {
                calendar.paint( g );
            }

            private class CalendarWidget implements Runnable {
                /* starting week day: 0 for monday, 6 for sunday */
                private final int START_WEEKDAY = 0;
                /* cells border properties */
                private final int BORDER_COLOR = new Color( 200, 200, 200 ).getRGB();
                private final int SELECTED_BORDER_COLOR = Color.BLACK.getRGB();
                /* weekday labels properties */
                private final Font WEEKDAY_FONT = Font.getFont( Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_MEDIUM );
                private final int WEEKDAY_BG_COLOR = Color.BLACK.getRGB();
                private final int WEEKDAY_COLOR = Color.WHITE.getRGB();
                /* header (month-year label) properties */
                private final Font HEADER_FONT = Font.getFont( Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_MEDIUM );
                private final Font WEEK_NUMBER_FONT = Font.getFont( Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_MEDIUM );
                private final int HEADER_BG_COLOR = Color.BLACK.getRGB();
                private final int HEADER_COLOR = Color.WHITE.getRGB();
                /* cells properties */
                private final Font FONT = Font.getFont( Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_MEDIUM );
                private final int FORE_COLOR = 0x000000;
                private final int SELECTED_BG_COLOR = Color.BLACK.getRGB();
                private final int SELECTED_FORE_COLOR = Color.WHITE.getRGB();
                private boolean paintSelection = true;
                
                private final int WEEK1_WEEKDAY = new Color( 223, 223, 223 ).getRGB();
                private final int WEEK1_SATURDAY = new Color( 128, 173, 240 ).getRGB();
                private final int WEEK1_SUNDAY = new Color( 240, 132, 129 ).getRGB();
                private final int WEEK2_WEEKDAY = Color.WHITE.getRGB();
                private final int WEEK2_SATURDAY = new Color( 191, 211, 246 ).getRGB();
                private final int WEEK2_SUNDAY = new Color( 247, 191, 192 ).getRGB();
                
                private final int TODAY_BORDER = Color.BLACK.getRGB();
                
                /* internal properties */
                private int width = 0;
                private int height = 0;
                private int headerHeight = 0;
                private int weekHeight = 0;
                private int cellWidth = 0;
                private int cellHeight = 0;
                /* internal time properties */
                private long currentTimestamp = 0;
                private Calendar calendar = null;
                private Calendar today = null;
                private int todayYear = 0;
                private int todayMonth = 0;
                private int todayDay = 0;
                private long weekNumber = 0;
                private int weeks = 6;

                public CalendarWidget( Date date ) {
                    calendar = Calendar.getInstance();
                    today = Calendar.getInstance();

                    if( date == null ) {
                        date = new Date();
                    }

                    setDate( date );

                    initialize();
                }

                private Date getSelectedDate() {
                    return calendar.getTime();
                }

                private void setDate( Date d ) {
                    currentTimestamp = d.getTime();

                    calendar.setTime( d );
                    today.setTime( new Date() );
                    todayYear = today.get( Calendar.YEAR );
                    todayMonth = today.get( Calendar.MONTH );
                    todayDay = today.get( Calendar.DAY_OF_MONTH );
                    weekNumber = calculateWeekNumber();

                    //weeks number can change, depending on week starting day and month total days
                    //this.weeks = (int) Math.ceil( ((double) getStartWeekday() + getMonthDays()) / 7 );
                }

                public void setDate( long timestamp ) {
                    setDate( new Date( timestamp ) );
                }

                private void initialize() {
                    //let's initialize calendar size
                    //this.cellWidth = font.stringWidth( "MM" ) + 2 * padding;
                    this.cellWidth = (DateSettingEditor.this.getWidth()-10)/7;
                    this.cellHeight = FONT.getHeight() - 2;

                    this.headerHeight = HEADER_FONT.getHeight() + 3;
                    this.weekHeight = WEEKDAY_FONT.getHeight() - 1;

                    this.width = 7 * (cellWidth + 1) + 3;
                    this.height =
                            headerHeight + weekHeight
                            + this.weeks * (cellHeight + 1) + 3;
                    invalidate();
                }

                int getMonthDays() {
                    int month = calendar.get( Calendar.MONTH );

                    switch( month ) {
                        case 3:
                        case 5:
                        case 8:
                        case 10:
                            return 30;
                        case 1:
                            return calendar.get( Calendar.YEAR ) % 4 == 0 && calendar.get( Calendar.YEAR ) % 100 != 0 ? 29 : 28;
                        default:
                            return 31;
                    }
                }

                int getStartWeekday() {
                    //let's create a new calendar with same month and year, but with day 1
                    Calendar c = Calendar.getInstance();

                    c.set( Calendar.MONTH, calendar.get( Calendar.MONTH ) );
                    c.set( Calendar.YEAR, calendar.get( Calendar.YEAR ) );
                    c.set( Calendar.DAY_OF_MONTH, 1 );

                    //we must normalize DAY_OF_WEEK returned value
                    return (c.get( Calendar.DAY_OF_WEEK ) + 5) % 7;
                }

                public boolean keyPressed( int key ) {
                    boolean isKeyUsed = false;

                    switch( key ) {
                        case Canvas.UP:
                            go( -7 );
                            isKeyUsed = true;
                            break;
                        case Canvas.DOWN:
                            go( 7 );
                            isKeyUsed = true;
                            break;
                        case Canvas.RIGHT:
                            go( 1 );
                            isKeyUsed = true;
                            break;
                        case Canvas.LEFT:
                            go( -1 );
                            isKeyUsed = true;
                            break;
                    }

                    return isKeyUsed;
                }

                void go( int delta ) {
                    setDate( currentTimestamp + 86400000 * delta );
                }

                public void paint( Graphics g ) {
                    //painting background
                    //g.setColor( bgColor );
                    //g.fillRect( 0, 0, width, height );
                    
                    //Paint the item border
                    if( editing )
                        g.setColor( SELECTED_BORDER_COLOR );
                    else 
                        g.setColor( BORDER_COLOR );
                    g.drawRect( 0, 0, width-1, height-1 );
                    g.drawRect( 1, 1, width-3, height-3 );
                    g.translate( 1, 1 );

                    //painting header (month-year label)
                    g.setColor( HEADER_BG_COLOR );
                    g.fillRect( 1, 1, width-4, headerHeight-1 );
                    
                    g.setFont( HEADER_FONT );
                    g.setColor( HEADER_COLOR );
                    g.drawString(
                            monthLabels[calendar.get( Calendar.MONTH )] + " " + calendar.get( Calendar.YEAR ),
                            width / 2,
                            2,
                            Graphics.TOP | Graphics.HCENTER );

                    //painting week number
                    g.setFont( WEEK_NUMBER_FONT );
                    g.drawString(
                            Long.toString( weekNumber ),
                            4,
                            2,
                            Graphics.TOP | Graphics.LEFT );
                    
                    //Paint line between month-year label and weekday labels
                    g.setColor( BORDER_COLOR );
                    g.drawLine( 1, headerHeight-1, width-4, headerHeight-1);
                    
                    //painting week days labels
                    g.translate( 0, headerHeight );

                    g.setColor( WEEKDAY_BG_COLOR );
                    g.fillRect( 1, 0, width-4, weekHeight );

                    g.setColor( WEEKDAY_COLOR );
                    g.setFont( WEEKDAY_FONT );

                    for( int i = 0; i < 7; i++ ) {
                        if( i == 5 )
                            g.setColor( WEEK2_SATURDAY );
                        else if( i == 6 )
                            g.setColor( WEEK2_SUNDAY );
                        g.drawString(
                                weekdayLabels[(i + START_WEEKDAY) % 7],
                                1 + i * (cellWidth + 1) + cellWidth / 2 + 2,
                                1,
                                Graphics.TOP | Graphics.HCENTER );
                    }
                    
                    //Paint cells
                    g.translate( 0, weekHeight );
                    
                    //Paint cell background
                    //Week 1
                    for( int i = 0; i < 6; i += 2 ) {
                        g.setColor( WEEK1_WEEKDAY );
                        g.fillRect( 1, 1 + ((cellHeight + 1) * i), (cellWidth + 1) * 5, cellHeight );
                        g.setColor( WEEK1_SATURDAY );
                        g.fillRect( 1 + ((cellWidth + 1) * 5), 1 + ((cellHeight + 1) * i), cellWidth, cellHeight );
                        g.setColor( WEEK1_SUNDAY );
                        g.fillRect( 1 + ((cellWidth + 1) * 6), 1 + ((cellHeight + 1) * i), cellWidth, cellHeight );
                    }
                    //Week 2
                    for( int i = 1; i < 6; i += 2 ) {
                        g.setColor( WEEK2_WEEKDAY );
                        g.fillRect( 1, 1 + ((cellHeight + 1) * i), (cellWidth + 1) * 5, cellHeight );
                        g.setColor( WEEK2_SATURDAY );
                        g.fillRect( 1 + ((cellWidth + 1) * 5), 1 + ((cellHeight + 1) * i), cellWidth, cellHeight );
                        g.setColor( WEEK2_SUNDAY );
                        g.fillRect( 1 + ((cellWidth + 1) * 6), 1 + ((cellHeight + 1) * i), cellWidth, cellHeight );
                    }

                    //painting cells borders
                    g.setColor( BORDER_COLOR );
                    for( int i = 0; i < weeks; i++ ) {
                        g.fillRect( 1, i * (cellHeight + 1), width-4, 1 );
                    }
                    for( int i = 1; i < 7; i++ ) {
                        g.fillRect( i * (cellWidth + 1), 0, 1, (height - headerHeight - weekHeight)-3 );
                    }

                    //painting days
                    int days = getMonthDays();
                    int dayIndex = (getStartWeekday() - this.START_WEEKDAY + 7) % 7;

                    g.setColor( FORE_COLOR );

                    int currentDay = calendar.get( Calendar.DAY_OF_MONTH );
                    boolean isTodayYearAndMonth = (calendar.get( Calendar.YEAR ) == todayYear) && (calendar.get( Calendar.MONTH ) == todayMonth);

                    for( int i = 0; i < days; i++ ) {
                        int weekday = (dayIndex + i) % 7;
                        int row = (dayIndex + i) / 7;

                        int x = 1 + weekday * (cellWidth + 1) + cellWidth - 2;
                        int y = 1 + row * (cellHeight + 1) + 1;
                        
                        if( isTodayYearAndMonth && i+1 == todayDay ) {
                            g.setColor( TODAY_BORDER );
                            g.drawRect(
                                    weekday * (cellWidth + 1),
                                    row * (cellHeight + 1),
                                    cellWidth+1,
                                    cellHeight+1 );
                        }

                        //if this is the current day, we'll use selected bg and fore colors
                        if( (i + 1 == currentDay) && paintSelection ) {
                            g.setColor( SELECTED_BG_COLOR );
                            g.fillRect(
                                    1 + weekday * (cellWidth + 1),
                                    1 + row * (cellHeight + 1),
                                    cellWidth, cellHeight );
                            g.setColor( SELECTED_FORE_COLOR );
                        }

                        g.drawString( "" + (i + 1), x, y, Graphics.TOP | Graphics.RIGHT );

                        //if this is the current day, we must restore standard fore color
                        if( i + 1 == currentDay ) {
                            g.setColor( FORE_COLOR );
                        }
                    }
                    //let's traslate back!
                    g.translate( 0, -headerHeight - weekHeight );
                    
                    updateCurrentDayRectangle();
                }
                
                private final long DAY_DURATION = 24*60*60*1000;
                private final long WEEK_DURATION = 7*DAY_DURATION;
                private long calculateWeekNumber() {
                    Calendar firstDayFirstWeek = Calendar.getInstance();
                    firstDayFirstWeek.set( Calendar.YEAR, calendar.get( Calendar.YEAR ) );
                    firstDayFirstWeek.set( Calendar.MONTH, Calendar.JANUARY );
                    firstDayFirstWeek.set( Calendar.DAY_OF_MONTH, 4 );
                    firstDayFirstWeek.set( Calendar.HOUR_OF_DAY, 0 );
                    firstDayFirstWeek.set( Calendar.MINUTE, 0 );
                    firstDayFirstWeek.set( Calendar.SECOND, 0 );
                    firstDayFirstWeek.set( Calendar.MILLISECOND, 0 );
                    
                    int jan4WeekDay = firstDayFirstWeek.get( Calendar.DAY_OF_WEEK );
                    int daysBackToMonday;
                    if( jan4WeekDay == Calendar.SUNDAY )
                        daysBackToMonday = 6;
                    else
                        daysBackToMonday = jan4WeekDay - Calendar.MONDAY;
                    firstDayFirstWeek.setTime( new Date( firstDayFirstWeek.getTime().getTime() - (daysBackToMonday * DAY_DURATION) ) );
                    
                    if( calendar.before( firstDayFirstWeek ) )
                        firstDayFirstWeek.set( Calendar.YEAR, firstDayFirstWeek.get( Calendar.YEAR )-1 );
                    return ((calendar.getTime().getTime() - firstDayFirstWeek.getTime().getTime()) / WEEK_DURATION) + 1;
                }

                private Rectangle currentDayRectangle;
                private void updateCurrentDayRectangle() {
                    int dayIndex = (getStartWeekday() - this.START_WEEKDAY + 7) % 7;
                    int currentDay = calendar.get( Calendar.DAY_OF_MONTH ) - 1;
                    int weekday = (dayIndex + currentDay) % 7;
                    int row = (dayIndex + currentDay) / 7;
                    currentDayRectangle = new Rectangle(
                            2 + weekday * (cellWidth + 1),
                            2 + row * (cellHeight + 1) + headerHeight + weekHeight,
                            cellWidth,
                            cellHeight );
                }
                
                private Thread editingDateThread;
                private static final int SELECTED_BG_COLOR_SLEEP = 500;

                public void run() {
                    try {
                        repaint();
                        updateCurrentDayRectangle();
                        while( editing ) {
                            paintSelection = !paintSelection;
                            repaint( currentDayRectangle.x, currentDayRectangle.y, currentDayRectangle.width, currentDayRectangle.height );
                            Thread.sleep( SELECTED_BG_COLOR_SLEEP );
                        }
                    } catch( InterruptedException ex ) {
                    }
                    paintSelection = true;
                    repaint();
                    //repaint( currentDayRectangle.x, currentDayRectangle.y, currentDayRectangle.width, currentDayRectangle.height );
                }
            }
        }
    }
}
