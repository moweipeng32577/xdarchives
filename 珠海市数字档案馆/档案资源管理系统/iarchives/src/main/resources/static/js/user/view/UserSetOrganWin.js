/**
 * Created by tanly on 2018/04/21 0023.
 */

Ext.define('User.view.UserSetOrganWin', {
    extend: 'Ext.window.Window',
    xtype: 'userSetOrganWin',
    itemId:'userSetOrganWinId',
    title: '设置机构权限',
    frame: true,
    closeToolText:'关闭',
    closeAction:'hide',
    modal:true,
    layout: {
        type: 'fit',
        align: 'stretch'
    },

    items: [{
        xtype:'userSetOrganView'
    }],

    buttons: [
        { text: '提交',itemId:'userSetOrganSubmit'},
        { text: '关闭',itemId:'userSetOrganWinClose'}
    ]
});