package com.integryst.kdbrowser.objects;

import com.integryst.kdbrowser.PTHelpers.PTSession;

import org.apache.log4j.Logger;
import org.json.*;
import java.util.*;
import com.plumtree.server.*;
import com.plumtree.openfoundation.util.*;
import com.plumtree.openkernel.config.*;
import com.plumtree.openkernel.factory.*;
import com.integryst.kdbrowser.httphelpers.HttpHelper;

import com.plumtree.portalpages.admin.serversettings.search.SearchServerStatusInfo;
import com.plumtree.remote.prc.*;

import com.plumtree.portaluiinfrastructure.application.varpacks.PTDirPrefsVarPack;
import com.plumtree.uiinfrastructure.application.varpacks.AppConstants;
import com.plumtree.uiinfrastructure.web.ApplicationManager;
import com.plumtree.uiinfrastructure.web.IApplication;

import java.net.URL;
import com.integryst.kdbrowser.helpers.*;

import javax.servlet.http.HttpServletRequest;

// http://forums.oracle.com/forums/thread.jspa?threadID=844377&tstart=0
public class Card {
    private PTSession m_session; // PT session object
    private int folderId;
    private String fileName;
    private String fileDescription;
    private String filePath;
    private String contentType;

    public Card() {
    }

    public Card(PTSession m_session, int folderId, String fileName, String fileDescription, String filePath, String contentType)
    {
        this.m_session = m_session;
        this.folderId = folderId;
        this.fileName = fileName;
        this.fileDescription = fileDescription;
        this.filePath = filePath;
        this.contentType = contentType;
        
    }

    public void setSession(PTSession m_session) { this.m_session = m_session; }
    public PTSession getSession() { return m_session; }

    public void setFolderId(int folderId) { this.folderId = folderId; }
    public int getFolderId() { return folderId; }

    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getFileName() { return fileName; }

    public void setFileDescription(String fileDescription) { this.fileDescription = fileDescription; }
    public String getFileDescription() { return fileDescription; }

    public void setFilePath(String filePath) { this.filePath = filePath; }
    public String getFilePath() { return filePath; }

    public void setContentType(String contentType) { this.contentType = contentType; }
    public String getContentType() { return contentType; }
    
