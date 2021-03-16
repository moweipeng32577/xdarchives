/**
 * Created by Administrator on 2019/10/26.
 */


Ext.define('TransforAuditDeal.view.DuplicateCheckingSelectView', {
    extend: 'Ext.window.Window',
    xtype: 'duplicateCheckingSelectView',
    title: '查重字段',
    width:'40%',
    height:'90%',
    bodyPadding: '15 40 15 40',
    layout: 'fit',
    modal: true,
    closeToolText: '关闭',
    scrollable:true,
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
