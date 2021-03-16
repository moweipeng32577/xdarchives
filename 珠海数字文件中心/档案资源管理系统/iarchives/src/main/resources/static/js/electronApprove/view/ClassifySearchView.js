/**
 * Created by Administrator on 2020/7/17.
 */


Ext.define('ElectronApprove.view.ClassifySearchView', {
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
                title: '分类选择'
            },{
                region: 'center',
                itemId:'classifySearchPromptViewId',
                layout:'card',
                activeItem:0,
                items: [{
                    xtype: 'panel',
                    itemId: 'bgSelectOrgan',
                    bodyStyle: 'background:#DFE8F6;background-image:url(../../img/background/logPrompt.jpg);background-repeat:no-repeat;background-position:center;'
                },{
                    xtype: 'classifySearchFormView'
                }]
            }]
        },{
            itemId:'gridview',
            xtype:'classifySearchResultGridView'
        },{
            xtype:'EntryFormView'
        }
    ]
});
