/**
 * Created by Administrator on 2019/12/14.
 */


Ext.define('UserGroup.view.LookUserGroupAddView', {
    extend: 'Ext.window.Window',
    xtype: 'lookUserGroupAddView',
    itemId:'lookUserGroupAddViewId',
    title: '设置组内用户',
    width:800,
    height:500,
    bodyPadding: 20,
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
        store: 'UserGroupSelectStore',
        displayField: 'realname',
        valueField: 'userid',
        allowBlank: false,
        msgTarget: 'side',
        fromTitle: '可选用户(按Ctrl+F查找)',
        toTitle: '组内用户'
    }],
    buttons: [
        { text: '提交',itemId:'addSubmit'},
        { text: '关闭',itemId:'addClose'}
    ]
});