/**
 * Created by Administrator on 2017/10/23 0023.
 */
Ext.define('Restitution.view.XjDescAddView', {
    extend: 'Ext.window.Window',
    xtype: 'xjDescAddView',
    itemId: 'xjDescAddView',
    title: '续查信息',
    frame: true,
    resizable: true,
    closeToolText: '关闭',
    width: 520,
    height:310,
    modal: true,
    layout: 'fit',
    items: [{
        xtype: 'form',
        layout: {
            type: 'vbox',
            align: 'stretch'
        },
        bodyPadding: 15,
        itemId: 'formId',
        items: [{
            labelWidth: 60,
            xtype: 'numberfield',
            fieldLabel: "天数",
            name: 'jyts'
        }, {
            labelWidth: 60,
            itemId: 'approveId',
            xtype: 'textarea',
            fieldLabel: '理由',
            name: 'renewreason',
            height:145
        }]
    }],

    buttons: [
        {text: '完成', itemId: 'xjAddSubmit'},
        {text: '关闭', itemId: 'xjAddClose'}
    ]
});