/**
 * 表单与表格视图
 */
Ext.define('DataEvent.view.DataEventSetFormView',{
    extend:'Ext.panel.Panel',
    xtype:'dataEventSetFormView',
    layout:'border',
    items:[{
    	region:'center',//中间
    	itemId:'northform',//上方的表单视图
    	xtype:'dataEventFormView'//表单类型
    }]
});