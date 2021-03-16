/**
 * Created by yl on 2017/11/3.
 */
Ext.define('ElectronApprove.view.ElectronApproveView', {
    extend: 'Ext.panel.Panel',
    xtype: 'electronApproveView',
    layout: 'card',
    items: [{
        layout: 'border',
        xtype: 'panel',
        itemId: 'gridview',
        items: [{
                xtype: 'electronApproveGridView'
            }, {
                xtype: 'electronApproveFormView'
            }]
        }, {
            itemId: 'mediaFormView',
            xtype: 'mediaFormView'
        },{
        xtype:'EntryFormView'
    }]
});