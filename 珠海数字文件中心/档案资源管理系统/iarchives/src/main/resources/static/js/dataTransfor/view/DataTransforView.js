Ext.define('DataTransfor.view.DataTransforView', {
    extend: 'Ext.panel.Panel',
    xtype:'dataTransforView',
    layout: 'card',
    items: [{
        itemId:'formview',
        layout:'border',
        items:[{
        	region: 'west',
        	title: '原节点',
        	xtype: 'dataTransforSelectView'
        },{
            region: 'center',
            layout: 'card',
            activeItem: 1,
            itemId: 'dataTransforGridId',
        	items: [{
        		title: '节点条目',
            	xtype: 'dataTransforGridView'
            }, {
                xtype: 'panel',
                itemId: 'bgSelectOrgan',
                bodyStyle: 'background:#DFE8F6;background-image:url(../../img/background/logPrompt.jpg);background-repeat:no-repeat;background-position:center;'
            }]
        },{
        	region: 'east',
        	title: '目标节点',
        	xtype: 'dataTransforSelectedView'
        }]
    },{
    	xtype: 'dataTransforFieldView'
    }]
});