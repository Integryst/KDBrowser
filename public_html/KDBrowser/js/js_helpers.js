// TAB MANAGEMENT
handleTreeClick = function(node) {
    activeTreeNode = node;
    if (node.attributes.qtip)
        changeTabContent(node.attributes.id, node.attributes.text + ": <font color=114411>" + node.attributes.qtip + "</font>");
    else
        changeTabContent(node.attributes.id, node.attributes.text);
}

changeTabContent = function(id, name) {

    var activeTab = detailspane.getActiveTab();
    activeTab.setTitle(activeTab.tag + name);
    activeTab.load(
        {
            url: actionServletURL, 
            scripts:true,
            params:
            {
                action:'loadpanel',
                tabType:activeTab.id,
                id: id,
                text: name
            }
        }
    )

}

// SIZE / LAYOUT MANAGEMENT
resizeGridDelay = function() {
    var resizerHeight = Ext.get('content').getHeight();
    panel.setHeight(resizerHeight);
    panel.doLayout();
    if (typeof grid2 != 'undefined')
    {
        grid2.setSize(10,10);
        grid2.setSize(Ext.get('dynamic_grid_results').getWidth() - 2,resizerHeight - 32);
    }
    if (typeof form != 'undefined')
    {
        form.setSize(10,10);
        form.setSize(Ext.get('dynamic_form').getWidth() - 5,resizerHeight - 53);
    }

}

var resizeTimer = null;
resizeGrid = function() {
    if (resizeTimer) clearTimeout(resizeTimer);    
        resizeTimer = setTimeout(resizeGridDelay, 100);
}

window.onresize = function() { resizeGrid(); };
