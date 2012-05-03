///// The panel that is used to view files
PanelFiles = function() {
	var panel;

	return {
		init: function(url) {

			// The grid definition itself
			panel = new Ext.Panel(
				{
                                        layout:'fit',
					id: 'PanelFiles',
					region: 'center', 
					loadMask: true,
                                        title: 'Files',
                                        autoWidth: true,
                                        autoHeight: true,
                                        forcefit: true,
                                        autoScroll:true,
                                        iconCls:'properties_icon',
                                        //listeners: {activate: changeTabContent},
                                        tag: ""
				}
			);
		},

		getPanel: function() {
			return panel;
		}
	}
} ();
