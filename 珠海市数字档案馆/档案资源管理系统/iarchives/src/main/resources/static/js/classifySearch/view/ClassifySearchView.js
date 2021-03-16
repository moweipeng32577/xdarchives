/**
 * Created by RonJiang on 2017/10/26 0026.
 */
Ext.define('ClassifySearch.view.ClassifySearchView', {
    extend: 'Ext.panel.Panel',
    xtype:'classifySearchView',
    layout: 'card',
    items: [
        {
            itemId:'formview',
            layout:'border',
            items:[{
                region: 'west',
                width: XD.treeWidth,
                xtype:'classifySearchTreeView',
                itemId:'classifySearchTreeId',
                rootVisible:false,
                store: 'ClassifySearchTreeStore',
                collapsible:true,
                split:1,
                // header:false,
                // hideHeaders: true,
                title: '分类选择'
            },{
                region: 'center',
                layout:'fit',
                xtype: 'classifySearchPromptView'
            }]
        },{
            itemId:'gridview',
            xtype:'classifySearchResultGridView'
        },{
            xtype:'EntryFormView'
        }
    ]
});