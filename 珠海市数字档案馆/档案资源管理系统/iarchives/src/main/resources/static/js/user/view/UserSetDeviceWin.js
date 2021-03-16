/**
 * Created by Administrator on 2017/10/23 0023.
 */

Ext.define('User.view.UserSetDeviceWin', {
    extend: 'Ext.window.Window',
    xtype: 'userSetDeviceWin',
    itemId:'userSetDeviceWinId',
    title: '设备权限设置',
    frame: true,
    //resizable: true,
    closeToolText:'关闭',
    modal:true,
    layout: {
        type: 'fit',
        align: 'stretch'
    },

    items: [{
        xtype:'userSetDeviceView'
    }],

    buttons: [
        { text: '提交',itemId:'userSetDeviceSubmit'},
        { text: '关闭',itemId:'userSetDeviceWinClose'}
    ]
});