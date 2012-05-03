package com.integryst.kdbrowser.objects;

import org.apache.log4j.Logger;
import org.json.*;
import java.util.*;
import com.plumtree.server.*;
import com.plumtree.openfoundation.util.*;
import com.plumtree.openkernel.config.*;
import com.plumtree.openkernel.factory.*;

import com.integryst.kdbrowser.PTHelpers.*;

import javax.servlet.http.HttpServletRequest;

public class Folder implements JSONString {
    private String folderName;
    private String folderDescription;
    private int folderId;
    private int fileCount;
    private boolean leaf=false;
    private static Logger LOG = Logger.getLogger(Folder.class);
    private boolean folderLinked = false;

    public static int FOLDER_CLASSID = 17; // classID for a document folder

    private IPTFolder this_folder; // PT Object representing this folder
    private PTSession m_session; // PT session object
    private Vector subFolders;
    private Vector files;

    public Folder(PTSession session, int folderId) {
        init(session, folderId, null, null);
    }

    public Folder(PTSession session, int folderId, String folderName, String folderDescription) {
        init(session, folderId, folderName, folderDescription);
    }

    public void init(PTSession session, int folderId, String folderName, String folderDescription) {
        this.m_session = session;
        this.folderId = folderId;
        this.folderName = folderName;
        this.folderDescription = folderDescription;
        try {
            // get folder object
            this_folder = m_session.getSession().GetCatalog().OpenFolder(folderId, false);
            
            // see if there are subfolders
            IPTQueryResult res = this_folder.QuerySubfolders(PT_PROPIDS.PT_PROPID_OBJECTID, 0, PT_PROPIDS.PT_PROPID_NAME, 0, 5, null);
            if (res.RowCount() >0 )
                leaf = false;
            else
                leaf = true;
            folderLinked = true;
            
            // get num cards in folder
            res = this_folder.QueryCards(PT_PROPIDS.PT_PROPID_OBJECTID, false, PT_PROPIDS.PT_PROPID_NAME, 0,-1, null);
            fileCount = res.RowCount();
        }
        catch (Exception ex) {
            folderLinked = false;
        }
    }

    public Vector getSubFolders() { return getSubFolders(false); }
    public Vector getSubFolders(boolean forceReload) {
        if ((subFolders != null) && (!forceReload))
            return subFolders;
        
        int tempFolderId;
        String tempFolderName;
        String tempFolderDescription;
        
        subFolders = new Vector();
        IPTQueryResult res = this_folder.QuerySubfolders(PT_PROPIDS.PT_PROPID_OBJECTID + PT_PROPIDS.PT_PROPID_NAME + PT_PROPIDS.PT_PROPID_DESCRIPTION, 0, PT_PROPIDS.PT_PROPID_NAME, 0, 100, null);
        for (int i = 0; i < res.RowCount(); i++)
        {
            tempFolderId = res.ItemAsInt(i, PT_PROPIDS.PT_PROPID_OBJECTID);
            tempFolderName = res.ItemAsString(i, PT_PROPIDS.PT_PROPID_NAME);
            tempFolderDescription = res.ItemAsString(i, PT_PROPIDS.PT_PROPID_DESCRIPTION);
            Folder tempFolder = new Folder(m_session, tempFolderId, tempFolderName, tempFolderDescription);
            subFolders.add(tempFolder);
        }
        
        return subFolders;
    }

    public String getSubFoldersJSON() { return getSubFoldersJSON(false); }
    public String getSubFoldersJSON(boolean forceReload) {
        getSubFolders(forceReload);
        return (new JSONArray(subFolders)).toString();
    }

    public Vector getFiles(HttpServletRequest request, int start, int limit) { return getFiles(request, start, limit, false); }
    public Vector getFiles(HttpServletRequest request, int start, int limit, boolean forceReload) {
        if ((files != null) && (!forceReload))
            return files;

        files = new Vector();
        int tempObjectId;
        String tempObjectName;
        IPTQueryResult res = this_folder.QueryCards(PT_PROPIDS.PT_PROPID_OBJECTID + PT_PROPIDS.PT_PROPID_NAME, false, PT_PROPIDS.PT_PROPID_NAME, start, limit, null);

        for (int i = 0; i < res.RowCount(); i++)
        {
            tempObjectId = res.ItemAsInt(i, PT_PROPIDS.PT_PROPID_OBJECTID);
            tempObjectName = res.ItemAsString(i, PT_PROPIDS.PT_PROPID_NAME);
            File tempFile= new File(m_session, tempObjectId);
            tempFile.loadProperties(request);
            files.add(tempFile);
        }
        return files;
    }

    public String getFilesJSON(HttpServletRequest request, int start, int limit) { return getFilesJSON(request, start, limit, false); }
    public String getFilesJSON(HttpServletRequest request, int start, int limit, boolean forceReload) {
        getFiles(request, start, limit, forceReload);
        JSONObject json = new JSONObject();
        try {
            json.put("folder_id", folderId);
            json.put("count", fileCount);
            json.put("items", new JSONArray(files));
            LOG.debug("Returning Files list for folder " + folderName + ": " + files.size() + " files found");
        }
        catch (Exception ex) {
            LOG.error("Exception generating files JSON: ", ex);
        }
        return json.toString();
    }

    public String createChildFolder(String name) {
        try {
            IPTFolder new_folder = m_session.getSession().GetCatalog().CreateFolder(this_folder.GetObjectID());
            new_folder.SetName(name);
            new_folder.Store();
            new_folder.UnlockObject();
            return "{success:true, message:'Folder " + new_folder.GetName() + " created', folderId:" + new_folder.GetObjectID() + ",folderName:'" + new_folder.GetName() + "'}";
        }
        catch (Exception ex) {
            return "{success:false, message:'Folder Creation Failed: " + ex.getMessage() + "'}";
        }
    }

    public String toJSONString() { 
        JSONObject json = new JSONObject();
        try {
            json.put("id", folderId);
            json.put("text", folderName);
            if (folderDescription != null)
                json.put("qtip", folderDescription );
            json.put("leaf", leaf);
            if (leaf)
                json.put("iconCls","folder");
        }
        catch (Exception ex) {
            LOG.error("Exception generating files JSON: ", ex);
        }
        return json.toString();
    }

    public void setFolderName(String folderName) { this.folderName = folderName; }
    public void setFolderId(int folderId) { this.folderId = folderId; }

    public int getId() { return folderId; }
    public String getText() { return folderName; }
    
}
