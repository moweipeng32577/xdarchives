/**
 * Created by Administrator on 2019/9/20.
 */

Ext.define('DigitalProcess.view.ShUploadView', {
    extend: 'Ext.window.Window',
    xtype: 'ShUploadView',
    itemId:'ShUploadViewId',
    title: '上传文件',
    frame: true,
    resizable: true,
    closeToolText:'关闭',
    width: 500,
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
                {
                    xtype: 'filefield',
                    clearOnSubmit:false,
                    name:'source',
                    buttonText:'选择',
                    allowBlank:false,
                    hideLabel: true,
                    allowBlank: false
                }
            ]
        },

    ],

    buttons: [
        { text: '提交',itemId:'submit'},
        { text: '关闭',itemId:'close'}
    ]
});
