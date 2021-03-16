/**
 * Created by Administrator on 2017/10/23 0023.
 */

Ext.define('User.view.UserSetGnWin', {
    extend: 'Ext.window.Window',
    xtype: 'userSetGnWin',
    itemId:'userSetGnWinId',
    title: '设置功能权限',
    frame: true,
    //resizable: true,
    closeToolText:'关闭',
    modal:true,
    layout: {
        type: 'fit',
        align: 'stretch'
    },

    items: [{
        xtype:'userSetGnView'
    }],

    buttons: [
        { text: '提交',itemId:'userSetGnSubmit'},
        { text: '关闭',itemId:'userSetGnWinClose'}
    ]
});