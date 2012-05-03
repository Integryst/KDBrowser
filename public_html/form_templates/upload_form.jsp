<%@ page import="com.integryst.kdbrowser.*, com.integryst.kdbrowser.helpers.*, java.util.*, com.plumtree.remote.portlet.*, com.plumtree.remote.prc.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" >
<head>
<title>File Upload</title>

<%
    // get preferences
    String JS_BASE_PATH = PrefsHelper.getPreference(request, "portlet.url.js");
    String collabProjectID = request.getParameter("collabProjectID");
    String collabFolderID = request.getParameter("collabFolderID");
    
    IPortletContext pc = PortletContextFactory.createPortletContext(request,response);
    IPortletRequest req = pc.getRequest();
    String ali_token = req.getLoginToken();

%>
<link rel="stylesheet" type="text/css" href="KDBrowser/css/styles.css" />
 
<script type="text/javascript" src="KDBrowser/js/upload/swfupload.js"></script>
<script type="text/javascript" src="KDBrowser/js/upload/swfupload.swfobject.js"></script>
<script type="text/javascript" src="KDBrowser/js/upload/swfupload.queue.js"></script>
<script type="text/javascript" src="KDBrowser/js/upload/fileprogress.js"></script>
<script type="text/javascript" src="KDBrowser/js/upload/handlers.js"></script>
<script type="text/javascript"> 
    // set up location objects objects so that they're properly gatewayed
    var flashPath = new Object(); flashPath.location = 'KDBrowser/js/upload/swfupload.swf';
    var flashPathURL = flashPath.location;

var swfu;

SWFUpload.onload = function () {
	var settings = {
		flash_url : flashPathURL,
		upload_url: "/kdUploader/upload.aspx",
		file_size_limit : "100 MB",
		file_types : "*.*",
		file_types_description : "All Files",
		file_upload_limit : 100,
		file_queue_limit : 0,
                
                file_post_name : "fileName",
                post_params : {
                        "collabProjectID" : "<%=collabProjectID%>",
                        "collabFolderID" : "<%=collabFolderID%>",
                        "ali_token" : "<%=ali_token%>"
                },
                
		custom_settings : {
			progressTarget : "fsUploadProgress",
			cancelButtonId : "btnCancel",
			commitButtonId : "btnCommit"
		},
		debug: false,
 
		// Button Settings
		button_placeholder_id : "spanButtonPlaceholder",
		button_width: 61,
		button_height: 22,
		button_window_mode: SWFUpload.WINDOW_MODE.TRANSPARENT,
		button_cursor: SWFUpload.CURSOR.HAND,
 
		// The event handler functions are defined in handlers.js
		swfupload_loaded_handler : swfUploadLoaded,
		file_queued_handler : fileQueued,
		file_queue_error_handler : fileQueueError,
		file_dialog_complete_handler : fileDialogComplete,
		upload_start_handler : uploadStart,
		upload_progress_handler : uploadProgress,
		upload_error_handler : uploadError,
		upload_success_handler : uploadSuccess,
		upload_complete_handler : uploadComplete,
		queue_complete_handler : queueComplete,	// Queue plugin event
		
		// SWFObject settings
		minimum_flash_version : "9.0.28",
		swfupload_pre_load_handler : swfUploadPreLoad,
		swfupload_load_failed_handler : swfUploadLoadFailed
	};
 
	swfu = new SWFUpload(settings);
}
 
</script>
</head>
<body>

<div id="content">
 
	<form id="form1" action="index.php" method="post" enctype="multipart/form-data">
 
		<noscript style="background-color: #FFFF66; border-top: solid 4px #FF9966; border-bottom: solid 4px #FF9966; margin: 10px 25px; padding: 10px 15px;">
			We're sorry.  SWFUpload could not load.  You must have JavaScript enabled to enjoy SWFUpload.
		</noscript>
		<div id="divLoadingContent" class="content" style="background-color: #FFFF66; border-top: solid 4px #FF9966; border-bottom: solid 4px #FF9966; margin: 10px 25px; padding: 10px 15px; display: none;">
			SWFUpload is loading. Please wait a moment...
		</div>
		<div id="divLongLoading" class="content" style="background-color: #FFFF66; border-top: solid 4px #FF9966; border-bottom: solid 4px #FF9966; margin: 10px 25px; padding: 10px 15px; display: none;">
			SWFUpload is taking a long time to load or the load has failed.  Please make sure that the Flash Plugin is enabled and that a working version of the Adobe Flash Player is installed.
		</div>
		<div id="divAlternateContent" class="content" style="background-color: #FFFF66; border-top: solid 4px #FF9966; border-bottom: solid 4px #FF9966; margin: 10px 25px; padding: 10px 15px; display: none;">
			We're sorry.  SWFUpload could not load.  You may need to install or upgrade Flash Player.
			Visit the <a href="http://www.adobe.com/shockwave/download/download.cgi?P1_Prod_Version=ShockwaveFlash">Adobe website</a> to get the Flash Player.
		</div>
			<p>
				<span id="spanButtonPlaceholder"></span>
				<input id="btnUpload" type="button" value="Select Files" style="width: 61px; height: 22px; font-size: 8pt;" />
				<input id="btnCommit" type="button" value="Upload Files" disabled="disabled" style="margin-left: 2px; height: 22px; font-size: 8pt;" />
				<input id="btnCancel" type="button" value="Cancel All Uploads" disabled="disabled" style="margin-left: 2px; height: 22px; font-size: 8pt;" />
			</p>		
                        <p id="divStatus">0 Files Uploaded</p>
			<div id="divSWFUploadUI">
			<div class="fieldset flash" id="fsUploadProgress">
			<span class="legend">Upload Queue</span>
			</div>

			<br style="clear: both;" />
		</div>
	</form>
</div>
</body>
</html>
