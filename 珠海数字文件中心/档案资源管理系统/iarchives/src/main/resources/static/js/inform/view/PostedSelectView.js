/**
 * Created by Administrator on 2017/10/23 0023.
 */
Ext.define('Inform.view.PostedSelectView', {
    extend: 'Ext.window.Window',
    xtype: 'postedSelectView',
    itemId:'postedSelectViewId',
    title: '发布',
    width:600,
    height:500,
    bodyPadding: 5,
    layout:'hbox',
    modal:true,
    closeToolText:'关闭',
    items:[{
        flex:1,
        height:'100%',
        xtype:'organTreeView'
    },{
        flex:2,
        height:'100%',
        xtype: 'itemselector',
        imagePath: '../ux/images/',
        store: 'PostedSelectStore',
        displayField: 'text',
        valueField: 'fnid',
        allowBlank: false,
        msgTarget: 'side',
        fromTitle: '可选(按Ctrl+F查找)',
        toTitle: '已选'
    }],
    buttons: [
        { text: '全选',itemId:'allOrNotSelect'},
        { text: '提交',itemId:'postedSelectSubmit'},
        { text: '关闭',itemId:'postedSelectClose'}
    ]
});