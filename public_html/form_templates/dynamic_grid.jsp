<%@ page import="com.integryst.kdbrowser.extjs.*, com.integryst.kdbrowser.helpers.*, java.util.*" %>

<span xmlns:pt='http://www.plumtree.com/xmlschemas/ptui/'>
<% 
    ArrayList grid_columns = (ArrayList)request.getAttribute("grid_columns"); 
    int folder_id = (Integer)request.getAttribute("folder_id");

    String EXT_BASE_PATH = PrefsHelper.getPreference(request, "portlet.url.ext");
    String CSS_BASE_PATH = PrefsHelper.getPreference(request, "portlet.url.css");
    String JS_BASE_PATH = PrefsHelper.getPreference(request, "portlet.url.js");
    String SERVLET_URL = PrefsHelper.getPreference(request, "portlet.url.servlet");
    String UPLOAD_URL = PrefsHelper.getPreference(request, "portlet.url.uploadTargetURL");

    String ADMIN_GROUP_ID = PrefsHelper.getPreference(request, "portlet.adminGroupID");
    int PAGINATION_SIZE = PrefsHelper.getIntPreference(request, "portlet.paginationsize");
%>

<script>

// user vars
var grid2;

<pt:standard.choose>
    <pt:when pt:test="stringToACLGroup('group=<%=ADMIN_GROUP_ID%>;').isMember($currentuser)">
// admin vars
var swiffy;
var uploadGrid;
var fileProgressBar;
var uploadProgressBar;
var statusContainer;
var FileUploadEntry;
        </pt:standard.when>
</pt:standard.choose>

// user functions
refreshGrid = function() {
    searchMenu.items.get('cancel_search').disable();
    grid2.store.baseParams = {folder_id:<%=folder_id%>, action:'getfiles'};
    grid2.store.load();
}

doSearchCurrent = function() { doSearchAction("search_current_only"); }
doSearchAll = function() { doSearchAction("search_all"); }
doSearch = function() { doSearchAction("search_current_and_subs"); }

doSearchAction = function(options)
{
    search = Ext.get('docsearchfield').dom.value;
    searchMenu.items.get('cancel_search').enable();
    grid2.store.baseParams = {searchtext: search, action:'search', folder_id:<%=folder_id%>, options: options};
    grid2.store.load({params:{start:0,limit:200}});
}

<pt:standard.choose>
    <pt:when pt:test="stringToACLGroup('group=<%=ADMIN_GROUP_ID%>;').isMember($currentuser)">
// admin functions
openUploadDialog = function() {
    window.open(actionServletURL + '?action=uploadpopup', 'uploadwindow', 'status=0, toolbar=0, location=0, menubar=0, resizable=1, scrollbars=yes, width=440, height=600');
}

browseFiles = function() {
    if (swiffy != null)
        swiffy.browse();
}

clearList = function() {
    uploadGrid.store.removeAll();
    swiffy.removeFile();
}

uploadFilesButton = function() {
    swiffy.upload();
    return false;
}
        </pt:standard.when>
</pt:standard.choose>


