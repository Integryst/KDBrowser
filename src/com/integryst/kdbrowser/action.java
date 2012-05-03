// Copyright (c) 2012 Integryst, LLC, http://www.integryst.com/
// See LICENSE.txt for licensing information

package com.integryst.kdbrowser;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.*;
import javax.servlet.http.*;

import com.integryst.kdbrowser.httphelpers.*;
import com.integryst.kdbrowser.objects.*;
import com.integryst.kdbrowser.PTHelpers.*;
import com.integryst.kdbrowser.helpers.*;

import com.plumtree.remote.portlet.IPortletContext;
import com.plumtree.remote.portlet.IPortletRequest;
import com.plumtree.remote.portlet.PortletContextFactory;
import com.plumtree.remote.prc.RemoteSessionFactory;

public class action extends HttpServlet {
    private static final String CONTENT_TYPE = "text/html; charset=UTF-8";

    private static final String ACTION_PORTLET = "portlet";
    private static final String ACTION_GETFOLDERS = "getfolders";
    private static final String ACTION_GETFILES = "getfiles";
    private static final String ACTION_LOADPANEL = "loadpanel";
    private static final String ACTION_UPLOADDIALOG = "uploaddialog";
    private static final String ACTION_UPLOAD = "upload";
    private static final String ACTION_CREATE_CARD = "createcard";
    private static final String ACTION_CREATE_FOLDER = "createfolder";
    private static final String ACTION_OPEN_PREFS = "openprefs";
    private static final String ACTION_SAVE_PREFS = "saveprefs";
    private static final String ACTION_SAVE_PROPS = "saveproperties";
    private static final String ACTION_DELETE_FILE = "deletefile";
    private static final String ACTION_SEARCH = "search";
    private static final String ACTION_UPLOAD_POPUP = "uploadpopup";
            
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException,
                                                           IOException {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException,
                                                            IOException {
        response.setContentType(CONTENT_TYPE);
        String action = HttpHelper.getParameter(request, "action", ACTION_PORTLET);
        String responseText = "";
        
        if (ACTION_PORTLET.equals(action)) {
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/portlet.jsp");
            dispatcher.forward(request, response);
            return;
        }
        else if (ACTION_OPEN_PREFS.equals(action)) {
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/prefs.jsp");
            dispatcher.forward(request, response);
            return;
        } 
        else if (ACTION_SAVE_PROPS.equals(action)) {
            String json = request.getParameter("json");
            PTSession session = new PTSession(request, response);
            responseText = PTPropertyHelper.saveProps(session, json);
        }
        else if (ACTION_SAVE_PREFS.equals(action)) {
            PrefsHelper.saveAllPrefs(request, response);
            // just close the window
            responseText = "<script>window.close();\nif (window.opener && !window.opener.closed)\n{window.opener.location.reload();\n} </script>";
//            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/prefs.jsp");
//            request.setAttribute("status","Preferences Saved");
//            dispatcher.forward(request, response);
//            return;
        } 
        else if (ACTION_UPLOADDIALOG.equals(action)) {
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/form_templates/upload_form.jsp");
            dispatcher.forward(request, response);
            return;
        } 
        else if (ACTION_UPLOAD_POPUP.equals(action)) {
            try {
                String collabProjectID = HttpHelper.getParameter(request, "collabProjectID", "12");
                String collabFolderID = HttpHelper.getParameter(request, "collabFolderID", "17");
                
                RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/form_templates/upload_form.jsp?collabProjectID=" + collabProjectID + "&collabFolderID=" + collabFolderID);
                dispatcher.forward(request, response);
                return;
            }
            catch (Exception ex) {
                responseText = "An error occurred processing your request: " + ex.getMessage();                
            }
        }
        else if (ACTION_UPLOAD.equals(action)) {
            responseText = "{result:'success', size:'size of file'}";
        }
        else if (ACTION_CREATE_CARD.equals(action)) {
            int folderId = HttpHelper.getIntParameter(request, "folderId", -1);
            String fileName = HttpHelper.getParameter(request, "fileName", null);
            String fileDescription = HttpHelper.getParameter(request, "fileDescription", null);
            String filePath = HttpHelper.getParameter(request, "filePath", null);
            String contentType = request.getContentType();
            
            PTSession session = new PTSession(request, response);
            
            Card newCard = new Card(session, folderId, fileName, fileDescription, filePath, contentType);
            responseText = newCard.saveJSON(request);
        }
        else if (ACTION_CREATE_FOLDER.equals(action)) {
            int folderId = HttpHelper.getIntParameter(request, "rootID", -1);
            String folderName = HttpHelper.getParameter(request, "name", null);
            
            PTSession session = new PTSession(request, response);
            
            Folder folder = new Folder(session, folderId);
            responseText = folder.createChildFolder(folderName);
        }
        else if (ACTION_GETFOLDERS.equals(action)) {
            int folderId = HttpHelper.getIntParameter(request, "node", 1);
            PTSession session = new PTSession(request, response);
            Folder folder = new Folder(session, folderId);
            responseText = folder.getSubFoldersJSON();
        }
        else if (ACTION_GETFILES.equals(action)) {
            int folderId = HttpHelper.getIntParameter(request, "folder_id", 1);
            int start = HttpHelper.getIntParameter(request, "start", 0);
            int limit = HttpHelper.getIntParameter(request, "limit", PrefsHelper.getIntPreference(request, "portlet.paginationsize"));
            PTSession session = new PTSession(request, response);
            Folder folder = new Folder(session, folderId);
            responseText = folder.getFilesJSON(request, start, limit, true);
        }
        else if (ACTION_SEARCH.equals(action)) {
            int folderId = HttpHelper.getIntParameter(request, "folder_id", -1);
            int start = HttpHelper.getIntParameter(request, "start", -1);
            int limit = HttpHelper.getIntParameter(request, "limit", -1);
            String searchText = HttpHelper.getParameter(request, "searchtext", "*");
            String sOptions = HttpHelper.getParameter(request, "options", "search_all");

            SearchHelper.SEARCHOPTION options = SearchHelper.SEARCHOPTION.ALL;
            if ("search_current_only".equals(sOptions))
                options = SearchHelper.SEARCHOPTION.CURRENT_FOLDER_ONLY;
            else if ("search_current_and_subs".equals(sOptions))
                options = SearchHelper.SEARCHOPTION.CURRENT_FOLDER_AND_SUBS;
            
            PTSession session = new PTSession(request, response);
            SearchHelper searchHelper = new SearchHelper(session, start, limit, searchText, options, folderId);
            
            responseText = searchHelper.getSearchResultsJSON(request);
        }
        else if (ACTION_DELETE_FILE.equals(action)) {
            int cardId = HttpHelper.getIntParameter(request, "cardid", 1);
            PTSession session = new PTSession(request, response);
            Folder folder = new Folder(session, cardId);
            File f = new File(session, cardId);
            responseText = f.delete();
        }
        else if (ACTION_LOADPANEL.equals(action)) {

            // get nodeType
            String tabType = HttpHelper.getParameter(request, "tabType");
            int treeNodeId = HttpHelper.getIntParameter(request, "id");
    
            FileGrid.showFileGrid(request, response);
            return;
        }
        
        PrintWriter out = response.getWriter();
        out.print(responseText);
    }
}
