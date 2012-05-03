///// The panel that is used to view security
PanelSecurity = function() {
	var panel;

	return {
		init: function(url) {

			// The grid definition itself
			panel = new Ext.Panel(
				{
                                        layout:'fit',
					id: 'PanelSecurity',
					region: 'center', 
					loadMask: true,
                                        title: 'Security',
                                        autoWidth: true,
                                        autoHeight: true,
                                        forcefit: true,
                                        autoScroll:true,
                                        iconCls:'rule',
                                        //listeners: {activate: changeTabContent},
                                        tag: "Security",
                                        height:270
				}
			);
		},

		getPanel: function() {
			return panel;
		}
	}
} ();
