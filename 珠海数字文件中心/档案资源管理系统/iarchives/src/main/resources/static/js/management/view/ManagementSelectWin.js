Ext.define('Management.view.ManagementSelectWin', {
    extend: 'Ext.window.Window',
    xtype: 'managementSelectWin',
    itemId:'managementSelectWinId',
    title: '设置功能权限',
    frame: true,
    closeToolText:'关闭',
    modal:true,
    layout: {
        type: 'fit',
        align: 'stretch'
    },

    items: [{
        xtype:'managementSelectView'
    }],

    buttons: [
        { text: '字段设置',itemId:'setField'},
        { text: '关闭',itemId:'close'}
    ]
});