/**
 * Created by Administrator on 2019/2/19.
 */


var certificateType = Ext.create("Ext.data.Store",{
    fields:["text","value"],
    data:[
        {text:"身份证",value:"身份证"},
        {text:"介绍信",value:"介绍信"},
        {text:"委托书",value:"委托书"}
    ]
});

var borrowcontentMode = Ext.create("Ext.data.Store",{
    fields:["text","value"],
    data:[
        {text:"婚姻",value:"婚姻"},
        {text:"退伍",value:"退伍"},
        {text:"文书",value:"文书"},
        {text:"其他",value:"其他"},
    ]
});

var lyMode = Ext.create("Ext.data.Store",{
    fields:["text","value"],
    data:[
        {text:"复制、摘抄",value:"复制、摘抄"}
    ]
});

var relationShipModel = Ext.create("Ext.data.Store",{
    fields:["text","value"],
    data:[
        {text:"夫妻",value:"夫妻"},
        {text:"委托人",value:"委托人"},
        {text:"公",value:"公"},
        {text:"检",value:"检"},
        {text:"法",value:"法"},
        {text:"安全部门",value:"安全部门"},
        {text:"受理案件",value:"受理案件"},
        {text:"婚姻登记部门",value:"婚姻登记部门"},
        {text:"其他",value:"其他"}
    ]
});

