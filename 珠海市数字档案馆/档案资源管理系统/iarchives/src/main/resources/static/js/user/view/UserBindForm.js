/**
 * Created by Administrator on 2017/10/23 0023.
 */
var genderStore = Ext.create("Ext.data.Store", {
    fields: ["Name", "Value"],
    data: [
        { Name: "男", Value: '男' },
        { Name: "女", Value: '女'}
    ]
});
Ext.define('User.view.UserBindForm', {
    extend: 'Ext.window.Window',
    xtype: 'userBindForm',
    itemId:'userBindFormId',
    title: '绑定证书',
    frame: true,
    resizable: true,
    closeToolText:'关闭',
    width: 610,
    minWidth: 610,
    minHeight: 250,
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
            { fieldLabel: '帐号',name:'loginname',allowBlank: false,readOnly:true},
            { fieldLabel: '用户姓名',name:'realname', allowBlank: false,readOnly:true},
            { fieldLabel: '证书邦定值',name:'nickname',itemId:'caUserid',readOnly:true},
            { fieldLabel: '证书主体',itemId:'caUsername',name:'caUsername',readOnly:true },
            { fieldLabel: '证书BASE64编码',name:'cacode',itemId:'cacodeid',readOnly:true},
            { fieldLabel: '证书签章BASE64编码',name:'signcode',itemId:'signcodeid',readOnly:true},
        ]
    }],

    buttons: [
        { text: '提交',itemId:'userBindSubmit'},
        { text: '关闭',itemId:'userBindClose'}
    ]
});