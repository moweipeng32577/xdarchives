/**
 * Created by Administrator on 2017/10/23 0023.
 */

Ext.define('User.view.UserSetAreaWin', {
    extend: 'Ext.window.Window',
    xtype: 'userSetAreaWin',
    itemId:'userSetAreaWinId',
    title: '区域权限设备',
    frame: true,
    //resizable: true,
    closeToolText:'关闭',
    modal:true,
    layout: {
        type: 'fit',
        align: 'stretch'
    },

    items: [{
        xtype:'userSetAreaView'
    }],

    buttons: [
        { text: '提交',itemId:'userSetAreaSubmit'},
        { text: '关闭',itemId:'userSetAreaWinClose'}
    ]
});