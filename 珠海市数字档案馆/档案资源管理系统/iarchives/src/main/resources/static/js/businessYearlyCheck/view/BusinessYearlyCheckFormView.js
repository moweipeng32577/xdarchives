/**
 * Created by Administrator on 2020/10/14.
 */


Ext.define('BusinessYearlyCheck.view.BusinessYearlyCheckFormView', {
    extend: 'Ext.window.Window',
    xtype: 'businessYearlyCheckFormView',
    itemId: 'businessYearlyCheckFormViewId',
    title: '',
    frame: true,
    resizable: true,
    closeToolText: '关闭',
    width: 700,
    minWidth: 700,
    height: 150,
    modal: true,
    layout: 'fit',
    items: [{
        xtype: 'form',
        layout: 'column',
        bodyPadding: 16,
        items: [{
            xtype: 'textfield',
            fieldLabel: '',
            name: 'id',
            hidden: true
        },{
            columnWidth: .47,
            xtype: 'textfield',
            itemId: 'selectyearId',
            fieldLabel: '年度',
            name: 'selectyear',
            allowBlank: false,
            labelWidth: 60
        }, {
            columnWidth: .06,
            xtype: 'displayfield'
        }, {
            columnWidth: .47,
            xtype: 'textfield',
            itemId: 'titleId',
            fieldLabel: '题名',
            allowBlank: false,
            name: 'title',
            labelWidth: 60
        },{
            xtype: 'textfield',
            fieldLabel: '',
            name: 'state',
            hidden: true
        }]
    }],

    buttons: [
        {text: '保存', itemId: 'saveSubmit'},
        {text: '关闭', itemId: 'saveClose'}
    ]
});