    public String saveJSON(HttpServletRequest request) {
        String response = "";
        try {
            int DATA_SOURCE_ID = PrefsHelper.getIntPreference(request,"portal.ntcrawler.datasourceid");
            int CRAWLER_ID = PrefsHelper.getIntPreference(request,"portal.ntcrawler.crawlerid");
            String CRAWLER_PTDTMSECTIONS = PrefsHelper.getPreference(request,"portal.ntcrawler.ptdtmsections");
            String CRAWLER_TAG = PrefsHelper.getPreference(request,"portal.ntcrawler.crawlertag");
            int CRAWLER_AUTOAPPROVE = PrefsHelper.getIntPreference(request,"portal.ntcrawler.autoapprove");
            int CRAWLER_INDEXONSTORE = PrefsHelper.getIntPreference(request,"portal.ntcrawler.indexonstore");
            int CRAWLER_SUMMARIZEBEFORESTORE = PrefsHelper.getIntPreference(request,"portal.ntcrawler.summarizebeforestore");
            
            IPTSession ptSession = m_session.getSession();

            IPTCatalog cat = ptSession.GetCatalog();
            IPTCard card = cat.CreateCard();
            
            card.SetCrawlerID(CRAWLER_ID);
            card.SetDataSourceID(DATA_SOURCE_ID);

            // set name and description
            if ((null != fileName) && (fileName.length() > 0)) {
                card.SetCardSettings(PT_CARD_SETTINGS.PT_CARD_OVERWRITENAME, 0);
                card.SetName(fileName);
            }

            if ((null != fileDescription) && (fileDescription.length() > 0)) {
                card.SetCardSettings(PT_CARD_SETTINGS.PT_CARD_OVERWRITEDESCRIPTION, 0);
                card.SetDescription(fileDescription);
            }

            // put the card in the specified folder
            card.SetCardSettings(PT_CARD_SETTINGS.PT_CARD_PARENTFOLDERIDS, folderId);

            // approve card
            card.SetCardSettings(PT_CARD_SETTINGS.PT_CARD_INSERTAPPROVED, CRAWLER_AUTOAPPROVE);

            // inherit parent folder ACLs
            card.SetCardSettings(PT_CARD_SETTINGS.PT_CARD_INHERITPARENTFOLDERPERMISSIONS, 1);

            // Set the crawler tag to be "KDUpload"
            card.SetCrawlerTag(CRAWLER_TAG);

            // if the index server isn't up, don't try to index, create the card and inform the user later
            if (CRAWLER_INDEXONSTORE != 0) {
                IXPPropertyBag statusBag = ptSession.GetCatalog().GetSearchServerStatus();
                SearchServerStatusInfo statusInfo = new SearchServerStatusInfo(statusBag, "INDEXSTATUS");
                if (XPStringUtility.EqualsIgnoreCase(statusInfo.connectStatus,"OK")) {
                    card.SetCardSettings(PT_CARD_SETTINGS.PT_CARD_INDEXONSTORE, 1);
                } else {
                    card.SetCardSettings(PT_CARD_SETTINGS.PT_CARD_INDEXONSTORE, 0);
                }
            }
            else
                card.SetCardSettings(PT_CARD_SETTINGS.PT_CARD_INDEXONSTORE, 0);

            // create the Doc Location Property Bag
            IXPPropertyBag propBag = CreateCardSubmitUploadPropertyBag(filePath, CRAWLER_PTDTMSECTIONS, fileName) ;
            card.SetDocumentLocation(propBag);

            // set the document type
            IPTDocumentTypeMap ptMap;
            ptMap = (IPTDocumentTypeMap) ptSession.OpenGlobalObject(PT_GLOBALOBJECTS.PT_GLOBAL_DOCUMENTTYPEMAP, false);
            ptMap.Initialize(ptSession);
            int docTypeID = ptMap.LookupByPropBag(propBag);
            
            // If no default DocType was found, 
            // determine what default DocType should be used based on the server config setting
            if (0 == docTypeID) {
                IApplication application = ApplicationManager.GetInstance().GetApplication(AppConstants.MAIN_APPLICATION_NAME.ToString());
                PTDirPrefsVarPack vpPTDirPrefs = (PTDirPrefsVarPack) application.GetVarPackManager().GetVariablePackage(PTDirPrefsVarPack.VARPACK_ID);
                docTypeID = vpPTDirPrefs.GetDefaultDocType();
            }
            
            // attach document to Data Source (sets the signature)
            // summarize flag is set to false for performance reasons
            IPTDataSource ptDS = (IPTDataSource) ptSession.GetDataSources().Open(DATA_SOURCE_ID, false);
            String strCardPropBagXML = propBag.SaveToXML(0);
            if (CRAWLER_SUMMARIZEBEFORESTORE != 0)
                ptDS.ImportDocument(strCardPropBagXML, docTypeID, card, true, null);
            else
                ptDS.ImportDocument(strCardPropBagXML, docTypeID, card, false, null);

            card.Store();
            card.UnlockObject();

            response = "success";
        }
        catch (Exception ex) {
            response = "Exception: " + ex.getMessage();
        }
        return response;
    }

    private IXPPropertyBag CreateCardSubmitUploadPropertyBag(String _path, String _type, String _docID) {
        IXPPropertyBag pbagCardProp;
    
        pbagCardProp = PortalObjectsFactory.CreatePropertyBag();
    
    
        pbagCardProp.Write("PTC_PBAGFORMAT", 2000);
        pbagCardProp.Write("PTC_DTM_SECT", XPConvert.ToInteger(_type));
        pbagCardProp.Write("PTC_UNIQUE", _path);
        pbagCardProp.Write("PTC_DOC_ID", _docID);
    
        return pbagCardProp;
    }
}
