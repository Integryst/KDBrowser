// doc tree object
docTree = function(){

	// private "member variables"
	var tree; 
	var loader;

	return {
		// initialize the tree
		init : function(){

			tree = new Ext.tree.TreePanel(
				{
					region:'west',
					id:'docTreePanel',
					title:'Folders',
					split:true,
					width: 200,
					minSize: 50,
					maxSize: 400,
					collapsible: true,
					autoScroll: true,
					margins:'0 0 0 5',
					rootVisible:true,
					lines:true,		
					enableDD:false,
					containerScroll: true,
					root: new Ext.tree.AsyncTreeNode(
						{
                                                        id: rootFolderID,
                                                        text: rootFolderName,
							draggable:false,
							allowDrop:false,
							allowDrag:false,
                                                        treeNodeType:'root',
                                                        expanded:true
						}
					)
				}
			);

			t=tree;
			tree.on(
				'click', 
				function(node) {
                                        handleTreeClick(node);
				}
			);

			loader = new Ext.tree.TreeLoader({dataUrl:actionServletURL + "?action=getfolders"}); 
                        loader.on(
                            'beforeload', 
                            function(loader, node){ 
                                loader.baseParams = {
                                    id: node.attributes.id,
                                    text: node.attributes.text
                                };
                            }
                        );
                        tree.loader = loader;
		},

		getTree : function() {
			return tree;
		}
	}
} ();
