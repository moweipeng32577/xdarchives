/**
 * Created by Administrator on 2017/10/23 0023.
 */
Ext.define('JyAdmins.view.XjDescAddView', {
    extend: 'Ext.window.Window',
    xtype: 'xjDescAddView',
    itemId: 'xjDescAddView',
    title: '续借信息',
    frame: true,
    resizable: true,
    closeToolText: '关闭',
    width: 500,
    minWidth: 500,
    minHeight: 230,
    modal: true,
    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    defaults: {
        layout: 'form',
        xtype: 'container',
        defaultType: 'textfield',
        style: 'width: 50%'
    },
    items: [{
        xtype: 'form',
        modelValidation: true,
        itemId: 'formId',
        margin: '15',
        items: [{
            xtype: 'numberfield',
            fieldLabel: "天数",
            vtype: 'numeric'
        }, {
            columnWidth: 1,
            itemId: 'approveId',
            xtype: 'textarea',
            fieldLabel: '理由',
            name: 'desci',
            flex: 1
        }]
    }],

    buttons: [
        {text: '完成', itemId: 'xjAddSubmit'},
        {text: '关闭', itemId: 'xjAddClose'}
    ]
});