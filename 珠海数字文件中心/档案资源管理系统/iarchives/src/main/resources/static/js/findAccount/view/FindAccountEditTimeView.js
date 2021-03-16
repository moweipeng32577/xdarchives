/**
 * Created by yl on 2019/7/9.
 */
Ext.define('FindAccount.view.FindAccountEditTimeView', {
    extend: 'Ext.window.Window',
    xtype: 'findAccountEditTimeView',
    itemId:'findAccountEditTimeViewId',
    title: '调整到期时间',
    frame: true,
    resizable: true,
    closeToolText:'关闭',
    width: 390,
    minWidth: 310,
    minHeight: 150,
    modal:true,
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
        margin: '15',
        items: [
            { fieldLabel: '',name:'userid',hidden:true},
            {
                fieldLabel: '到期时间',
                xtype: 'datefield',
                name: 'exdate',
                format: 'Y-m-d',
                allowBlank: true,
                editable: false
            }
        ]
    }],

    buttons: [
        { text: '保存',itemId:'ExpireDateSubmit'},
        { text: '取消',itemId:'ExpireDateClose'}
    ]
});