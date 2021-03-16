/**
 * 事件管理控制器
 */
Ext.define('DataEvent.view.DataEventView',{
    extend:'Ext.panel.Panel',
    xtype:'dataEventView',
    layout:'card',
    activeItem:0,
    items:[{
    	layout:'border',
        itemId: 'gridview',
        items: [{
            region: 'center',
            xtype: 'dataEventGridView'
        }]
    },{
    	xtype: 'dataEventGridView'
    },{
    	xtype: 'dataEventAddView'
    },{
    	xtype: 'dataEventDetailGridView'
    },{
    	xtype: 'simpleSearchView'
    },{
    	xtype: 'dataEventSetFormView'
    }]
});