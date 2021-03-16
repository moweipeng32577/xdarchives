/**
 * Created by Administrator on 2019/2/19.
 */
var genderStore = Ext.create("Ext.data.Store", {
    fields: ["Name", "Value"],
    data: [
        { Name: "男", Value: '男' },
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
Ext.define('FindAccount.view.FindAccountOutAddFormView', {
    extend: 'Ext.window.Window',
    xtype: 'findAccountOutAddFormView',
    itemId:'findAccountOutAddFormViewId',
    title: '增加外来人员用户',
    frame: true,
    resizable: true,
    closeToolText:'关闭',
    autoScroll : true,
    width:'40%',
    height:'75%',
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
            {
                fieldLabel: '身份证号',
                name:'loginname',
                itemId: "certificatenumber",
                allowBlank: false,regex:/(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/,
                regexText   : "身份证号码格式不正确！",
                vtypeText  :'请输入身份证号码',minLength :3,afterLabelTextTpl: ['<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>']
                // listeners: {
                //     render: function(sender) {
                //         new Ext.ToolTip({
                //             target: sender.el,
                //             trackMouse: true,
                //             dismissDelay: 0,
                //             anchor: 'buttom',
                //             html: '请输入3-30位字符，建议由数字、字母或中文字符组合命名'
                //         });
                //     }
                // }
            },
            {
                fieldLabel: '用户姓名',
                name: 'realname',
                allowBlank: false,
                itemId: "borrowmanId",
                afterLabelTextTpl: [
                    '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'
                ],
                listeners: {
                    render: function (sender) {
                        new Ext.ToolTip({
                            target: sender.el,
                            trackMouse: true,
                            dismissDelay: 0,
                            anchor: 'buttom',
                            html: '请输入用户姓名'
                        });
                    }
                }
            },
            {
                xtype: "combobox",
                name: "sex",
                itemId: "sex",
                fieldLabel: "性别",
                store: genderStore,
                editable: false,
                displayField: "Name",
                valueField: "Value",
                queryMode: "local"
            },
            {
                fieldLabel: '出生年月',
                xtype: 'datefield',
                name: 'birthday',
                itemId: "birthday",
                format: 'Ymd',
                allowBlank: true,
                editable: false,
            },
            { fieldLabel: '民族',name:'ethnic', itemId: "ethnic"},
            { fieldLabel: '电话',name:'phone',itemId: "phone"},
            { fieldLabel: '地址',name:'address', itemId: "address"},
            { 
            	xtype: "combobox",
                name: "infodate",
                fieldLabel: "有效期",
                store: infodateStore,
                editable: false,
                displayField: "Name",
                valueField: "Value",
                queryMode: "local",
                value: '一周'
            },
            { fieldLabel: '介绍信编号',name:'letternumber'},
            { xtype: 'textarea',fieldLabel: '备注',name:'remark'},
            { fieldLabel: '单位',name:'organid',readOnly:true,hidden:true},
           {
               xtype: 'radiogroup',
               fieldLabel: '管理平台用户',
               hidden:true,
               items:[{
                   boxLabel: '是',
                   name: 'usertype',
                   inputValue: '1'
               },{
                   xtype:'displayfield'
               },{
                   boxLabel: '否',
                   name: 'usertype',
                   inputValue: '0',
                   checked:'true'
               }]
           }
        ]
    }],

    buttons: [
        { text: '读取身份证',itemId:'userOutAddGetID',handler: function () {
            var result = CertCtl.getStatus();
            var str = JSON.parse(result);
            if (str.status == 0) {
            } else if (str.status == 1) {
                CertCtl.connect();
            }

            result = CertCtl.readCert();
            var resultObj = JSON.parse(result);
            if (resultObj.resultFlag == 0) {
                // Ext.get('PicFront').dom.src = "data:image/jpeg;base64," + resultObj.resultContent.base64ID_PicFront;
                // Ext.get('PicBack').dom.src = "data:image/jpeg;base64," + resultObj.resultContent.base64ID_PicBack;
                var gender = resultObj.resultContent.gender;//性别
                if (gender == 1) {
                    this.up('findAccountOutAddFormView').down('[itemId=sex]').setValue('男');
                } else {
                    this.up('findAccountOutAddFormView').down('[itemId=sex]').setValue('女');
                }
                this.up('findAccountOutAddFormView').down('[itemId=borrowmanId]').setValue(resultObj.resultContent.partyName); //姓名
                this.up('findAccountOutAddFormView').down('[itemId=certificatenumber]').setValue(resultObj.resultContent.certNumber);//身份证号
                this.up('findAccountOutAddFormView').down('[itemId=address]').setValue(resultObj.resultContent.certAddress);//地址
                this.up('findAccountOutAddFormView').down('[itemId=ethnic]').setValue(resultObj.resultContent.nation);//民族
                this.up('findAccountOutAddFormView').down('[itemId=birthday]').setValue(resultObj.resultContent.bornDay);//出生日期

            } else {
                Ext.Msg.alert('失败', resultObj.errorMsg);
            }
            CertCtl.disconnect();
        }},
        { text: '保存',itemId:'userOutAddSubmit'},
        { text: '取消',itemId:'userOutAddClose'}
    ]
});