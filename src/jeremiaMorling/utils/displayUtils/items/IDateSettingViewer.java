/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jeremiaMorling.utils.displayUtils.items;

import java.util.Date;

/**
 *
 * @author Jeremia
 */
public interface IDateSettingViewer {
    public void setDate( Date date );
    public Date getDate();
    public void setFocus();
    public int getId();
}
