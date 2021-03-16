/**
 * Created by Administrator on 2018/12/3.
 */

Ext.define('CheckGroup.view.CheckGroupAddUserView', {
    extend: 'Ext.window.Window',
    xtype: 'checkGroupAddUserView',
    itemId:'checkGroupAddUserViewid',
    title: '设置质检组人员',
    width:800,
    height:500,
    bodyPadding: 20,
    layout:'hbox',
    modal:true,
    closeToolText:'关闭',
    items:[{
        flex:1,
        height:'100%',
        xtype:'checkGroupOrganTreeView'
    },{
        flex:2,
        height:'100%',
        xtype: 'itemselector',
        imagePath: '../ux/images/',
        store: 'CheckGroupUserSelectStore',
        displayField: 'realname',
        valueField: 'userid',
        allowBlank: false,
        msgTarget: 'side',
        fromTitle: '可选用户(按Ctrl+F查找)',
        toTitle: '已选用户'
    }],
    buttons: [
        { text: '提交',itemId:'userSelectSubmit'},
        { text: '关闭',itemId:'userSelectClose'}
    ]
});
