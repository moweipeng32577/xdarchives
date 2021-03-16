/**
 * Created by tanly on 2018/05/21 0023.
 */
Ext.define('User.view.UserAddAdmin', {
    extend: 'Ext.window.Window',
    xtype: 'userAddAdmin',
    itemId: 'userAddAdminId',
    title: '设置三员信息',
    frame: true,
    resizable: true,
    closeToolText: '关闭',
    width: 610,
    minWidth: 610,
    minHeight: 250,
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
        margin: '15',
        items: [{
            fieldLabel: '安全保密管理员帐号',
            name: 'secretAdmin',
            value : 'aqbm_',
            allowBlank: false,
            vtypeText: '请输入2-30位字符，建议由数字、字母或中文字符组合命名', minLength: 2, afterLabelTextTpl: [
                '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'
            ],
            listeners: {
                render: function (sender) {
                    new Ext.ToolTip({
                        target: sender.el,
                        trackMouse: true,
                        dismissDelay: 0,
                        anchor: 'buttom',
                        html: '请输入2-30位字符，建议由数字、字母或中文字符组合命名'
                    });
                }
            }
       }, {
        	fieldLabel: '安全保密管理员名称',
            name: 'aqbmName'
        }, {
            fieldLabel: '系统管理员帐号',
            name: 'systemAdmin',
            value : 'xitong_',
            allowBlank: false,
            vtypeText: '请输入2-30位字符，建议由数字、字母或中文字符组合命名', minLength: 2, afterLabelTextTpl: [
                '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'
            ],
            listeners: {
                render: function (sender) {
                    new Ext.ToolTip({
                        target: sender.el,
                        trackMouse: true,
                        dismissDelay: 0,
                        anchor: 'buttom',
                        html: '请输入2-30位字符，建议由数字、字母或中文字符组合命名'
                    });
                }
            }
        }, {
        	fieldLabel: '系统管理员名称',
            name: 'xitongName'
        }, {
            fieldLabel: '安全审计员帐号',
            name: 'auditor',
            value : 'aqsj_',
            allowBlank: false,
            vtypeText: '请输入2-30位字符，建议由数字、字母或中文字符组合命名', minLength: 2, afterLabelTextTpl: [
                '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'
            ],
            listeners: {
                render: function (sender) {
                    new Ext.ToolTip({
                        target: sender.el,
                        trackMouse: true,
                        dismissDelay: 0,
                        anchor: 'buttom',
                        html: '请输入2-30位字符，建议由数字、字母或中文字符组合命名'
                    });
                }
            }
        }, {
        	fieldLabel: '安全审计员名称',
            name: 'aqsjName'
        }]
    }],

    buttons: [
        {text: '提交', itemId: 'addSubmit'},
        {text: '关闭', itemId: 'close'}
    ]
});