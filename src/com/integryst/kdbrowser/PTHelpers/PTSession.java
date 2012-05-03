package com.integryst.kdbrowser.PTHelpers;

import com.integryst.kdbrowser.helpers.PrefsHelper;

import com.plumtree.server.*;
import com.plumtree.openfoundation.util.*;
import com.plumtree.openkernel.config.*;
import com.plumtree.openkernel.factory.*;

import com.plumtree.remote.portlet.IPortletContext;

import com.plumtree.remote.portlet.IPortletRequest;
import com.plumtree.remote.portlet.PortletContextFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class PTSession {
    private IPTSession cachedSession = null;

    public PTSession(HttpServletRequest request, HttpServletResponse response) {
        HttpSession httpSession = request.getSession(true);
        cachedSession = (IPTSession)httpSession.getAttribute("cachedsession");
        
        if (cachedSession != null) {
            try {
                // session will probably expire after 5 minutes, so validate it here
                cachedSession.GetSessionInfo();
            }
            catch (Exception ex) {
                cachedSession = null;
            }
        }
        
        if (cachedSession == null) {
            cachedSession = getSession(request, response);
            httpSession.setAttribute("cachedsession", cachedSession);
        }
    }

    public IPTSession getSession() {
        return cachedSession;
    }
    
    public IPTSession getSession(HttpServletRequest request, HttpServletResponse response)
    {
        if (cachedSession == null) {
            IPortletContext portletContext = PortletContextFactory.createPortletContext(request, response);
            IPortletRequest portletRequest = portletContext.getRequest();    
            String loginToken = null;
            try {
                loginToken = portletRequest.getLoginToken();
            }
            catch (Exception ex) { // not gatewayed; use properties for user/pass 
            }
            cachedSession = newSessionLogin(PrefsHelper.getPreference(request, "portal.api.settings"), PrefsHelper.getPreference(request, "portal.api.user"), PrefsHelper.getPreference(request,"portal.api.pass"), loginToken);
        }
        return cachedSession;
    }
    
    private IPTSession newSessionLogin(String configPath, String userName, String password, String loginToken)
    {
        // initialize the API
        IPTSession ptSession = null;
        IOKContext okContext = OKConfigFactory.createInstance(configPath, "portal");
        PortalObjectsFactory.Init(okContext);
        ptSession = PortalObjectsFactory.CreateSession();

        if (loginToken == null)
            ptSession.Connect(userName, password, null);
        else
            ptSession.Reconnect(loginToken);
        
        return ptSession;
    }
    
    
}
