/**
 * Created by yl on 2017/11/3.
 */
Ext.define('StApprove.view.StApproveView', {
    extend: 'Ext.panel.Panel',
    xtype: 'stApproveView',
    layout: 'card',
    items: [{
        itemId:'gridview',
        layout: 'border',
        items:[
            {xtype: 'stApproveGridView'},
            {xtype: 'stApproveFormView'}
            ]},{
            itemId: 'mediaFormView',
            xtype: 'mediaFormView'
        }]
});