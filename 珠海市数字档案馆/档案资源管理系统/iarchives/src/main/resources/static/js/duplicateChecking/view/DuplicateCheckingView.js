/**
 * Created by RonJiang on 2018/01/24
 */
Ext.define('DuplicateChecking.view.DuplicateCheckingView', {
    extend: 'Ext.panel.Panel',
    xtype:'duplicateChecking',
    layout: 'card',
    items: [
        {
            itemId:'formview',
            layout:'border',
            items:[{
                region: 'west',
                width: XD.treeWidth,
                xtype:'duplicateCheckingTreeView',
                itemId:'duplicateCheckingTreeId',
                rootVisible:false,
                store: 'DuplicateCheckingTreeStore',
                collapsible:true,
                split:1,
                header:false,
                hideHeaders: true,
                title: '(按下Ctrl+F可查找)'
            },{
                region: 'center',
                layout:'fit',
                xtype: 'duplicateCheckingPromptView'
            }]
        },{
            itemId:'gridview',
            xtype:'duplicateCheckingGridView'
        },{
            xtype:'duplicateCheckingEntryView'
        }
    ]
});