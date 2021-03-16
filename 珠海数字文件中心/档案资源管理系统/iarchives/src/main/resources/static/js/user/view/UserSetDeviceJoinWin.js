/**
 * Created by Administrator on 2017/10/23 0023.
 */

Ext.define('User.view.UserSetDeviceJoinWin', {
    extend: 'Ext.window.Window',
    xtype: 'userSetDeviceJoinWin',
    itemId:'userSetDeviceJoinWinId',
    title: '设备接入权限设置',
    frame: true,
    //resizable: true,
    closeToolText:'关闭',
    modal:true,
    layout: {
        type: 'fit',
        align: 'stretch'
    },

    items: [{
        xtype:'userSetDeviceJoinView'
    }],

    buttons: [
        { text: '提交',itemId:'userSetDeviceJoinSubmit'},
        { text: '关闭',itemId:'userSetDeviceJoinWinClose'}
    ]
});