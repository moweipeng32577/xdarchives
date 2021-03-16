/**
 * Created by tanly on 2018/2/9 0009.
 */
Ext.define('DuplicateChecking.view.DuplicateCheckingExportView', {
    extend: 'Ext.window.Window',
    xtype: 'duplicateCheckingExportView',
    width: 500,
    layout: 'fit',
    resizable: false,
    modal: true,
    title: '导出excel报表',
    closeToolText: '关闭',
    items: [{
        xtype: 'textfield',
        fieldLabel: 'Excel名称',
        itemId: 'filename',
        margin: '20'
    }],
    buttons: [{
        text: '导出',
        itemId: 'export'
    }, {
        text: '关闭',
        itemId: 'close'
    }]
});