Ext.onReady(function() {

<pt:standard.choose>
    <pt:when pt:test="stringToACLGroup('group=<%=ADMIN_GROUP_ID%>;').isMember($currentuser)">
createFolder = function(btn, text) {
    if (btn == 'ok'){
        var currTreeNode = activeTreeNode;
        var conn = new Ext.data.Connection();
        conn.request({
            url: actionServletURL,
            method: 'POST',
            params: {"action": "createfolder", "name":text, "rootID":currTreeNode.attributes.id},
            success: function(responseObject) {
                var res = Ext.util.JSON.decode(responseObject.responseText);
                if (res.success)
                {
                    Ext.MessageBox.show({
                       title: 'Folder Created',
                       msg: res.message,
                       buttons: Ext.MessageBox.OK,
                       animEl: 'body'
                   });
                   
                    var newNode = new Ext.tree.TreeNode(
                    {
                            id: res.folderId,
                            text: res.folderName,
                            draggable:false,
                            allowDrop:false,
                            allowDrag:false,
                            iconCls:'folder'
                    });
                    
                    currTreeNode.leaf = false;
                    currTreeNode.appendChild(newNode);
                    currTreeNode.expand();
                    currTreeNode.iconCls = 'folderopen';
                }
                else {
                    Ext.MessageBox.show({
                       title: 'Folder Creation Failed',
                       msg: res.message,
                       buttons: Ext.MessageBox.OK,
                       animEl: 'body',
                       icon: Ext.MessageBox.ERROR
                   });
                }
            },
             failure: function() {
                 Ext.Msg.alert('Folder Creation Failed', 'Failed connecting to server.');
             }
        });
    }

}
createFolderPrompt = function() {
    // Prompt for user data and process the result using a callback:
    Ext.Msg.prompt('Create Folder', 'Enter a folder name:', createFolder);

}

deleteFiles = function() {
    if (grid2.selModel.getSelected().data) {
        var selectedName = grid2.selModel.getSelected().data.name;
        Ext.MessageBox.confirm('Confirm', 'Are you sure you want to delete this file?<br>' + selectedName, deleteFilesConfirmed );
    }
    else {
        Ext.Msg.alert('No File Selected', 'Select a file from the grid to delete it.');
    }
}

deleteFilesConfirmed = function(btn) {
    if (btn != "yes")
        return;

    var selectedId = grid2.selModel.getSelected().data.objectid;
    var conn = new Ext.data.Connection();
    conn.request({
        url: actionServletURL,
        method: 'POST',
        params: {action: "deletefile", cardid:selectedId},
        success: function(responseObject) {
            var res = Ext.util.JSON.decode(responseObject.responseText);
            if (res.success)
            {
                refreshGrid();
            }
            else {
                Ext.MessageBox.show({
                   title: 'Deleting File Failed',
                   msg: res.message,
                   buttons: Ext.MessageBox.OK,
                   animEl: 'body',
                   icon: Ext.MessageBox.ERROR
               });
            }
        },
         failure: function() {
             Ext.Msg.alert('Deleting File Failed', 'Failed connecting to server.');
         }
    });
}

saveChanges = function() {
    var allRecords = grid2.store.getModifiedRecords();
    if (allRecords.length == 0) {
        Ext.Msg.alert('No Changes Found', 'Double-click a cell in an editable (green) column to change the text.');
        return;        
    }
    var arr = [];
    for (i=0; i<allRecords.length; i++) {
        tmp = {id: allRecords[i].data.objectid, changes:allRecords[i].getChanges()};
        arr.push(tmp);
    }
    var changedObjects = {changedObjects: arr};
    var jsonObj = Ext.util.JSON.encode(changedObjects);
    var conn = new Ext.data.Connection();
    conn.request({
        url: actionServletURL,
        method: 'POST',
        params: {action: "saveproperties", json:jsonObj},
        success: function(responseObject) {
            var res = Ext.util.JSON.decode(responseObject.responseText);
            if (res.success)
            {
                refreshGrid();
            }
            else {
                Ext.MessageBox.show({
                   title: 'Saving Properties Failed',
                   msg: res.message,
                   buttons: Ext.MessageBox.OK,
                   animEl: 'body',
                   icon: Ext.MessageBox.ERROR
               });
            }
        },
         failure: function() {
             Ext.Msg.alert('Saving Properties Failed', 'Failed connecting to server.');
         }
    });
}

uploadFiles = function() {
    var oURL = new Object(); oURL.location = btn.actionurl;
    var sURL = oURL.location;

// Reader for Files
        FileUploadEntry = Ext.data.Record.create([
           {name: 'fileName', type: 'string'},
           {name: 'fileSize', type: 'string'},
           {name: 'uploadStatus', type: 'string'}
        ]);

	var store = new Ext.data.Store({
        // the return will be XML, so lets set up a reader
        reader: new Ext.data.XmlReader({
               record: 'fileuploadentry'
           }, FileUploadEntry)
        });
        
        // The grid definition itself
        uploadGrid = new Ext.grid.GridPanel(
                {
                        store: store,
                        id: 'UploadGridPanel',
                        layout: 'fit',
                        forceFit: true,
                        autoScroll:true,
                        enableDragDrop : false,
                        region:'center',
                        columns: [
                            {id:'fileName', dataIndex:'fileName', header:'File Name', width:170, hidden: false},
                            {id:'fileSize', dataIndex:'fileSize', header:'File Size', width:70, hidden: false},
                            {id:'uploadStatus', dataIndex:'uploadStatus', header:'Status', width:100, hidden: false}
                        ],
                        tbar: [
                             {
                                iconCls:'browse_files',
                                text: 'Browse Files',
                                id: 'browsebutton',
                                handler: browseFiles
                            },
                            {
                                iconCls:'upload_files',
                                text: 'Upload Files',
                                handler: uploadFilesButton
                            },
                            {
                                iconCls:'clear_list',
                                text: 'Clear List',
                                handler: clearList
                            }                            
                        ]

                }
        );

        uploadGrid.on(
                'rowdblclick', 
                function(grid, rowIndex, e) {
                    var row = resultDS.getAt(rowIndex);
                    var url = row.data.objectURL;
                }
        );
        
        fileProgressBar = new Ext.ProgressBar({
            text:'',
            height:20,
            border:false,
            bodyBorder:false
        });
        
        uploadProgressBar = new Ext.ProgressBar({
            text:'Select Files...',
            height:20
        });

        statusContainer = new Ext.Panel({
                    region:'south',
                    id:'status-panel',
                    height:42,
                    border:false,
                    bodyBorder:false,
                    items: [fileProgressBar,uploadProgressBar]
                });

    win = new Ext.Window({
        title: 'Upload Files',
        layout      : 'border',
        animateTarget: false,
        width       : 400,
        height      : 250,
        autoHeight: false,
        autoWidth: false,
        autoScroll: false,
        modal: true,
        constrain:true,
        plain:false,
        items:[uploadGrid, statusContainer]
    })
    win.show();
    
    var relativeFolder = activeTreeNode.getPath("text");
    relativeFolder = encodeURI(relativeFolder.substring(rootFolderName.length+1));

    swiffy = new FancyUpload3(uploadProgressBar, uploadGrid, {
            url: '<%=UPLOAD_URL%>?folderId=' + activeTreeNode.id + '&folder=' + relativeFolder,
            fieldName: 'fileupload',
            path: swfUploadURL,
            limitSize: 20 * 1024 * 1024, // 20Mb
            target: 'browsebutton' // the element for the overlay (Flash 10 only)
    });
}
        </pt:standard.when>
</pt:standard.choose>

	// Reader for Results
	var ruleReader = new Ext.data.JsonReader(
		{
                        totalProperty: 'count',
			root: 'items',
			id: 'folder_id'
		}, 
		[
                
<%
    // create the reader columns
    Enumeration e = Collections.enumeration(grid_columns);
    GridColumn temp;
    while(e.hasMoreElements())
    {
        temp = (GridColumn)e.nextElement();
        out.print ("{name: '" + temp.getId() + "'");
        if (temp.getMapping() != null)
            out.print(", mapping:'" + temp.getMapping() + "'");
        if (temp.isDateField())
            out.print(", type:'date', dateFormat: 'Y-m-d h:i:s.0'");
        if (e.hasMoreElements())
            out.println("},");
        else
            out.println("}");
    }
%>
		]
	);

        // Data Store for Results
        var resultDS = new Ext.data.Store(
                {
                        proxy: new Ext.data.HttpProxy(
                                {url: actionServletURL}
                        ),
                        reader: ruleReader
                }
        );

        // the search menu
        searchMenu = new Ext.menu.Menu({
            id: 'docsearchmenu',
            items: [
                    {text: '<b>Search current folder and subfolders</b>', handler: doSearch},
                    {text: 'Search current folder only', handler: doSearchCurrent},
                    {text: 'Search all folders', handler: doSearchAll},
                    {
                        text:'Reset',
                        tooltip:'Reset Search Field',
                        disabled: true,
                        id: 'cancel_search',
                        handler: refreshGrid
                    }
            ]
        })

        // The grid definition itself
<pt:standard.choose>
    <pt:when pt:test="stringToACLGroup('group=<%=ADMIN_GROUP_ID%>;').isMember($currentuser)">
        grid2 = new Ext.grid.EditorGridPanel(
    </pt:standard.when>
    <pt:standard.otherwise>
        grid2 = new Ext.grid.GridPanel(
    </pt:standard.otherwise>
</pt:standard.choose>
                {
                        ds: resultDS,
                        id: 'ResultGridPanel',
                        layout: 'fit',
                        forceFit: true,
                        autoScroll:true,
                        enableDragDrop : false,
                        loadMask: true,
<pt:standard.choose>
    <pt:when pt:test="stringToACLGroup('group=<%=ADMIN_GROUP_ID%>;').isMember($currentuser)">
                        clicksToEdit:2,
        </pt:standard.when>
</pt:standard.choose>
                        selModel: new Ext.grid.RowSelectionModel({singleSelect: true}),
                        columns: [
<%
    // create the grid columns
    Enumeration e2 = Collections.enumeration(grid_columns);
    GridColumn temp2;
    while(e2.hasMoreElements())
    {
        temp2 = (GridColumn)e2.nextElement();
        out.print ("{sortable:true, id:'" + temp2.getId() + "'");
        out.print (", header:'");
        out.print ("<pt:standard.choose>");
        out.print ("<pt:when pt:test=\"stringToACLGroup('group=" + ADMIN_GROUP_ID + ";').isMember($currentuser)\">");
        if (temp2.isEditable())
            out.print("<B><FONT COLOR=008800>" + temp2.getName() + "</FONT></B>");
        else
            out.print("<B><FONT COLOR=880000>" + temp2.getName() + "</FONT></B>");
        out.print ("</pt:standard.when>");
        out.print ("<pt:otherwise>");
        out.print ("<b>" + temp2.getName() + "</b>");
        out.print ("</pt:otherwise>");
        out.print ("</pt:standard.choose>");
        out.print ("'");
        
        if ((temp2.getRenderer() != null) && ((!"".equals(temp2.getRenderer()))))
            out.print(", renderer: function(v, params, record) {" + temp2.getRenderer() + "}");
        out.print (", dataIndex:'" + temp2.getId() + "'");
        if (temp2.getWidth() != 0)
            out.print(", width:" + temp2.getWidth() + "");
        if (temp2.isHidden())
            out.print(", hidden: true");
        if (temp2.isEditable())
            out.print(", editor: new Ext.form.TextField({allowBlank: true})");

        if (e2.hasMoreElements())
            out.println("},");
        else
            out.println("}");
    }
%>
                        ],
                        bbar: new Ext.PagingToolbar({
                            pageSize: <%=PAGINATION_SIZE%>,
                            store: resultDS,
                            displayInfo: true,
                            displayMsg: 'Displaying files {0} - {1} of {2}',
                            emptyMsg: "No files to display"
                        }),
                        tbar: [
<pt:standard.choose>
    <pt:when pt:test="stringToACLGroup('group=<%=ADMIN_GROUP_ID%>;').isMember($currentuser)">
                             {
                                iconCls:'save',
                                text: 'Save Changes',
                                handler: saveChanges
                            },
                             {
                                iconCls:'folder',
                                text: 'Create Folder',
                                handler: createFolderPrompt
                            },
                             {
                                iconCls:'delete_file',
                                text: 'Delete Files',
                                handler: deleteFiles
                            } ,
                             {
                                iconCls:'upload',
                                text: 'Upload Files',
                                handler: openUploadDialog // uploadFiles
                            } 
                            , '-',
        </pt:standard.when>
</pt:standard.choose>
                            'Search: ',
                            new Ext.form.TextField({
                                    id: 'docsearchfield'
                                }),
                            {
                                text:'Search',
                                tooltip:'Search current folder and subfolders',
                                xtype: 'tbsplit',
                                handler: doSearch,
                                menu: searchMenu
                            }
                        ]
                }
        );

<pt:standard.choose>
    <pt:when pt:test="stringToACLGroup('group=<%=ADMIN_GROUP_ID%>;').isMember($currentuser)"></pt:standard.when>
        <pt:standard.otherwise>
        grid2.on(
                'rowdblclick', 
                function(grid, rowIndex, e) {
                    var row = resultDS.getAt(rowIndex);
                    var url = row.data.objectURL;
<pt:common.transformer pt:fixurl="off" xmlns:pt='http://www.plumtree.com/xmlschemas/ptui/'/>
                    window.open(url);
<pt:common.transformer pt:fixurl="on" xmlns:pt='http://www.plumtree.com/xmlschemas/ptui/'/>
            }
        );
        </pt:standard.otherwise>
</pt:standard.choose>

        resizeGrid();
        grid2.render("dynamic_grid_results");
        refreshGrid();
});

</script>
<div id="dynamic_grid_results" style="height:270;width:100%"></div>
<pt:standard.choose>
    <pt:when pt:test="stringToACLGroup('group=<%=ADMIN_GROUP_ID%>;').isMember($currentuser)">
<div id="divupload"></div>
<div id="btn"></div>
<div id="btn_placeholder"></div>
        </pt:standard.when>
</pt:standard.choose>
</span>