/**
 * Created by Administrator on 2019/7/24.
 */



Ext.define('DigitalProcess.view.DigitalProcessMessageRightView', {
    extend: 'Ext.panel.Panel',
    xtype: 'DigitalProcessMessageRightView',
    layout:'border',
    defaults: {
        bodyPadding: 12,
        scrollable: true
    },
    items:[
        {
            region:'center',
            itemId:'treepanelMessageId',
            hideMode: 'visibility',
            title:'相关日志信息',
            autoScroll:true,
        }
    ]
});
