Ext.define('Acquisition.view.AcquisitionSelectWin', {
    extend: 'Ext.window.Window',
    xtype: 'acquisitionSelectWin',
    itemId:'acquisitionSelectWinId',
    title: '设置功能权限',
    frame: true,
    closeToolText:'关闭',
    modal:true,
    layout: {
        type: 'fit',
        align: 'stretch'
    },

    items: [{
        xtype:'acquisitionSelectView'
    }],

    buttons: [
        { text: '字段设置',itemId:'setField'},
        { text: '关闭',itemId:'close'}
    ]
});