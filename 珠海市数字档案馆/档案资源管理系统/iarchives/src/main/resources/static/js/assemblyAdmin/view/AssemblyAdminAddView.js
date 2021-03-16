/**
 * Created by Administrator on 2019/7/3.
 */


var textTpl = ['<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'];
Ext.define('AssemblyAdmin.view.AssemblyAdminAddView', {
    extend: 'Ext.window.Window',
    xtype: 'assemblyAdminAddView',
    itemId:'assemblyAdminAddViewId',
    title: '新增流水线',
    frame: true,
    resizable: true,
    closeToolText:'关闭',
    width: 610,
    minWidth: 610,
    minHeight: 100,
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

    items: [
        {
            xtype: 'form',
            modelValidation: true,
            margin: '15',
            items: [
                { fieldLabel: '流水线名',name:'title',allowBlank: false, afterLabelTextTpl: textTpl},
                { xtype: 'textarea',fieldLabel: '备注',name:'remark',allowBlank: true}
            ]
        }
    ],

    buttons: [
        { text: '提交',itemId:'assemblyAddSubmit'},
        { text: '关闭',itemId:'assemblyAddClose',handler:function (btn) {
            btn.up('assemblyAdminAddView').close();
        }}
    ]
});
