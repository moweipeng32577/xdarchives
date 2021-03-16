/**
 * Created by Administrator on 2019/6/27.
 */


Ext.define('ClassifySearchDirectory.view.ClassifySearchDirectoryView', {
    extend: 'Ext.panel.Panel',
    xtype:'classifySearchDirectoryView',
    layout: 'card',
    items: [
        {
            itemId:'formview',
            layout:'border',
            items:[{
                region: 'west',
                width: XD.treeWidth,
                xtype:'classifySearchDirectoryTreeView',
                itemId:'classifySearchDirectoryTreeViewId',
                rootVisible:false,
                store: 'ClassifySearchDirectoryTreeStore',
                collapsible:true,
                split:1,
                // header:false,
                // hideHeaders: true,
                title: '机构选择'
            },{
                region: 'center',
                layout:'fit',
                xtype: 'classifySearchPromptDirectoryView'
            }]
        },{
            itemId:'gridview',
            xtype:'classifySearchDirectoryGridView'
        },{
            xtype:'classifySearchDirectoryFormView'
        }
    ]
});
