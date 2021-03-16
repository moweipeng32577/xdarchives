/**
 * Created by tanly on 2017/11/8 0024.
 */
Ext.define('MetadataTemplate.view.MetadataTemplateView', {
    extend: 'Ext.panel.Panel',
    xtype:'atemplateView',
    layout: 'card',
    activeItem:0,
    items:[{
    	itemId: 'gridview',
        layout:'border',
        items:[{
        	itemId:'templateTreeViewItemID',
	        width: XD.treeWidth,
	        header:false,
	        region: 'west',
	        floatable: false,
	        collapsible:true,
	        rootVisible:false,
	        split:1,
	        layout: 'fit',
	        items: [{xtype:'templateTreeView'}]
        },{
        	itemId: 'templatePromptViewID',
	        collapsible: false,
	        region: 'center',
	        layout:'card',
	        activeItem:1,
	        items: [{
	            xtype: 'atemplateGridView'
	        },{
	            xtype: 'panel',
	            itemId: 'bgSelectOrgan',
	            bodyStyle: 'background:#DFE8F6;background-image:url(../../img/background/logPrompt.jpg);background-repeat:no-repeat;background-position:center;'
	        }]
        }]
    },{
    	xtype: 'atemplateGridPreView'
    }, {
    	xtpye: 'templateGridInfoView'
    }, {
    	xtype:'atemplateFormInfoView'
    },{
    	xtype:'GroupingManagement'
	}]
});