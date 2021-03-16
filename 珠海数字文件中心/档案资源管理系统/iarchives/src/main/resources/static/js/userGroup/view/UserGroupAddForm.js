/**
 * Created by Administrator on 2017/10/23 0023.
 */
Ext.define('UserGroup.view.UserGroupAddForm', {
    extend: 'Ext.window.Window',
    xtype: 'userGroupAddForm',
    itemId:'userGroupAddFormId',
    title: '增加用户组',
    frame: true,
    resizable: true,
    width: 410,
    minWidth: 410,
    minHeight: 100,
    modal:true,
    closeToolText:'关闭',
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
            { fieldLabel: '',name:'roleid',hidden:true},
            { fieldLabel: '用户组名',name:'rolename',allowBlank: false,afterLabelTextTpl: [
                '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'
            ]},
            { fieldLabel: '描述',name:'desciption'}
        ]
    }],

    buttons: [
        { text: '提交',itemId:'userGroupAddSubmit'},
        { text: '关闭',itemId:'userGroupAddClose'}
    ]
});