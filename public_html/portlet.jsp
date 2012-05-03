<%@ page import="com.integryst.kdbrowser.*, com.integryst.kdbrowser.helpers.*, java.util.*" %>
<%
    // get preferences
    String EXT_BASE_PATH = PrefsHelper.getPreference(request, "portlet.url.ext");
    String CSS_BASE_PATH = PrefsHelper.getPreference(request, "portlet.url.css");
    String JS_BASE_PATH = PrefsHelper.getPreference(request, "portlet.url.js");
    String SERVLET_URL = PrefsHelper.getPreference(request, "portlet.url.servlet");
    String BASE_KD_FOLDER_ID = PrefsHelper.getPreference(request, "portlet.baseKDFolder");
    String BASE_KD_FOLDER_NAME = PrefsHelper.getPreference(request, "portlet.baseKDFolderName");
%>

<!-- LIBS -->
<link rel="stylesheet" type="text/css" href="<%=EXT_BASE_PATH%>resources/css/ext-all.css" />
<script type="text/javascript" src="<%=EXT_BASE_PATH%>adapter/ext/ext-base.js"></script>
<script type="text/javascript" src="<%=EXT_BASE_PATH%>ext-all-debug.js"></script>
<!-- ENDLIBS -->

<link rel="stylesheet" type="text/css" href="<%=CSS_BASE_PATH%>styles.css" />
<script type="text/javascript" src="<%=JS_BASE_PATH%>js_helpers.js"></script> 
<script type="text/javascript" src="<%=JS_BASE_PATH%>tree.js"></script> 
<script type="text/javascript" src="<%=JS_BASE_PATH%>panel_files.js"></script>
<script type="text/javascript" src="<%=JS_BASE_PATH%>panel_security.js"></script> 

<!-- multi-upload -->
<script type="text/javascript" src="<%=JS_BASE_PATH%>upload_js.js"></script>

<script>
    // set up location objects objects so that they're properly gatewayed
    var actionServlet = new Object(); actionServlet.location = '<%=SERVLET_URL%>';
    var actionServletURL = actionServlet.location;
    var swfupload = new Object(); swfupload.location = '<%=JS_BASE_PATH%>/Swiff.Uploader.swf';
    var swfUploadURL = swfupload.location;

    var win;
    var activeTreeNode;
    var panel, tree, detailspane, resizer;
    var rootFolderID = <%=BASE_KD_FOLDER_ID%>;
    var rootFolderName = '<%=BASE_KD_FOLDER_NAME%>';
    // entry point the ext libraries
    Ext.onReady(function() {
            Ext.BLANK_IMAGE_URL = "<%=CSS_BASE_PATH%>blank.gif";
            Ext.QuickTips.init();

            // create the various tab panels
            PanelFiles.init();
            panel_files = PanelFiles.getPanel();

//            PanelSecurity.init();
//            panel_security = PanelSecurity.getPanel();

            docTree.init();
            tree = docTree.getTree();

            detailspane = new Ext.TabPanel({
                    id: 'DetailsPane',
//			items:[panel_files, panel_security],
                    items:[panel_files],
                    alwaysShowTabs: false,
                    enableTabScroll: true,  
                    margins:'0 3 0 0',
                    tabWidth:125,
                    deferredRender: true,
                    layoutOnTabChange: true,
                    activeTab: 0,
                    region: 'center'
            });


            panel = new Ext.Panel({
                    id:'panel',
                    layout: 'border',
                    items: [tree, detailspane],
                    height: 300,
                    monitorResize:true
            });

            resizer = new Ext.Resizable("content", {
                id: 'resizer',
                handles: 'e se s',
                minWidth: 200,
                minHeight: 100,
                height:300
            });
            resizer.on("resize", resizeGrid);
            panel.render("content");
            resizeGrid();
            activeTreeNode = tree.getRootNode();
            changeTabContent(rootFolderID, rootFolderName);
    });
</script>

<div id="content" style="text-align:left; height:270;width:100%"></div>

<script>
/*   // register the callback to be called once the JS is loaded
    iScriptLoader.setCallBack(showKDReady);
    // dynamically load the JS and CSS required
    loadCSSByKey('EXTCSS');
    iScriptLoader.load('EXTJSBASE', true);
    iScriptLoader.load('EXTJSALL', true);
    iScriptLoader.load('PTUTILFIX', false);
*/
</script>

