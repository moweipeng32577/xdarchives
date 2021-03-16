/**
 * Created by tanly on 2017/12/1 0001.
 */
Ext.define('Dataopen.view.DataopenView', {
    extend: 'Ext.panel.Panel',
    xtype: 'dataopenView',
    layout: 'card',
    activeItem: 0,
    items: [{
        layout: 'border',
        bodyBorder: false,
        defaults: {
            split: true
        },
        itemId: 'gridview',
        items: [{
            region: 'west',
            width: XD.treeWidth,
            xtype: 'treepanel',
            itemId: 'treepanelId',
            rootVisible: false,
            store: 'DataopenTreeStore',
            collapsible: true,
            split: 1,
            hideHeaders: true,
            header: false
        }, {
            region: 'center',
            itemId: 'tabviewID',
            xtype: 'dataopenTabView'
        }]
    }, {
    	xtype: 'dataopenFormView'
    }, {
        xtype: 'EntryFormView'
    }, {
        xtype: 'dataopenDealGridView'
    }, {
    	layout: 'border',
        itemId: 'documentInfoView',
        items: [{
            itemId: 'documentInfo',
            xtype: 'dataopenDocumentInfoView'
        }]
    }, {
        layout: 'border',
        itemId: 'sendformview',
        items: [{
            itemId: 'sendformID',
            xtype: 'dataopenSendFormView'
        }, {
            itemId: 'sendgridID',
            height: '51%',
            xtype: 'dataopenSendGridView'
        }]
    }, {
        layout: 'border',
        itemId: 'sendnextview',
        items: [{
            itemId: 'sendnextID',
            xtype: 'dataopenSendNextView'
        }, {
            itemId: 'sendgridID',
            height: '70%',
            xtype: 'dataopenSendGridView'
        }]
    }]
});