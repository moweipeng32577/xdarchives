/**
 * Created by Administrator on 2017/10/23 0023.
 */
Ext.define('User.view.UserSetWjWin', {
    extend: 'Ext.window.Window',
    xtype: 'userSetWjWin',
    itemId:'userSetWjWinId',
    title: '设备文件权限',
    frame: true,
    //resizable: true,
    closeToolText:'关闭',
    modal:true,
    layout: {
        type: 'fit',
        align: 'stretch'
    },

    items: [{
        xtype:'userSetWjView'
    }],

    buttons: [
        { text: '提交',itemId:'userSetWjSubmit'},
        { text: '关闭',itemId:'userSetWjClose'}
    ]
});