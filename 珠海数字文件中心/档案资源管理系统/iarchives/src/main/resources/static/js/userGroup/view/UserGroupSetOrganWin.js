/**
 * Created by tanly on 2018/4/23 0023.
 */

Ext.define('UserGroup.view.UserGroupSetOrganWin', {
    extend: 'Ext.window.Window',
    xtype: 'userGroupSetOrganWin',
    itemId: 'userGroupSetOrganWinId',
    title: '设置机构权限',
    frame: true,
    width: 400,
    height: 650,
    modal: true,
    closeToolText: '关闭',
    closeAction:'hide',
    layout: {
        type: 'fit',
        align: 'stretch'
    },
    items: [{
        xtype: 'userGroupSetOrganView'
    }],

    buttons: [
        {text: '提交', itemId: 'userGroupSetOrganSubmit'},
        {text: '关闭', itemId: 'userGroupSetOrganWinClose'}
    ]
});