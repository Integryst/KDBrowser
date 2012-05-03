// Copyright (c) 2012 Integryst, LLC, http://www.integryst.com/
// See LICENSE.txt for licensing information

package com.integryst.kdbrowser.helpers;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

public class PropertyHelper {
    
    public static ArrayList getProperties(HttpServletRequest request) {
        ArrayList propertyList = new ArrayList();
        ArrayList propertyIDs = PrefsHelper.getIntPreferenceArray(request,"portlet.properties");
        for (int x=0; x<propertyIDs.size(); x++) {
            int tempPropertyID = (Integer)propertyIDs.get(x);
            Property tempProperty = new Property(tempPropertyID);
            tempProperty.setName(PrefsHelper.getPreference(request, "portlet.properties." + tempPropertyID + ".name"));
            tempProperty.setHideColumn(PrefsHelper.getBoolPreference(request, "portlet.properties." + tempPropertyID + ".hidecolumn"));
            tempProperty.setEditable(PrefsHelper.getBoolPreference(request, "portlet.properties." + tempPropertyID + ".iseditable"));
            tempProperty.setColumnName(PrefsHelper.getPreference(request, "portlet.properties." + tempPropertyID + ".columnname"));
            tempProperty.setColumnWidth(PrefsHelper.getIntPreference(request, "portlet.properties." + tempPropertyID + ".columnwidth"));
            tempProperty.setRenderer(PrefsHelper.getPreference(request, "portlet.properties." + tempPropertyID + ".renderer"));
            tempProperty.setDateField(PrefsHelper.getBoolPreference(request, "portlet.properties." + tempPropertyID + ".isdate"));
            propertyList.add(tempProperty);
        }
        return propertyList;
    }
}
