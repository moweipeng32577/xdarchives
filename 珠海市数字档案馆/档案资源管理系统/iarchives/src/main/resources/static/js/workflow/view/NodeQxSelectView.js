/**
 * Created by Administrator on 2017/10/23 0023.
 */
Ext.define('Workflow.view.NodeQxSelectView', {
    extend: 'Ext.window.Window',
    xtype: 'nodeQxSelectView',
    itemId:'nodeQxSelectViewId',
    title: '设置环节权限',
    width:600,
    height:500,
    bodyPadding: 20,
    layout:'fit',
    modal:true,
    closeToolText:'关闭',
    items:[{
        xtype: 'itemselector',
        anchor: '100%',
        imagePath: '../ux/images/',
        store: 'NodeQxSelectStore',
        displayField: 'text',
        valueField: 'id',
        allowBlank: false,
        msgTarget: 'side',
        fromTitle: '可选权限(按Ctrl+F查找)',
        toTitle: '已选权限'
    }],
    buttons: [
        { text: '提交',itemId:'nodeQxSelectSubmit'},
        { text: '关闭',itemId:'nodeQxSelectClose'}
    ]
});