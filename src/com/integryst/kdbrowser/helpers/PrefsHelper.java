// Copyright (c) 2012 Integryst, LLC, http://www.integryst.com/
// See LICENSE.txt for licensing information

package com.integryst.kdbrowser.helpers;

import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.plumtree.remote.portlet.*;

import java.util.ArrayList;

public class PrefsHelper {
    private static String BUNDLE_NAME = "kdbrowser";
    

    public static boolean getBoolPreference(HttpServletRequest request, String prefName) {
        String value=getPreference(request, prefName);
        if (value == null)
            return false;
        try {
            if ("1".equals(value))
                return true;
            return Boolean.parseBoolean(value);
        }
        catch (Exception ex) {
            return false;
        }
    }

    public static int getIntPreference(HttpServletRequest request, String prefName) {
        String value=getPreference(request, prefName);
        if (value == null)
            return -1;
        try {
            return Integer.parseInt(value);
        }
        catch (Exception ex) {
            return -1;
        }
    }

    public static String getPreference(HttpServletRequest request, String prefName) {
        String value=null;

        try {
            //get the idk
            IPortletContext portletContext = PortletContextFactory.createPortletContext(request, null);
            IPortletRequest portletRequest = portletContext.getRequest();    
            
            value = portletRequest.getSettingValue(SettingType.Community, fixPrefName(prefName));
        }
        catch (Exception ex) {
            // not gatewayed; try ResourceBundle    
        }

        // get default from resource bundle
        if ((value==null) || (value.length() == 0))
        try {
            ResourceBundle props = ResourceBundle.getBundle(BUNDLE_NAME);
            value = props.getString(prefName);
        }
        catch (Exception ex) {
            // can't find the setting; contin
        }
        
        return value;
    }
    
    public static boolean setPreference(HttpServletRequest request, HttpServletResponse response, String prefName, String prefValue) {
        prefName = fixPrefName(prefName);
        try {
            //get the idk
            IPortletContext portletContext = PortletContextFactory.createPortletContext(request, response);
            IPortletResponse portletResponse = portletContext.getResponse();
                        
            portletResponse.setSettingValue(SettingType.Community, prefName, prefValue);
        }
        catch (Exception ex) {
            return false;
        }
        
        return true;
    }
    
    // periods aren't allowed in alui prefs
    private static String fixPrefName(String prefName) {
        return prefName.replaceAll("\\.","");
    }
    
    public static boolean saveAllPrefs(HttpServletRequest request, HttpServletResponse response) {
        setPreference(request, response, "portlet.paginationsize", request.getParameter("PAGINATION_SIZE"));
        setPreference(request, response, "portlet.adminGroupID", request.getParameter("ADMIN_GROUP_ID"));
        
        setPreference(request, response, "portlet.baseKDFolder", request.getParameter("BASE_KD_FOLDER_ID"));
        setPreference(request, response, "portlet.baseKDFolderName", request.getParameter("BASE_KD_FOLDER_NAME"));
        setPreference(request, response, "portlet.baseFilePath", request.getParameter("BASE_FILE_PATH"));
        setPreference(request, response, "portlet.baseUNCPath", request.getParameter("BASE_UNC_PATH"));

        setPreference(request, response, "portal.ntcrawler.datasourceid", request.getParameter("DATA_SOURCE_ID"));
        setPreference(request, response, "portal.ntcrawler.crawlerid", request.getParameter("CRAWLER_ID"));
        setPreference(request, response, "portal.ntcrawler.crawlertag", request.getParameter("CRAWLER_TAG"));
        setPreference(request, response, "portal.ntcrawler.autoapprove", request.getParameter("AUTOAPPROVE"));
        setPreference(request, response, "portal.ntcrawler.indexonstore", request.getParameter("INDEX_ON_STORE"));
        setPreference(request, response, "portal.ntcrawler.summarizebeforestore", request.getParameter("SUMMARIZE_BEFORE_STORE"));
        setPreference(request, response, "portal.ntcrawler.ptdtmsections", request.getParameter("PTDM_SECTIONS"));
        
        return true;
    }
    
    public static ArrayList getIntPreferenceArray(HttpServletRequest request, String prefName) {
        ArrayList ret = new ArrayList();
        String pref = getPreference(request, prefName);
        String[] items = pref.split(",");
        for (int x=0; x<items.length; x++) {
            int temp = Integer.parseInt(items[x]);
            ret.add(temp);
        }
        return ret;
    }
}