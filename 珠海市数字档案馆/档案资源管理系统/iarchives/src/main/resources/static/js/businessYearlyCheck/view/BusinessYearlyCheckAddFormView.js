/**
 * Created by Administrator on 2020/10/14.
 */



Ext.define('BusinessYearlyCheck.view.BusinessYearlyCheckAddFormView', {
    extend: 'Ext.window.Window',
    xtype: 'businessYearlyCheckAddFormView',
    itemId: 'businessYearlyCheckAddFormViewId',
    title: '新增',
    frame: true,
    resizable: true,
    closeToolText: '关闭',
    width: 700,
    minWidth: 700,
    height: 200,
    modal: true,
    layout: 'fit',
    items: [{
        xtype: 'form',
        layout: 'column',
        bodyPadding: 16,
        items: [{
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
        }, {
            columnWidth: 1,
            xtype: 'filefield',
            clearOnSubmit: false,
            name: 'source',
            buttonText: '上传',
            margin: '10 0 0 0',
            allowBlank: false,
            hideLabel: true
        }]
    }],

    buttons: [
        {text: '提交', itemId: 'addSubmit'},
        {text: '关闭', itemId: 'addClose'}
    ]
});
