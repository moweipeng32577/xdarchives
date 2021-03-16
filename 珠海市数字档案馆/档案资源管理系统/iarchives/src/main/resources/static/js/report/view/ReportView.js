/**
 * Created by RonJiang on 2018/2/27 0027.
 */
Ext.define('Report.view.ReportView', {
    //extend: 'Ext.panel.Panel',
    xtype:'report',
    extend: 'Ext.tab.Panel',
    requires: [
        'Ext.layout.container.Border'
    ],
    tabPosition: 'top',
    tabRotation: 0,
    activeTab: 0,

    items: [{
        title: '档案系统',
        itemId: 'daxtId',
        layout:'card',
        activeItem:0,
        items:[{
            layout: 'border',
            itemId:'treegridview',
            defaults: {
                collapsible: true,
                split: true
            },
            items: [{
                region: 'west',
                width: XD.treeWidth,
                xtype:'treepanel',
                itemId:'reportTreeId',
                rootVisible:false,
                store: 'ReportTreeStore',
                collapsible:true,
                split:1,
                header:false,
                hideHeaders: true,
                title: '(按下Ctrl+F可查找)'
            },{
                region: 'center',
                layout: 'fit',
                header:false,
                xtype:'reportPromptView'
            }]
        },{
            xtype:'reportform'
        }]
    },{
        title: '声像系统',
        itemId: 'sxxtId',
        layout:'card',
        activeItem:0,
        hidden:!openSxData,
        items:[{
            layout: 'border',
            itemId:'sxtreegridview',
            defaults: {
                collapsible: true,
                split: true
            },
            items: [{
                region: 'west',
                width: XD.treeWidth,
                xtype:'treepanel',
                itemId:'reportSxTreeId',
                rootVisible:false,
                store: 'ReportSxTreeStore',
                collapsible:true,
                split:1,
                header:false,
                hideHeaders: true,
                title: '(按下Ctrl+F可查找)'
            },{
                region: 'center',
                layout: 'fit',
                header:false,
                xtype:'reportSxPromptView'
            }]
        },{
            xtype:'reportSxform'
        }]
    }]

});