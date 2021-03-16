/**
 * Created by RonJiang on 2018/01/24
 */
Ext.define('BatchModify.view.BatchModifyView', {
    extend: 'Ext.panel.Panel',
    xtype:'batchModify',
    layout: 'card',
    items: [{
        itemId:'formview',
        layout:'border',
        items:[{
            region: 'west',
            width: XD.treeWidth,
            xtype:'batchModifyTreeView',
            itemId:'batchModifyTreeId',
            rootVisible:false,
            store: 'BatchModifyTreeStore',
            collapsible:true,
            split:1,
            header:false,
            hideHeaders: true,
            title: '(按下Ctrl+F可查找)'
        },{
            region: 'center',
            layout:'fit',
            xtype: 'batchModifyPromptView'
        }]
    },{
        itemId:'gridview',
        xtype:'batchModifyResultGridView'
    },{
        xtype:'EntryFormView'
    }]
});