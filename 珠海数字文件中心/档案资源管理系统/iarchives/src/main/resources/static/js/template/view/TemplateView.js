/**
 * Created by tanly on 2017/11/8 0024.
 */
Ext.define('Template.view.TemplateView', {
    //extend: 'Ext.panel.Panel',
    xtype:'templateView',
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
        layout: 'card',
        activeItem:0,
        items:[{
            itemId: 'gridview',
            layout:'border',
            items:[{
                itemId:'templateTreeViewItemID',
                width: XD.treeWidth,
                header:false,
                region: 'west',
                floatable: false,
                collapsible:true,
                rootVisible:false,
                split:1,
                layout: 'fit',
                items: [{xtype:'templateTreeView'}]
            },{
                itemId: 'templatePromptViewID',
                collapsible: false,
                region: 'center',
                layout:'card',
                activeItem:1,
                items: [{
                    xtype: 'templateGridView'
                },{
                    xtype: 'panel',
                    itemId: 'bgSelectOrgan',
                    bodyStyle: 'background:#DFE8F6;background-image:url(../../img/background/logPrompt.jpg);background-repeat:no-repeat;background-position:center;'
                }]
            }]
        },{
            xtype: 'templateGridPreView'
        }, {
            xtpye: 'templateGridInfoView'
        }, {
            xtype:'templateFormInfoView'
        }]
    }, {
        title: '声像系统',
        itemId: 'sxxtId',
        layout: 'card',
        hidden:!openSxData,
        activeItem:0,
        items:[{
            itemId: 'sxgridview',
            layout:'border',
            items:[{
                itemId:'templateSxTreeViewItemID',
                width: XD.treeWidth,
                header:false,
                region: 'west',
                floatable: false,
                collapsible:true,
                rootVisible:false,
                split:1,
                layout: 'fit',
                items: [{xtype:'templateSxTreeView'}]
            },{
                itemId: 'templateSxPromptViewID',
                collapsible: false,
                region: 'center',
                layout:'card',
                activeItem:1,
                items: [{
                    xtype: 'TemplateTableView',
                    itemId:'templateTable'
                },{
                    xtype: 'panel',
                    itemId: 'bgSelectSxOrgan',
                    bodyStyle: 'background:#DFE8F6;background-image:url(../../img/background/logPrompt.jpg);background-repeat:no-repeat;background-position:center;'
                }]
            }]
        },{
            xtype: 'templateGridPreView'
        }, {
            xtpye: 'templateGridInfoView'
        }, {
            xtype:'templateFormInfoView'
        }]
    }]

});