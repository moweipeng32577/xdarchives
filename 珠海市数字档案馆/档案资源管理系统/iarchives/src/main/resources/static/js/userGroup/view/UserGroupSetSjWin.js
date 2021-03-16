/**
 * Created by Administrator on 2017/10/23 0023.
 */

Ext.define('UserGroup.view.UserGroupSetSjWin', {
    extend: 'Ext.window.Window',
    xtype: 'userGroupSetSjWin',
    itemId:'userGroupSetSjWinId',
    title: '设置数据权限 (右键--快捷操作)',
    frame: true,
    width: 350,
    height: '75%',
    modal:true,
    closeToolText:'关闭',
    layout: 'border',
    items: [{
        region: 'center',
        xtype: 'userGroupSetSjView'
    }, {
        height: 32,
        region: 'south',
        xtype: 'panel',
        layout: 'fit',
        items: [{
            region: 'south',
            xtype: 'button',
            itemId: 'selectedCountItem',
            text: ''
        }]
    }],

    buttons: [
        { text: '提交',itemId:'userGroupSetSjSubmit'},
        { text: '关闭',itemId:'userGroupSetSjWinClose'}
    ]
});