Ext.define('DigitalProcess.view.DigitalProcessView', {
    extend: 'Ext.panel.Panel',
    xtype: 'DigitalProcessView',
    layout: 'card',
    activeItem: 0,
    items: [{
        layout: 'border',
        bodyBorder: false,
        defaults: {
            split: true
        },
        itemId: 'gridview',
        items: [
            {
                region: 'west',
                width: 240,
                xtype: 'DigitalProcessLeftView',
                itemId: 'treepanelId',
                rootVisible: false,
                store: 'DigitalProcessTreeStore',
                collapsible: true,
                split: 1,
                hideHeaders: true,
                header: false,
            },
            {
                region: 'center',
                itemId:'rightCard',
                width: '70%',
                layout: 'card',
                activeItem: 0,
                items:[
                    {
                        itemId: 'tabviewId',
                        xtype: 'DigitalProcessTabView'
                    },
                    {
                        itemId: 'finishViewId',
                        xtype:'DigitalProcessWchjGridView'
                    }
                ]
            },
            {
                region: 'east',
                width: 270,
                xtype: 'DigitalProcessMessageRightView',
                title:"相关日志信息",
                itemId: 'massageRightViewId',
                rootVisible: false,
                store: 'DigitalProcessMessageRightStore',
                collapsible: true,
                split: 1,
                hideHeaders: true,
                header: false,
            }
            ]
    }
    // , {
    // 	xtype: 'dataopenFormView'
    // }, {
    //     xtype: 'EntryFormView'
    // }, {
    //     xtype: 'dataopenDealGridView'
    // }, {
    // 	layout: 'border',
    //     itemId: 'documentInfoView',
    //     items: [{
    //         itemId: 'documentInfo',
    //         xtype: 'dataopenDocumentInfoView'
    //     }]
    // }, {
    //     layout: 'border',
    //     itemId: 'sendformview',
    //     items: [{
    //         itemId: 'sendformID',
    //         xtype: 'dataopenSendFormView'
    //     }, {
    //         itemId: 'sendgridID',
    //         height: '51%',
    //         xtype: 'dataopenSendGridView'
    //     }]
    // }, {
    //     layout: 'border',
    //     itemId: 'sendnextview',
    //     items: [{
    //         itemId: 'sendnextID',
    //         xtype: 'dataopenSendNextView'
    //     }, {
    //         itemId: 'sendgridID',
    //         height: '70%',
    //         xtype: 'dataopenSendGridView'
    //     }]
    // }
	]
});