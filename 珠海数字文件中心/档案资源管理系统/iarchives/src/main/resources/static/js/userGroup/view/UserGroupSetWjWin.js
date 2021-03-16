/**
 * Created by Administrator on 2017/10/23 0023.
 */
Ext.define('UserGroup.view.UserGroupSetWjWin', {
    extend: 'Ext.window.Window',
    xtype: 'userGroupSetWjWin',
    itemId:'userGroupSetWjWinId',
    title: '设备文件权限',
    frame: true,
    width: 350,
    height: '75%',
    closeToolText:'关闭',
    modal:true,
    layout: {
        type: 'fit',
        align: 'stretch'
    },

    items: [{
        xtype:'userGroupSetWjView'
    }],

    buttons: [
        { text: '提交',itemId:'userGroupSetWjSubmit'},
        { text: '关闭',itemId:'userGroupSetWjClose'}
    ]
});