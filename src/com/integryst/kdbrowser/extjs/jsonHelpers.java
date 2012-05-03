// Copyright (c) 2012 Integryst, LLC, http://www.integryst.com/
// See LICENSE.txt for licensing information

package com.integryst.kdbrowser.extjs;

import java.util.Vector;

import org.apache.log4j.Logger;

import org.json.*;

public class jsonHelpers {

    private static Logger LOG = Logger.getLogger(jsonHelpers.class);

    public static String getJSONString(Vector inArray) {
        return null;
    }

    public static String getJSONWithMetaData(Vector inArray) {
        return null;
        
    }

    public static String JSONStatusResponse(boolean status, String message)
    {
            try
            {
                    JSONObject json = new JSONObject();
                    json.put("success", status);
                    if (message != null)
                            json.put("message", message);
                    return json.toString();
            }
            catch (Exception ex)
            {
                LOG.error("JSONStatusReponse failed: " + message, ex);
            }
            return null;
    }
}
