/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jeremiaMorling.utils.displayUtils.items;

import jeremiaMorling.utils.vector.ListVector;

/**
 * 
 *
 * @author Jeremia Mörling
 */
public class ChoiceVector extends ListVector {
        public void addElement( Object element ) {
        if( !(element instanceof ChoiceItem) )
            throw new IllegalArgumentException( "element has to be of type ChoiceItem, but is of type " + element.getClass().getName() );

        super.addElement( element );
    }

    public void insertElementAt( Object o, int index ) {
        if( !(o instanceof ChoiceItem) )
            throw new IllegalArgumentException( "Added objects has to be of type ChoiceItem. Object was of type " + o.getClass().getName() );

        super.insertElementAt( o, index );
    }

    public ChoiceItem getChoiceItem( int index ) {
        return (ChoiceItem)elementAt( index );
    }
}
