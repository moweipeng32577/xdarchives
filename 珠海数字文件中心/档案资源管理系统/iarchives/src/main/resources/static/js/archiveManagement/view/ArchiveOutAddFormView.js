/**
 * Created by Administrator on 2019/2/19.
 */
var genderStore = Ext.create("Ext.data.Store", {
    fields: ["Name", "Value"],
    data: [
        { Name: "男", Value: '男'},
        { Name: "女", Value: '女'}
    ]
});
var infodateStore = Ext.create("Ext.data.Store", {
	fields: ["Name", "Value"],
    data: [
        { Name: "一天", Value: '一天'},
        { Name: "一周", Value: '一周'},
        { Name: "一月", Value: '一月'}
    ]
});
Ext.define('ArchiveManagement.view.ArchiveOutAddFormView', {
    extend: 'Ext.window.Window',
    xtype: 'archiveOutAddFormView',
    itemId:'archiveOutAddFormViewId',
    title: '增加外来人员用户',
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
            { fieldLabel: '身份证号',name:'loginname',allowBlank: false,
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
            {  
            	xtype: "combobox",
                name: "sex",
                fieldLabel: "性别",
                store: genderStore,
                editable: false,
                displayField: "Name",
                valueField: "Value",
                queryMode: "local"
            },
            { 
            	xtype: "combobox",
                name: "infodate",
                fieldLabel: "有效期",
                store: infodateStore,
                editable: false,
                displayField: "Name",
                valueField: "Value",
                queryMode: "local",
                value: '一月'
            },
            { fieldLabel: '介绍信编号',name:'letternumber'},
            { xtype: 'textarea',fieldLabel: '备注',name:'remark'},
            { fieldLabel: '单位',name:'organid',readOnly:true,hidden:true}
//            {
//                xtype: 'radiogroup',
//                fieldLabel: '管理平台用户',
//                items:[{
//                    boxLabel: '是',
//                    name: 'usertype',
//                    inputValue: '1'
//                },{
//                    xtype:'displayfield'
//                },{
//                    boxLabel: '否',
//                    name: 'usertype',
//                    inputValue: '0',
//                    checked:'true'
//                }]
//            }
        ]
    }],

    buttons: [
        { text: '提交',itemId:'userOutAddSubmit'},
        { text: '关闭',itemId:'userOutAddClose'}
    ]
});