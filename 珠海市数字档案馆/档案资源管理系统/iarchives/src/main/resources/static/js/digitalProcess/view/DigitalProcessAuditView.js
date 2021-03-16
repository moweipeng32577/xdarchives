/**
 * Created by Administrator on 2019/9/20.
 */


Ext.define('DigitalProcess.view.DigitalProcessAuditView', {
    extend: 'Ext.window.Window',
    width:'100%',
    height:'100%',
    header:false,
    xtype:'DigitalProcessAuditView',
    layout: 'border',
    bodyBorder: false,
    modal:true,
    closeAction:'hide',
    defaults: {
        collapsible: true,
        split: true
    },
    items: [
        {
            itemId:'detailLeftId',
            width: '28%',
            title: '(按下Ctrl+F可查找)',
            region: 'west',
            floatable: false,
            header:false,
            margin: '0 0 0 0',
            layout: 'fit',
            items: [{
                xtype:'DigitalProcessAuditLeftUpView'
            }]
        },
        {
            itemId:'detailCenterId',
            collapsible: false,
            region: 'center',
            margin: '0 0 0 0',
            layout: 'fit',
            items: [{
                html:'<div id="p5" class="pw-view"></div>'
            }]
        },
        {
            itemId:'detailRightId',
            width: '22%',
            region: 'east',
            floatable: false,
            header:false,
            margin: '0 0 0 0',
            layout: 'fit',
            items: [{
                xtype:'EntryFormView1'
            }]
        }
    ]
});
