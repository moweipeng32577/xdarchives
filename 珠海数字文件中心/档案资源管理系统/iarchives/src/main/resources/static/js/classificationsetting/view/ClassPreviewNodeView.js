/**
 * Created by tanly on 2018/05/30 0002.
 */

Ext.define('Classificationsetting.view.ClassPreviewNodeView', {
    extend: 'Ext.window.Window',
    xtype: 'classPreviewNodeView',
    itemId:'ClassPreviewNodeViewId',
    title: '预览数据节点设置',
    width: 750,
    height: 750,
    modal:true,
    closeToolText:'关闭',
    frame: true,
    layout: 'fit',
    items:[{
        xtype: 'treepanel',
        itemId: 'previewTreepanelId',
        store: 'ClassificationsettingPreviewTreeStore',
        collapsible: true,
        split: 1,
        header: false,
        bodyBorder: false
    }]
    ,
    buttons: [{
        text: '提交',
        itemId:'classSaveBtnID'
    },{
        text: '取消',
        itemId:'classCancelBtnID'
    }
    ]
});
