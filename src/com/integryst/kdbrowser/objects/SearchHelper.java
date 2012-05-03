// Copyright (c) 2012 Integryst, LLC, http://www.integryst.com/
// See LICENSE.txt for licensing information

package com.integryst.kdbrowser.objects;

import com.integryst.kdbrowser.PTHelpers.PTSession;

import com.plumtree.remote.prc.content.folder.IFolder;
import com.plumtree.server.IPTFolder;
import com.plumtree.server.IPTQueryResult;
import com.plumtree.server.IPTSearchQuery;
import com.plumtree.server.IPTSearchRequest;
import com.plumtree.server.IPTSearchResponse;
import com.plumtree.server.PT_CLASSIDS;
import com.plumtree.server.PT_INTRINSICS;
import com.plumtree.server.PT_PROPIDS;

import com.plumtree.server.PT_SEARCH_APPS;
import com.plumtree.server.PT_SEARCH_SETTING;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

public class SearchHelper {

    private int start_index;
    private int limit;
    private String searchText;
    private SEARCHOPTION options;
    private int folderId;
    private PTSession m_session; // PT session object
    private static Logger LOG = Logger.getLogger(SearchHelper.class);

    public enum SEARCHOPTION { ALL, CURRENT_FOLDER_ONLY, CURRENT_FOLDER_AND_SUBS};

    public SearchHelper(PTSession session) {
        m_session = session;
    }

    public SearchHelper(PTSession session, int start_index, int limit, String searchText, SEARCHOPTION options, int folderId) {
        m_session = session;
        this.start_index = start_index;
        this.limit = limit;
        this.searchText = searchText;
        this.options = options;
        this.folderId = folderId;
    }

    private Vector getResults(HttpServletRequest httpRequest) {
        Vector files = new Vector();
        int tempObjectId;
        
        //search for users and groups that match this query
        IPTSearchRequest request = m_session.getSession().GetSearchRequest();

        //turn off requests for collab/content apps
        request.SetSettings(PT_SEARCH_SETTING.PT_SEARCHSETTING_APPS, PT_SEARCH_APPS.PT_SEARCH_APPS_PORTAL);

        // restrict to folders?
        if ((options == SEARCHOPTION.CURRENT_FOLDER_AND_SUBS) || (options == SEARCHOPTION.CURRENT_FOLDER_ONLY))
            request.SetSettings(PT_SEARCH_SETTING.PT_SEARCHSETTING_DDFOLDERS, new int[] {folderId});

        // include subfolders?    
        if (options == SEARCHOPTION.CURRENT_FOLDER_AND_SUBS)
            request.SetSettings(PT_SEARCH_SETTING.PT_SEARCHSETTING_INCLUDE_SUBFOLDERS, true);
        else
            request.SetSettings(PT_SEARCH_SETTING.PT_SEARCHSETTING_INCLUDE_SUBFOLDERS, false);
        
        // Start index
        request.SetSettings(PT_SEARCH_SETTING.PT_SEARCHSETTING_SKIPRESULTS, start_index);
        
        // limit results
        request.SetSettings(PT_SEARCH_SETTING.PT_SEARCHSETTING_MAXRESULTS, limit);

        // Restrict the search to documents
        request.SetSettings(PT_SEARCH_SETTING.PT_SEARCHSETTING_OBJTYPES, new int[] {PT_CLASSIDS.PT_CATALOGCARD_ID});

        // make sure the appropriate fields are returned.
        request.SetSettings(PT_SEARCH_SETTING.PT_SEARCHSETTING_RET_PROPS, new int[] {PT_INTRINSICS.PT_PROPERTY_OBJECTID });

        // sort by the name
        request.SetSettings(PT_SEARCH_SETTING.PT_SEARCHSETTING_ORDERBY, PT_INTRINSICS.PT_PROPERTY_OBJECTNAME);
        IPTSearchQuery query = request.CreateBasicQuery(searchText, null);

        IPTSearchResponse res = request.Search(query);        
        
        for (int i = 0; i < res.GetResultsReturned(); i++)
        {
            int matches = res.GetTotalMatches();
            int skipped = res.GetSkipped();
            tempObjectId = res.GetFieldsAsInt(i, PT_INTRINSICS.PT_PROPERTY_OBJECTID);
            File tempFile= new File(m_session, tempObjectId);
            tempFile.loadProperties(httpRequest);
            files.add(tempFile);
        }
        return files;
    }

    public String getSearchResultsJSON(HttpServletRequest request) {
        Vector results = getResults(request);
        JSONObject json = new JSONObject();
        try {
            json.put("folder_id", folderId);
            json.put("count", results.size());
            json.put("items", new JSONArray(results));
            LOG.debug("Returning Search Results for [searchText:" + searchText + ", folderId: " + folderId + ", start: " + start_index + ", limit: " + limit + ", options: " + options + "]  results: " + results.size() + " objects found");
        }
        catch (Exception ex) {
            LOG.error("Exception generating search JSON: ", ex);
        }
        return json.toString();
    }


    public void setStart_index(int start_index) { this.start_index = start_index; }
    public void setLimit(int limit) { this.limit = limit; }
    public void setSearchText(String searchText) { this.searchText = searchText; }
    public void setOptions(SEARCHOPTION options) { this.options = options; }
    public void setFolderId(int folderId) { this.folderId = folderId; }
}
