/**
 * Created by RonJiang on 2017/10/31 0031.
 */

var certificateType = Ext.create("Ext.data.Store",{
    fields:["text","value"],
    data:[
        {text:"身份证",value:"身份证"},
        {text:"介绍信",value:"介绍信"},
        {text:"委托书",value:"委托书"}
    ]
});

var lyMode = Ext.create("Ext.data.Store",{
    fields:["text","value"],
    data:[
        {text:"复制、摘抄",value:"复制、摘抄"},
        {text:"调阅、外借、调出",value:"调阅、外借、调出"}
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

Ext.define('SimpleSearch.view.ElectronFormItemView', {
    extend: 'Ext.form.Panel',
    xtype: 'electronFormItemView',
    itemId: 'electronFormItemViewId',
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
            afterLabelTextTpl: [
                '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'
            ],
            margin: '10 0 0 0'
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
            afterLabelTextTpl: [
                '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'
            ],
            store: borrowcontentMode,
            reference: 'states',
            displayField: 'text',
            valueField: 'value',
            // filterPickList: true,
            queryMode: 'local',
            publishes: 'value',
            margin: '10 0 0 0'
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
            afterLabelTextTpl: [
                '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'
            ],
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
            columnWidth:.06,
            xtype:'displayfield'
        },{
            columnWidth: .23,
            fieldLabel: '申请时间',
            xtype: 'datefield',
            name: 'borrowdate',
            format: 'Ymd',
            labelWidth: 85,
            allowBlank: false,
            afterLabelTextTpl: [
                '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'
            ],
            editable: false,
            margin:'10 0 0 0'
        }, {
            columnWidth:.01,
            xtype:'displayfield'
        },{
            columnWidth: .23,
            fieldLabel: '受理时间',
            xtype: 'datefield',
            name: 'acceptdate',
            format: 'Ymd',
            labelWidth: 85,
            editable: false,
            margin:'10 0 0 0'
        },{
            columnWidth: .47,
            fieldLabel: '地址',
            xtype: 'textfield',
            name: 'comaddress',
            labelWidth: 85,
            editable: true,
            // hidden:systemType=='0'&&type!='selfQuery' ? true:false,
            // allowBlank: systemType=='0'&&type!='selfQuery' ? true:false,
            allowBlank: false,
            afterLabelTextTpl: [
                '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'
            ],
            margin: '10 0 0 0'
        }, {
            columnWidth:.06,
            xtype:'displayfield',
            // hidden:systemType=='0'&&type!='selfQuery' ? true:false,
        },{
            columnWidth: .47,
            fieldLabel: '电话或者电子邮箱',
            xtype: 'textfield',
            name: 'borrowmantel',
            labelWidth: 85,
            // hidden:systemType=='0'&&type!='selfQuery' ? true:false,
            // allowBlank: systemType=='0'&&type!='selfQuery' ? true:false,
            allowBlank: false,
            afterLabelTextTpl: [
                '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'
            ],
            margin: '10 0 0 0'
        },{
            columnWidth: .23,
            xtype: 'combo',
            itemId: 'relationshipId',
            store: relationShipModel,
            queryMode:'all',
            name: 'relationship',
            fieldLabel: '与当事人的关系',
            labelWidth: 85,
            displayField: 'value',
            valueField: 'value',
            // hidden:systemType=='0'&&type!='selfQuery' ? true:false,
            // allowBlank: systemType=='0'&&type!='selfQuery' ? true:false,
            allowBlank: false,
            afterLabelTextTpl: [
                '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'
            ],
            editable: false,
            margin: '-7 0 0 0',
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
            xtype:'displayfield',
            // hidden:systemType=='0'&&type!='selfQuery' ? true:false,
        },{
            columnWidth: .23,
            fieldLabel: '查档单位',
            xtype: 'textfield',
            name: 'borroworgan',
            labelWidth: 85,
            editable: true,
            // hidden:systemType=='0'&&type!='selfQuery' ? true:false,
            // allowBlank: systemType=='0'&&type!='selfQuery' ? true:false,
            allowBlank: false,
            afterLabelTextTpl: [
                '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'
            ]
        }, {
            columnWidth:.06,
            xtype:'displayfield',
            // hidden:systemType=='0'&&type!='selfQuery' ? true:false,
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
            // hidden:systemType=='0'&&type!='selfQuery' ? true:false,
            valueField: 'value',
            // allowBlank: systemType=='0'&&type!='selfQuery' ? true:false,
            allowBlank: false,
            afterLabelTextTpl: [
                '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'
            ],
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
            xtype:'displayfield',
            // hidden:systemType=='0'&&type!='selfQuery' ? true:false,
        },{
            columnWidth: .23,
            fieldLabel: '证件号码',
            xtype: 'textfield',
            itemId: 'certificatenumberId',
            name: 'certificatenumber',
            labelWidth: 85,
            // hidden:systemType=='0'&&type!='selfQuery' ? true:false,
            // allowBlank: systemType=='0'&&type!='selfQuery' ? true:false,
            allowBlank: false,
            afterLabelTextTpl: [
                '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'
            ],
            margin: '10 0 0 0'
        },{
            columnWidth: .47,
            xtype: 'combo',
            itemId: 'borrowmdId',
            name: 'borrowmd',
            fieldLabel: '目的',
            displayField: 'value',
            valueField: 'value',
            store:'PurposeStore',
            editable:false,
            labelWidth: 85,
            allowBlank: false,
            afterLabelTextTpl: [
                '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'
            ],
            margin: '10 0 0 0'
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
            // hidden:systemType=='0'&&type!='selfQuery' ? true:false,
            // allowBlank: systemType=='0'&&type!='selfQuery' ? true:false,
            allowBlank: false,
            afterLabelTextTpl: [
                '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'
            ],
            margin: '10 0 0 0'
        }, {
            columnWidth: .01,
            xtype: 'displayfield',
            // hidden:systemType=='0'&&type!='selfQuery' ? true:false,
        }, {
            // columnWidth: systemType=='0'&&type!='selfQuery' ? .47:.23,
            columnWidth: .23,
            xtype: 'numberfield',
            itemId:'borrowtsId',
            fieldLabel: '利用天数',
            labelWidth: 85,
            name: 'borrowts',
            allowBlank: false,
            afterLabelTextTpl: [
                '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'
            ],
            margin: '10 0 0 0'
        }, {
            columnWidth: borrrowSendmsg==true?.19:.23,
            xtype: 'combobox',
            itemId:'approveOrgan',
            store: 'ApproveOrganStore',
            fieldLabel: '审批单位',
            queryMode: "local",
            labelWidth: 85,
            allowBlank: false,
            displayField: 'organname',
            valueField: 'organid',
            editable: false,
            margin: '10 0 0 0',
            listeners: {
                beforerender: function (combo) {
                    combo.getStore().on('load',function () {
                        var store = combo.getStore();
                        if(store.getCount()>0){
                            var record = store.getAt(0);
                            combo.select(record);
                            combo.fireEvent("select",combo,record);
                        }
                    });
                },
                select:function (combo,record) {
                    var spmancombo = combo.findParentByType("electronFormItemView").down("[itemId=spmanId]");
                    spmancombo.select(null);
                    var spmanStore = spmancombo.getStore();
                    spmanStore.proxy.extraParams.findOrganid = record.get("organid");
                    spmanStore.proxy.extraParams.worktext = "查档审批";
                    spmanStore.reload();
                },
                render: function(sender) {
                    new Ext.ToolTip({
                        target: sender.el,
                        trackMouse: true,
                        dismissDelay: 0,
                        anchor: 'buttom',
                        html: ' <i class="fa fa-info-circle"></i> '+"支持跨单位查档申请，请选择要查档的单位！"
                    });
                }
            }
        },{
            columnWidth: .01,
            xtype: 'displayfield'
        },{
            columnWidth: borrrowSendmsg==true?.19:.23,
            xtype: 'combo',
            itemId: 'spmanId',
            store: 'ApproveManStore',
            queryMode: "local",
            name: 'borrowcode',
            fieldLabel: '受理人',
            labelWidth: 85,
            displayField: 'realname',
            valueField: 'userid',
            allowBlank: false,
            afterLabelTextTpl: [
                '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'
            ],
            editable: false,
            margin: '10 0 0 0',
            listeners: {
                beforerender: function (combo) {
                    var store = combo.getStore();
                    store.removeAll();
                    var organids = combo.findParentByType('electronFormItemView').organids;
                    store.proxy.extraParams.worktext = "查档审批";
                    store.proxy.extraParams.type = "1";
                    store.proxy.extraParams.organids = organids;
                    store.load();
                    store.on("load", function () {
                        if (store.getCount() > 0) {
                            var record = store.getAt(0);
                            combo.select(record);
                            combo.fireEvent("select", combo, record);
                        }
                    });
                }
            }
        },{
            columnWidth:.01,
            xtype:'displayfield',
            hidden:borrrowSendmsg==true?false:true
        },{
            columnWidth: .07,
            xtype: 'checkbox',
            itemId:'sendmsgId',
            labelWidth:85,
            inputValue : true,
            margin: '10 0 0 0',
            fieldLabel:'发送短信',
            hidden:borrrowSendmsg==true?false:true
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
                    itemId:'electronUpId',
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
        {text: '读取身份证', itemId: 'readuid',hidden:type=="selfQuery" || titleflag==2 || systemType=='1'?true:false},
        {text: '提交', itemId: 'electronFormSubmit'},
        {text: '关闭', itemId: 'electronFormClose'}
    ]
});