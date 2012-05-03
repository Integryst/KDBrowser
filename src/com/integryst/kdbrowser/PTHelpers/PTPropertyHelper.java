package com.integryst.kdbrowser.PTHelpers;

import com.integryst.kdbrowser.extjs.*;
import com.integryst.kdbrowser.helpers.*;
import org.json.*;
import com.integryst.kdbrowser.PTHelpers.PTSession;
import com.integryst.kdbrowser.helpers.PrefsHelper;
import com.integryst.kdbrowser.helpers.Property;
import com.integryst.kdbrowser.helpers.PropertyHelper;

import com.plumtree.server.*;

import java.util.ArrayList;


public class PTPropertyHelper {
    public static String saveProps(PTSession session, String json) {
        JSONObject o = null;
        try {
            o = new JSONObject(json);
            JSONArray changedObjects = o.getJSONArray("changedObjects");
            for (int x=0; x<changedObjects.length(); x++) {
                JSONObject currObj = (JSONObject)changedObjects.get(x);
                int objectID = currObj.getInt("id");
                IPTCard card = session.getSession().GetCatalog().OpenCard(objectID, true);
                JSONObject objChanges = currObj.getJSONObject("changes");
                String [] names = JSONObject.getNames(objChanges);
                for (int y=0; y<names.length; y++) {
                    int propID = getPropId(names[y]);
                    String newVal = (String)objChanges.get(names[y]);
                    if (propID > 0) {
                        IPTCardPropertyValue val = card.GetPropertyValues().GetItem(propID);
                        val.SetFieldValue(newVal);
                    }
                    else if (propID==-3)
                        card.SetName(newVal);
                    else if (propID==-4)
                        card.SetDescription(newVal);
                    else if (propID==-7)
                        card.SetDocumentLocation(newVal);
                }
                card.Store();
                card.UnlockObject();
            }
        }
        catch (Exception ex) {
            return jsonHelpers.JSONStatusResponse(false, "Failed Saving Property: " + ex.getMessage());            
        }
        
        return jsonHelpers.JSONStatusResponse(true,"Saved Properties");
    }

    public static int getPropId(String propName) {
        ArrayList properties = PropertyHelper.getProperties(null);
        for (int x=0; x<properties.size();x++) {
            Property tempProp = (Property)properties.get(x);
            if (propName.equals(tempProp.getName()))
                return tempProp.getId();
        }
        return -1;
    }
}
