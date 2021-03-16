/**
 * Created by RonJiang on 2018/01/24
 */
Ext.define('CodesettingAlign.view.CodesettingAlignView', {
    extend: 'Ext.panel.Panel',
    xtype:'codesettingAlign',
    layout: 'card',
    items: [
        {
            itemId:'formview',
            layout:'border',
            items:[{
                region: 'west',
                width: XD.treeWidth,
                xtype:'codesettingAlignTreeView',
                itemId:'codesettingAlignTreeId',
                rootVisible:false,
                store: 'CodesettingAlignTreeStore',
                collapsible:true,
                split:1,
                header:false,
                hideHeaders: true,
                title: '(按下Ctrl+F可查找)'
            },{
                region: 'center',
                layout:'fit',
                xtype: 'codesettingAlignPromptView'
            }]
        },{
            itemId:'gridview',
            xtype:'codesettingAlignResultGridView'
        },{
            xtype:'EntryFormView'
        }
    ]
});