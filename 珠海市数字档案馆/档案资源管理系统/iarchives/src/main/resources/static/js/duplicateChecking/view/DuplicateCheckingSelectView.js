/**
 * Created by tanly on 2018/2/5 0005.
 */
Ext.define('DuplicateChecking.view.DuplicateCheckingSelectView', {
    extend: 'Ext.window.Window',
    xtype: 'duplicateCheckingSelectView',
    title: '查重字段',
    width: 600,
    height: 550,
    bodyPadding: '15 40 15 40',
    layout: 'fit',
    modal: true,
    closeToolText: '关闭',
    items: [{
        xtype: 'itemselector',
        itemId: 'itemselectorID',
        anchor: '100%',
        imagePath: '../ux/images/',
        store: 'DuplicateCheckingSelectStore',
        displayField: 'fieldname',
        valueField: 'fieldcode',
        allowBlank: false,
        msgTarget: 'side',
        fromTitle: '可选字段(按Ctrl+F查找)',
        toTitle: '已选字段'
    }],
    buttons: [
        {text: '提交', itemId: 'submit'},
        {text: '关闭', itemId: 'close'}
    ]
});