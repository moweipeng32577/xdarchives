/**
 * Created by Administrator on 2017/10/23 0023.
 */

Ext.define('User.view.UserSetSjWin', {
    extend: 'Ext.window.Window',
    xtype: 'userSetSjWin',
    itemId:'userSetSjWinId',
    title: '设置数据权限 (右键--快捷操作)',
    frame: true,
    closeToolText:'关闭',
    modal:true,
    layout:'border',
    items: [{
        region: 'center',
        itemId:'userSetSjViewId',
        xtype: 'userSetSjView'
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
        { text: '提交',itemId:'userSetSjSubmit'},
        { text: '关闭',itemId:'userSetSjWinClose'}
    ]
});