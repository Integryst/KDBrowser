package com.integryst.kdbrowser.httphelpers;

import com.integryst.kdbrowser.extjs.*;
import com.integryst.kdbrowser.helpers.*;

import java.sql.Timestamp;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.*;

import org.apache.log4j.Logger;

public class FileGrid {
    private static Logger LOG = Logger.getLogger(FileGrid.class);

    public FileGrid() {
    }
    
    public static void showFileGrid(HttpServletRequest request, HttpServletResponse response) {

        // get agent id
        int folderId = HttpHelper.getIntParameter(request, "id", -1);

        // show results for all rules
        ArrayList grid_columns = new ArrayList();
        
        // Columns
        ArrayList properties = PropertyHelper.getProperties(request);

        for (int x=0; x<properties.size(); x++) {
            Property prop = (Property)properties.get(x);
            grid_columns.add(new GridColumn(prop.getName(), prop.getColumnName(), null, null, prop.getRenderer(), prop.getColumnWidth(), prop.isHideColumn(), prop.isEditable(), prop.isDateField()));
        }

/*        
        grid_columns.add(new GridColumn("objectId", "ID", "objectId", null, null, 30, true));
        grid_columns.add(new GridColumn("objectIcon", "Icon", "objectIcon", null, null, 30, true));
        grid_columns.add(new GridColumn("objectURL", "URL", "objectURL", null, null, 30, true));
        grid_columns.add(new GridColumn("fileName", "Name", "objectName", null, "return '<img src=' + record.data.objectIcon + '> '  + v;", 250, false));
        grid_columns.add(new GridColumn("fileDescription", "Description", "objectDescription", null, null, 450, false));
        grid_columns.add(new GridColumn("created", "Created", "objectCreated", null, null, 160, false));
        grid_columns.add(new GridColumn("modified", "Modified", "objectModified", null, null, 160, false));
*/
        request.setAttribute("grid_columns",grid_columns);
        request.setAttribute("folder_id",folderId);

        try {
            RequestDispatcher dispatcher = request.getRequestDispatcher("/form_templates/dynamic_grid.jsp");
            dispatcher.forward(request, response);
            } 
            catch (Exception ex) {
                LOG.error("showFileGrid failed", ex);
            }
            
        return;    
    }

}
