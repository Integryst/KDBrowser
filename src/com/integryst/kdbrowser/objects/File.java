package com.integryst.kdbrowser.objects;

import com.bea.alui.proxy.xp.IXPEnvironment;

import com.integryst.kdbrowser.PTHelpers.PTSession;
import com.integryst.kdbrowser.extjs.jsonHelpers;
import com.integryst.kdbrowser.helpers.PrefsHelper;
import com.integryst.kdbrowser.helpers.Property;
import com.integryst.kdbrowser.helpers.PropertyHelper;

import com.plumtree.openfoundation.util.IXPEnumerator;
import com.plumtree.server.*;

import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.json.JSONString;

public class File implements JSONString{
    public static int CARD_CLASSID = 18; // classID for a card

//    private IPTFolder this_folder; // PT Object representing this folder
    private PTSession m_session; // PT session object
    
    private int objectId;
    private String objectName;
    private String objectDescription;
    private String objectIcon;
    private String objectURL;
    private Date objectCreated;
    private Date objectModified;
    private IPTCardPropertyValues props;

    public File(PTSession session, int objectId) {
        this.m_session = session;
        this.objectId = objectId;
    }
    
    public void loadProperties(HttpServletRequest request) {
        String imgBase = PrefsHelper.getPreference(request, "portlet.url.imgBase");
        
        IPTCard card = m_session.getSession().GetCatalog().OpenCard(objectId, false);
        objectName = card.GetName();
        objectDescription = card.GetDescription();
        objectURL = card.GetDocumentURL();
        try {
            // remove the host name so the URL works with any experience definition
            int thirdSlash = objectURL.indexOf('/',8); // skip "https://"
            objectURL = objectURL.substring(thirdSlash);
        }
        catch (Exception ex) {
            // skip it
        }
        objectCreated = card.GetCreated().GetUnderlyingObject();
        objectModified = card.GetLastModified().GetUnderlyingObject();
        objectIcon = imgBase + "plumtree/portal/public/img/sml" + card.GetImageUUID() + ".gif";
        props = card.GetPropertyValues();
    }

    public int getObjectId() { return objectId; }
    public String getObjectName() { return objectName; }
    public String getObjectDescription() { return objectDescription; }
    public String getObjectIcon() { return objectIcon; }
    public String getObjectURL() { return objectURL; }
    public Date getObjectCreated() { return objectCreated; }
    public Date getObjectModified() { return objectModified; }
    
    public String toJSONString() { 
        JSONObject tempObject = new JSONObject();
        try {
            // put in the 6 default properties
            tempObject.put("objectid", objectId);
            tempObject.put("iconurl", objectIcon);
            tempObject.put("name", objectName);
            tempObject.put("description", objectDescription);
            tempObject.put("created", objectCreated);
            tempObject.put("modified", objectModified);
            tempObject.put("objecturl", objectURL);

            ArrayList customProps = PropertyHelper.getProperties(null);
            IXPEnumerator allProps = props.GetEnumerator();
            IPTCardPropertyValue val;
            String unused = "";
            boolean more = allProps.MoveNext();
            boolean found;
            while (more) {
                val = (IPTCardPropertyValue)allProps.GetCurrent();

                found = false;                
                for (int x=0; x<customProps.size(); x++) {
                    Property tempProp = (Property)customProps.get(x);
                    try {
                        if (tempProp.getName().equals(val.GetFieldValueAsString())) {
                            val = props.GetItem(tempProp.getId());
                            tempObject.put(tempProp.getName(), val.GetValueAsString());
                            found=true;
                            break;
                        }
                    }
                    catch(Exception ex) {

                    }
                }
                if (!found)
                    unused += "[" + val.GetPropertyID() + "] ";
                
                more = allProps.MoveNext();
            }
            tempObject.put("unused", unused);
            /*
            ArrayList customProps = PropertyHelper.getProperties(null);
            for (int x=0; x<customProps.size(); x++) {
                Property tempProp = (Property)customProps.get(x);
                IPTCardPropertyValue val;
                try {
                    val = props.GetItem(tempProp.getId());
                    tempObject.put(tempProp.getName(), val.GetValueAsString());
                }
                catch(Exception ex) {
                    // card doesn't have property, or is a different type than string
                }
            }
            */
        }
        catch (Exception ex) {
            // TODO: add log
            System.out.println("Exception enumerating properties: " + ex.getMessage());
        }
        return tempObject.toString();  
    }
    public String delete() {
        try {
            m_session.getSession().GetCatalog().DeleteCards(objectId);
        }
        catch (Exception ex) {
            return jsonHelpers.JSONStatusResponse(false, "Failed deleting card: " + ex.getMessage());
        }
        return jsonHelpers.JSONStatusResponse(true, "Card Deleted");
    }
}