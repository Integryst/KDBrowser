<%@ page import="com.integryst.kdbrowser.*, com.integryst.kdbrowser.helpers.*, java.util.*" %>
<%@ page contentType="text/html;charset=UTF-8"%>
<html>
  <head>
    <title>KD Browser Preferences</title>
  </head>
  <body>
<%
    // get preferences
    String EXT_BASE_PATH = PrefsHelper.getPreference(request, "portlet.url.ext");
    String CSS_BASE_PATH = PrefsHelper.getPreference(request, "portlet.url.css");
    String JS_BASE_PATH = PrefsHelper.getPreference(request, "portlet.url.js");
    String SERVLET_URL = PrefsHelper.getPreference(request, "portlet.url.servlet");
    String IMG_BASE_PATH = PrefsHelper.getPreference(request, "portlet.url.imgBase");
    String UPLOAD_TARGET_URL = PrefsHelper.getPreference(request, "portlet.url.uploadTargetURL");

    String ADMIN_GROUP_ID = PrefsHelper.getPreference(request, "portlet.adminGroupID");
    String BASE_KD_FOLDER_ID = PrefsHelper.getPreference(request, "portlet.baseKDFolder");
    String BASE_KD_FOLDER_NAME = PrefsHelper.getPreference(request, "portlet.baseKDFolderName");
    String BASE_FILE_PATH = PrefsHelper.getPreference(request, "portlet.baseFilePath");
    String BASE_UNC_PATH = PrefsHelper.getPreference(request, "portlet.baseUNCPath");
    String PAGINATION_SIZE = PrefsHelper.getPreference(request, "portlet.paginationsize");

    String DATA_SOURCE_ID = PrefsHelper.getPreference(request, "portal.ntcrawler.datasourceid");
    String CRAWLER_ID = PrefsHelper.getPreference(request, "portal.ntcrawler.crawlerid");
    String CRAWLER_TAG = PrefsHelper.getPreference(request, "portal.ntcrawler.crawlertag");
    String AUTOAPPROVE = PrefsHelper.getPreference(request, "portal.ntcrawler.autoapprove");
    String INDEX_ON_STORE = PrefsHelper.getPreference(request, "portal.ntcrawler.indexonstore");
    String SUMMARIZE_BEFORE_STORE = PrefsHelper.getPreference(request, "portal.ntcrawler.summarizebeforestore");
    String PTDM_SECTIONS = PrefsHelper.getPreference(request, "portal.ntcrawler.ptdtmsections");

    String status = (String)request.getAttribute("status"); 

%>
    <%=(status==null)?"":status%><BR/>
    
    <form action="<%=SERVLET_URL%>" method="post">
        <table border=1>
            <tr><th>Name</th><th>Value</th></tr>
            <tr><td colspan=2>KD / Upload Properties</td></tr>
            <tr><td>Num files to display</td><td><input size=6 type=text name="PAGINATION_SIZE" value="<%=PAGINATION_SIZE%>"></td></tr>
            <tr><td>Admin Group ID</td><td><input size=60 type=text name="ADMIN_GROUP_ID" value="<%=ADMIN_GROUP_ID%>"></td></tr>
            <tr><td>Base KD Folder ID</td><td><input size=60 type=text name="BASE_KD_FOLDER_ID" value="<%=BASE_KD_FOLDER_ID%>"></td></tr>
            <tr><td>Base KD Folder Name</td><td><input size=60 type=text name="BASE_KD_FOLDER_NAME" value="<%=BASE_KD_FOLDER_NAME%>"></td></tr>
            <tr><td>Base File Path</td><td><input size=60 type=text name="BASE_FILE_PATH" value="<%=BASE_FILE_PATH%>"></td></tr>
            <tr><td>Base UNC Path</td><td><input size=60 type=text name="BASE_UNC_PATH" value="<%=BASE_UNC_PATH%>"></td></tr>
            <tr><td colspan=2>Crawler Properties</td></tr>
            <tr><td>Data Source ID</td><td><input size=60 type=text name="DATA_SOURCE_ID" value="<%=DATA_SOURCE_ID%>"></td></tr>
            <tr><td>Crawler ID</td><td><input size=60 type=text name="CRAWLER_ID" value="<%=CRAWLER_ID%>"></td></tr>
            <tr><td>Crawler Tag</td><td><input size=60 type=text name="CRAWLER_TAG" value="<%=CRAWLER_TAG%>"></td></tr>
            <tr><td>Auto-approve</td><td><input size=60 type=text name="AUTOAPPROVE" value="<%=AUTOAPPROVE%>"></td></tr>
            <tr><td>Index on store</td><td><input size=60 type=text name="INDEX_ON_STORE" value="<%=INDEX_ON_STORE%>"></td></tr>
            <tr><td>Summarize before store</td><td><input size=60 type=text name="SUMMARIZE_BEFORE_STORE" value="<%=SUMMARIZE_BEFORE_STORE%>"></td></tr>
            <tr><td>PTDM Sections</td><td><input size=60 type=text name="PTDM_SECTIONS" value="<%=PTDM_SECTIONS%>"></td></tr>
            <tr><td colspan=2>Configured in properties file</td></tr>
            <tr><td>Servlet URL</td><td><%=SERVLET_URL%></td></tr>
            <tr><td>EXT Base Path</td><td><%=EXT_BASE_PATH%></td></tr>
            <tr><td>CSS Base Path</td><td><%=CSS_BASE_PATH%></td></tr>
            <tr><td>JS Base Path</td><td><%=JS_BASE_PATH%></td></tr>
            <tr><td>Image Base Path</td><td><%=IMG_BASE_PATH%></td></tr>
            <tr><td>Upload Target URL</td><td><%=UPLOAD_TARGET_URL%></td></tr>
            <tr><td colspan=2><input type=hidden name="action" value="saveprefs"><input type="submit" value="Save"> <input type="button" onClick="window.close(); return false;" value="Close"></td></tr>
        </table>
    </form>
  </body>
</html>