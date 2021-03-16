/**
 * Created by Administrator on 2017/10/23 0023.
 */

Ext.define('UserGroup.view.UserGroupSetGnWin', {
    extend: 'Ext.window.Window',
    xtype: 'userGroupSetGnWin',
    itemId:'userGroupSetGnWinId',
    title: '设置功能权限',
    frame: true,
    //resizable: true,
    width: 350,
    height: '75%',
    modal:true,
    closeToolText:'关闭',
    layout: {
        type: 'fit',
        align: 'stretch'
    },

    items: [{
        xtype:'userGroupSetGnView'
    }],

    buttons: [
        { text: '提交',itemId:'userGroupSetGnSubmit'},
        { text: '关闭',itemId:'userGroupSetGnWinClose'}
    ]
});