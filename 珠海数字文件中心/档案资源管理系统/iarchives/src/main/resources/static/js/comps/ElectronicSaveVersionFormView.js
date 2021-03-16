/**
 * Created by Administrator on 2019/2/21.
 */

var textTpl = ['<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'];
Ext.define('Comps.view.ElectronicSaveVersionFormView', {
    extend: 'Ext.window.Window',
    xtype: 'electronicSaveVersionFormView',
    itemId:'electronicSaveVersionFormViewId',
    title: '保存版本',
    frame: true,
    resizable: true,
    closeToolText:'关闭',
    width: 610,
    minWidth: 610,
    modal:true,
    items: [
        {
            xtype: 'form',
            layout:'form',
            items: [
                {xtype: 'textfield', fieldLabel: '版本号',name:'version',allowBlank: false, afterLabelTextTpl: textTpl},
                {xtype: 'textarea', fieldLabel: '备注',name:'remark',allowBlank: false}
            ]
        }
    ],

    buttons: [
        { text: '下一步',itemId:'saveversion'},
        { text: '关闭',itemId:'versionclose'}
    ]
});
