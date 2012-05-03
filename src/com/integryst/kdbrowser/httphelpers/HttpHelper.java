// Copyright (c) 2012 Integryst, LLC, http://www.integryst.com/
// See LICENSE.txt for licensing information

package com.integryst.kdbrowser.httphelpers;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

public class HttpHelper {
    private static Logger LOG = Logger.getLogger(HttpHelper.class);

    public static String getParameter(HttpServletRequest request, String param_name) {
        return getParameter(request, param_name, null);
    }
    
    public static String getParameter(HttpServletRequest request, String param_name, String default_value) {
        String value = request.getParameter(param_name);
        if ((value != null) && (!"".equals(value)))
            return value;
        
        return default_value;    
    }

    public static int getIntParameter(HttpServletRequest request, String param_name) {
        return getIntParameter(request, param_name, -1);
    }
    
    public static int getIntParameter(HttpServletRequest request, String param_name, int default_value) {
        String sValue = request.getParameter(param_name);
        if ((sValue != null) && (!"".equals(sValue)))
        {
            try {
                return Integer.parseInt(sValue);                
            }
            catch (Exception ex) {
                LOG.warn("Failed getting int parameter for " + param_name + ": " + ex.getMessage());
                return default_value;
            }
        }
        
        return default_value;    
    }

}
