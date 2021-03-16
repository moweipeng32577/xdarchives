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
Ext.define('FindAccount.view.UserAddForm', {
    extend: 'Ext.window.Window',
    xtype: 'userAddForm',
    itemId:'userAddFormId',
    title: '增加用户',
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
            { fieldLabel: '',name:'orders',hidden:true},
            { fieldLabel: '帐号',name:'loginname',allowBlank: false,
                // vtype:'alphanum',
                vtypeText  :'请输入2-30位字符，建议由数字、字母或中文字符组合命名',minLength :2,afterLabelTextTpl: [
                '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'
                ],
                listeners: {
                    render: function(sender) {
                        new Ext.ToolTip({
                            target: sender.el,
                            trackMouse: true,
                            dismissDelay: 0,
                            anchor: 'buttom',
                            html: '请输入2-30位字符，建议由数字、字母或中文字符组合命名'
                        });
                    }
                }
            },
            { fieldLabel: '用户姓名',name:'realname', allowBlank: false,
                afterLabelTextTpl: [
                    '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'
                ],
                listeners: {
                    render: function(sender) {
                        new Ext.ToolTip({
                            target: sender.el,
                            trackMouse: true,
                            dismissDelay: 0,
                            anchor: 'buttom',
                            html: '请输入用户姓名'
                        });
                    }
                }},
            { fieldLabel: '电话',name:'phone'},
            { fieldLabel: '地址',name:'address' },
            {  xtype: "combobox",
                name: "sex",
                fieldLabel: "性别",
                store: genderStore,
                editable: false,
                displayField: "Name",
                valueField: "Value",
                queryMode: "local"
            },
            { fieldLabel: '单位',name:'organid',readOnly:true,hidden:true},
            {
                xtype: 'radiogroup',
                fieldLabel: '管理平台用户',
                hidden:false,
                items:[{
                    boxLabel: '是',
                    name: 'usertype',
                    inputValue: '1',
                    checked:'true'
                }
                ,{
                    xtype:'displayfield'
                },{
                    boxLabel: '否',
                    name: 'usertype',
                    inputValue: '0'
                }
                ],
                listeners: {
                    beforerender: function (view) {
                        Ext.Ajax.request({
                            url: '/jyAdmins/getplatformopen',
                            method: 'get',
                            async:false,
                            success: function (resp) {
                                var respText = Ext.decode(resp.responseText);
                                if(respText.data=='false'){
                                    view.hidden=true;
                                }
                            }
                        });
                    }
                }
            }
        ]
    }],

    buttons: [
        { text: '保存',itemId:'userAddSubmit'},
        { text: '关闭',itemId:'userAddClose'}
    ]
});