Ext.define('SimpleSearch.view.ElectronAddFormView', {
    extend: 'Ext.form.Panel',
    xtype: 'electronAddFormView',
    itemId: 'electronAddFormViewId',
    autoScroll: true,
    fieldDefaults: {
        labelWidth: 70
    },
    layout: 'column',
    bodyPadding: 16,
    items: [
        {xtype: 'textfield', name: 'id', hidden: true},
        {
            columnWidth: .3,
            xtype: 'label',
            text: '温馨提示：红色外框表示输入非法数据！',
            style: {
                color: 'red',
                'font-size': '16px'
            },
            margin: '10 0 0 0'
        }, {
            columnWidth: .6,
            xtype: 'displayfield'
        }, {
            columnWidth: .47,
            fieldLabel: '申办姓名',
            itemId:'borrowmanId',
            xtype: 'textfield',
            name: 'borrowman',
            labelWidth: 85,
            allowBlank: false,
            margin: '10 0 0 0',
            afterLabelTextTpl: [
                '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>']
        },{
            columnWidth:.06,
            xtype:'displayfield'
        },{
            columnWidth: .47,
            fieldLabel: '查档内容',
            xtype: 'tagfield',
            name: 'borrowcontent',
            labelWidth: 85,
            allowBlank: false,
            store: borrowcontentMode,
            reference: 'states',
            displayField: 'text',
            valueField: 'value',
            filterPickList: true,
            queryMode: 'local',
            publishes: 'value',
            margin: '10 0 0 0',
            afterLabelTextTpl: [
                '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>']
        },{
            columnWidth: .23,
            xtype: 'combo',
            itemId: 'certificatetypeId',
            store: certificateType,
            queryMode:'all',
            name: 'certificatetype',
            fieldLabel: '提供证件',
            labelWidth: 85,
            displayField: 'value',
            valueField: 'value',
            allowBlank: false,
            editable: false,
            margin: '10 0 0 0',
            listeners: {
                afterrender: function (combo) {
                    var store = combo.getStore();
                    if (store.getCount() > 0) {
                        combo.select(store.getAt(0));
                    }
                }
            }
        },{
            columnWidth:.01,
            xtype:'displayfield'
        },{
            columnWidth: .23,
            fieldLabel: '证件号码',
            xtype: 'textfield',
            name: 'certificatenumber',
            labelWidth: 85,
            allowBlank: false,
            margin: '10 0 0 0',
            regex:/(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/,
            regexText   : "身份证号码格式不正确！",
            afterLabelTextTpl: [
                '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>']
        },{
            columnWidth:.06,
            xtype:'displayfield'
        },{
            columnWidth: .47,
            xtype: 'textfield',
            itemId: 'borrowmdId',
            name: 'borrowmd',
            fieldLabel: '目的',
            labelWidth: 85,
            allowBlank: false,
            margin: '10 0 0 0',
            afterLabelTextTpl: [
                '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>']
        },{
            columnWidth: .47,
            fieldLabel: '地址',
            xtype: 'textfield',
            name: 'comaddress',
            labelWidth: 85,
            editable: true,
            allowBlank: false,
            margin: '10 0 0 0',
            afterLabelTextTpl: [
                '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>']
        }, {
            columnWidth:.06,
            xtype:'displayfield'
        },{
            columnWidth: .47,
            fieldLabel: '电话或者电子邮箱',
            xtype: 'textfield',
            name: 'borrowmantel',
            labelWidth: 85,
            allowBlank: false,
            margin: '10 0 0 0',
            regex :/(^1[0-9]{10}$)|(^\w+([-+.']\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$)/,
            regexText   : "电话或电子邮箱格式不正确！",
            afterLabelTextTpl: [
                '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>']
        },{
            columnWidth: .47,
            xtype: 'combo',
            itemId: 'relationshipId',
            store: relationShipModel,
            queryMode:'all',
            name: 'relationship',
            fieldLabel: '与当事人的关系',
            labelWidth: 85,
            displayField: 'value',
            valueField: 'value',
            allowBlank: false,
            editable: false,
            margin: '0 0 0 0',
            listeners: {
                afterrender: function (combo) {
                    var store = combo.getStore();
                    if (store.getCount() > 0) {
                        combo.select(store.getAt(0));
                    }
                }
            }
        }, {
            columnWidth:.06,
            xtype:'displayfield'
        },{
            columnWidth: .47,
            xtype: 'combo',
            itemId: 'lymodeId',
            store: lyMode,
            queryMode:'all',
            name: 'lymode',
            fieldLabel: '利用方式',
            labelWidth: 85,
            displayField: 'value',
            valueField: 'value',
            allowBlank: false,
            editable: false,
            margin: '10 0 0 0',
            listeners: {
                afterrender: function (combo) {
                    var store = combo.getStore();
                    if (store.getCount() > 0) {
                        combo.select(store.getAt(0));
                    }
                }
            }
        },{
            columnWidth: .23,
            fieldLabel: '申请时间',
            xtype: 'datefield',
            name: 'borrowdate',
            format: 'Ymd',
            labelWidth: 85,
            allowBlank: false,
            editable: false,
            margin:'10 0 0 0'
        }, {
            columnWidth:.01,
            xtype:'displayfield'
        },{
            columnWidth: .23,
            fieldLabel: '受理时间',
            xtype: 'textfield',
            name: 'acceptdate',
            labelWidth: 85,
            editable: false,
            margin:'10 0 0 0'
        },{
            columnWidth:.06,
            xtype:'displayfield'
        },{
            columnWidth: .23,
            xtype: 'numberfield',
            itemId:'borrowmantimeld',
            fieldLabel: '利用人数',
            labelWidth: 85,
            name: 'borrowmantime',
            allowBlank: false,
            margin: '10 0 0 0'
        }, {
            columnWidth: .01,
            xtype: 'displayfield'
        }, {
            columnWidth: .23,
            xtype: 'numberfield',
            itemId:'borrowtsId',
            fieldLabel: '查档天数',
            labelWidth: 85,
            name: 'borrowts',
            allowBlank: false,
            margin: '10 0 0 0'
        }, {
            columnWidth: .47,
            xtype: 'combo',
            itemId: 'spmanId',
            store: 'ApproveManStore',
            queryMode:'all',
            name: 'borrowcode',
            fieldLabel: '受理人',
            labelWidth: 85,
            displayField: 'realname',
            valueField: 'userid',
            allowBlank: false,
            editable: false,
            margin: '10 0 0 0',
            listeners: {
                afterrender: function (combo) {
                    var store = combo.getStore();
                    setTimeout(function(){
                        if (store.getCount() > 0) {
                            combo.select(store.getAt(0));
                        }
                    },500);
                },
                beforerender: function (combo) {
                    var store = combo.getStore();
                    store.removeAll();
                    store.proxy.extraParams.worktext = "电子查档审批";
                    store.proxy.extraParams.type = "1";
                    store.load();
                }
            }
        },{
            columnWidth: .06,
            xtype: 'displayfield'
        }, {
            columnWidth: .32,
            xtype: 'textfield',
            itemId:'media',
            fieldLabel: '附件',
            labelWidth: 85,
            editable: false,
            name: 'evidencetext',
            margin: '10 0 0 0'
        },{
            columnWidth: .08,
            style : 'text-align:center;',
            margin: '17 0 0 0',
            items: [
                {
                    xtype: 'label',
                    itemId:'mediacount',
                    text: '共0份'
                }
            ]
        }, {
            columnWidth: .07,
            margin: '10 0 0 0',
            items: [
                {
                    xtype: 'button',
                    itemId:'electronAddId',
                    text: '上传'
                }
            ]
        },{
            columnWidth: 1,
            xtype: 'textarea',
            fieldLabel: '备注',
            labelWidth: 85,
            name: 'desci',
            margin: '10 0 0 0'
        }],
    buttons: [
        {itemId:'getAddressId', text: '读取身份证'},
        {text: '提交', itemId: 'electronAddSubmit'},
        {text: '关闭', itemId: 'electronAddClose'}
    ]
});
