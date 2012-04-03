/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jeremiaMorling.utils.displayUtils.displays;

import jeremiaMorling.utils.vector.ListVector;
import jeremiaMorling.utils.vector.IListItem;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.List;
import jeremiaMorling.utils.vector.IComparable;

/**
 * 
 *
 * @author Jeremia Mörling
 */
public class SortableList extends List {
    private ListVector topItems = new ListVector( 2 );
    private ListVector items = new ListVector( 2 );
    private boolean hideTopItems = false;
    
    private static boolean isSonyEricssonNonSmartPhone = false;
    
    public static void setSonyEricssonNonSmartphone( boolean isSonyEricssonNonSmartphone ) {
        SortableList.isSonyEricssonNonSmartPhone = isSonyEricssonNonSmartphone;
    }

    public SortableList( String title, int listType ) {
        super( title, listType );
    }

    public int append( String stringPart, Image imagePart ) {
        return -1;
    }

    public void appendTopItem( IListItem listItem ) {
        topItems.addElement( listItem );
        refresh();
    }
    
    public void appendWithoutRefresh( IListItem listItem ) {
        items.addElement( listItem );
    } 

    public void append( IListItem listItem ) {
        appendWithoutRefresh( listItem );
        super.append( listItem.getText(), listItem.getIcon() );
    }

    public void setItemsAndRefresh( ListVector items ) {
        setItems( items );
        refresh();
    }
    
    public void setItemsAndRefreshSameSelection( ListVector items ) {
        int index = getSelectedIndex();
        IListItem item = getSelectedItem();
        setItemsAndRefresh( items );
        
        if( size() == 0 )
            return;
            
        int newIndex = -1;
        if( item != null ) {
            newIndex = items.indexOf( item );
        }
        if( newIndex != -1 ) {
            setSelectedIndex( newIndex + topItems.size(), true );
        } else if( index != -1 ) {
            if( index > (size()-1) )
                setSelectedIndex( (size()-1), true );
            else
                setSelectedIndex( index, true );
        }
    }

    public void setItems( ListVector items ) {
        if( items == null )
            this.items.removeAllElements();
        else
            this.items = items;
    }
    
    public void setHideTopItems( boolean hideTopItems ) {
        if( this.hideTopItems == hideTopItems )
            return;
        
        this.hideTopItems = hideTopItems;
        refresh();
    }

    public ListVector getItems() {
        return items;
    }

    public IListItem getItem( int index ) {
        if( isTopItem( index ) )
            return null;
        else if( !hideTopItems )
            return items.getListItem( index-topItems.size() );
        else
            return items.getListItem( index );
    }
    
    public IListItem getListItem( int index ) {
        if( isTopItem( index ) )
            return topItems.getListItem( index );
        else if( !hideTopItems )
            return items.getListItem( index - topItems.size() );
        else
            return items.getListItem( index );
    }
    
    public boolean isSelectedItemTopItem() {
        return isTopItem( getSelectedIndex() );
    }
    
    public boolean isTopItem( int index ) {
        if( !hideTopItems && index < topItems.size() )
            return true;
        else
            return false;
    }

    public IListItem getSelectedItem() {
        return( getItem( getSelectedIndex() ) );
    }

    public void delete( Object listItem ) {
        int index = items.indexOf( listItem );
        if( index == -1 )
            return;
        
        //items.removeElementAt( index );
        delete( topItems.size()+index );
    }

    public void sort() {
        items.sort();
        refresh();
    }

    public void refresh() {
        deleteAll();

        if( !hideTopItems ) {
            for ( int i = 0; i < topItems.size(); i++ ) {
                IListItem listItem = topItems.getListItem( i );
                super.append( listItem.getText(), listItem.getIcon() );
                if ( listItem.isSelected() ) {
                    setSelectedIndex( i, true );
                    listItem.setSelected( false );
                }
            }
        }

        for ( int i = 0; i < items.size(); i++ ) {
            IListItem listItem = items.getListItem( i );
            super.append( listItem.getText(), listItem.getIcon() );
            if ( listItem.isSelected() ) {
                setSelectedIndex( i + topItems.size(), true );
                listItem.setSelected( false );
            }
        }
    }
    
    public void refresh( int index ) throws ArrayIndexOutOfBoundsException {
        if( index < 0 || index > (size()-1) )
            throw new ArrayIndexOutOfBoundsException( index );
        
        IListItem listItem = getListItem( index );
        set( index, listItem.getText(), listItem.getIcon() );
    }
    
    public void find( IComparable comparable ) {
        int index = items.binarySearch( comparable, false );
        setSelectedIndex( index + topItems.size(), true );
    }
    
    public int indexOf( IListItem item ) {
        return topItems.size() + items.indexOf( item );
    }
    
    public void deleteSelectedItem() {
        delete( getSelectedIndex() );
    }
    
    public void delete( int index ) throws ArrayIndexOutOfBoundsException {
        if( index < 0 || index > (size()-1) )
            throw new ArrayIndexOutOfBoundsException( index );
        
        super.delete( index );
        
        if( index < topItems.size() )
            topItems.removeElementAt( index );
        else if( index-topItems.size() < items.size() )
            items.removeElementAt( index-topItems.size() );
        
        if( isSonyEricssonNonSmartPhone ) {
            int refreshMaxIndex = Math.min( size(), index + 7 );
            for( int i=index; i<refreshMaxIndex; i++ )
                refresh( i );
        }
    }
}
