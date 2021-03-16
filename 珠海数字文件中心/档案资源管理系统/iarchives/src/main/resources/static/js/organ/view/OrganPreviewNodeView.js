/**
 * Created by tanly on 2018/05/30 0002.
 */

Ext.define('Organ.view.OrganPreviewNodeView', {
    extend: 'Ext.window.Window',
    xtype: 'organPreviewNodeView',
    itemId:'OrganPreviewNodeViewId',
    title: '预览数据节点设置',
    width: 750,
    height: 750,
    modal:true,
    closeToolText:'关闭',
    frame: true,
    layout: 'fit',
    items:[{
        xtype: 'organPreviewNodeTabView',
        itemId: 'previewTreepanelId'
        /*store: 'OrganPreviewTreeStore',
        collapsible: true,
        split: 1,
        header: false,
        bodyBorder: false*/
    }]
    ,
    buttons: [{
        text: '提交',
        itemId:'saveBtn'
    },{
        text: '取消',
        itemId:'cancelBtn'
    }
    ]
});
