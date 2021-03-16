/**
 * Created by RonJiang on 2017/10/31 0031.
 */

var certificateType = Ext.create("Ext.data.Store",{
    fields:["text","value"],
    data:[
        {text:"身份证",value:"身份证"},
        {text:"工作证",value:"工作证"},
        {text:"查询函",value:"查询函"},
        {text:"委托书",value:"委托书"},
        {text:"介绍信",value:"介绍信"},
        {text:"其他",value:"其他"}
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
        {text:"人员",value:"人员"},
        {text:"土地",value:"土地"},
        {text:"林政",value:"林政"},
        {text:"合同",value:"合同"},
        {text:"科技",value:"科技"},
        {text:"业务",value:"业务"},
        {text:"其他",value:"其他"},
    ]
});

var relationShipModel = Ext.create("Ext.data.Store",{
    fields:["text","value"],
    data:[
        {text:'本人',value:'本人'},
        {text:"夫妻",value:"夫妻"},
        {text:"委托人",value:"委托人"},
        {text:"父子",value:"父子"},
        {text:"父女",value:"父女"},
        {text:"母子",value:"母子"},
        {text:"母女",value:"母女"},
        {text:"公媳",value:"公媳"},
        {text:"婆媳",value:"婆媳"},
        {text:"监护人",value:"监护人"},
        {text:"工作关系",value:"工作关系"},
        {text:"政工人员",value:"政工人员"},
        {text:"公",value:"公"},
        {text:"检",value:"检"},
        {text:"法",value:"法"},
        {text:"安全部门",value:"安全部门"},
        {text:"受理案件",value:"受理案件"},
        {text:"婚姻登记部门",value:"婚姻登记部门"},
        {text:"其他",value:"其他"}

    ]
});

Ext.define('FindAccount.view.ElectronFormItemView', {
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
        {xtype: 'textfield', name: 'sex',itemId:'sex', hidden: true},//查档者性别
        {xtype: 'textfield', name: 'birthday',itemId:'birthday', hidden: true},//查档者生日
        {xtype: 'textfield', name: 'ethnic',itemId:'ethnic', hidden: true},//查档者民族
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
            margin: '10 0 0 0',
            listeners:{
                select:function (view) {
                    view.collapse();
                }
            }
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
            xtype:'displayfield'
        },{
            columnWidth: .23,
            fieldLabel: '证件号码',
            xtype: 'textfield',
            itemId: 'certificatenumberId',
            name: 'certificatenumber',
            labelWidth: 85,
            margin: '10 0 0 0'
        },{
            columnWidth:.06,
            xtype:'displayfield'
        },{
            columnWidth: .47,
            xtype: 'combo',
            itemId: 'borrowmdId',
            name: 'borrowmd',
            displayField: 'value',
            valueField: 'value',
            store:'PurposeStore',
            editable:false,
            fieldLabel: '目的',
            labelWidth: 85,
            allowBlank: false,
            afterLabelTextTpl: [
                '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'
            ],
            margin: '10 0 0 0'
        },{
            columnWidth:.23,
            xtype:'textfield',
            itemId:'sex',
            name:'sex',
            fieldLabel:'性别',
            labelWidth: 85,
            margin: '10 0 0 0'
        },{
            columnWidth:.01,
            xtype:'displayfield'
        },{
            columnWidth:.23,
            xtype:'textfield',
            itemId:'nation',
            name:'nation',
            fieldLabel :'民族',
            labelWidth: 85,
            margin: '10 0 0 0'
        },{
            columnWidth:.06,
            xtype:'displayfield'
        },{
            columnWidth: .47,
            fieldLabel: '地址',
            xtype: 'textfield',
            itemId: 'comaddressId',
            name: 'comaddress',
            labelWidth: 85,
            editable: true,
            margin: '10 0 0 0'
        }, {
            columnWidth: .47,
            fieldLabel: '电话',
            xtype: 'textfield',
            name: 'borrowmantel',
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
            xtype:'displayfield'
        },{
            columnWidth: .23,
            fieldLabel: '查档(接收)单位',
            xtype: 'textfield',
            name: 'borroworgan',
            labelWidth: 85,
            editable: true,
            allowBlank: true,
            margin: '15 0 0 0'
            // afterLabelTextTpl: [
            //     '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'
            // ]
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
            margin: '0 0 0 0',
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
            value:new Date(),
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
            value:new Date(),
            editable: false,
            margin:'10 0 0 0'
        },{
            columnWidth: .23,
            xtype: 'numberfield',
            itemId:'borrowmantimeld',
            fieldLabel: '利用人数',
            labelWidth: 85,
            value:1,
            name: 'borrowmantime',
            allowBlank: false,
            afterLabelTextTpl: [
                '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'
            ],
            margin: '3 0 0 0'
        }, {
            columnWidth: .01,
            xtype: 'displayfield'
        }, {
            columnWidth: .23,
            xtype: 'numberfield',
            itemId:'borrowtsId',
            fieldLabel: '利用天数',
            labelWidth: 85,
            value:3,
            name: 'borrowts',
            allowBlank: false,
            afterLabelTextTpl: [
                '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'
            ],
            margin: '3 0 0 0'
        },{
            columnWidth:.06,
            xtype:'displayfield'
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
                        for(var i=0;i<store.getCount();i++){
                            var record = store.getAt(i);
                            if(record.data.userid==loginUserid){
                                combo.select(record);
                                combo.fireEvent("select", combo, record);
                                break;
                            }
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
            layout : 'column',
            xtype:'fieldset',
            style:'background:#fff;padding-top:0px',
            columnWidth:1,
            title: '其它字段',
            collapsible: true,
            collapsed:true,
            autoScroll: true,
            items:[{
                columnWidth: 0.47,
                xtype: 'textfield',
                fieldLabel: '复制内容',
                labelWidth: 85,
                name: 'copycontent',
                margin: '5 0 0 0'
            },{
                columnWidth:.06,
                xtype:'displayfield'
            },{
                columnWidth: 0.47,
                xtype: 'textfield',
                fieldLabel: '复制目的',
                labelWidth: 85,
                name: 'copymd',
                margin: '5 0 0 0'
            }]
        }, {
            columnWidth: 1,
            xtype: 'textarea',
            fieldLabel: '备注',
            labelWidth: 85,
            name: 'desci',
            margin: '10 0 0 0'

        }],
    buttons: [
        {
            text: '读取身份证', itemId: 'readuid', handler: function () {
            var result = CertCtl.getStatus();
            var str = JSON.parse(result);
            if (str.status == 0) {
            } else if (str.status == 1) {
                CertCtl.connect();
            }

            result = CertCtl.readCert();
            var resultObj = JSON.parse(result);
            if (resultObj.resultFlag == 0) {
                var gender = resultObj.resultContent.gender;//性别
                if (gender == 1) {
                    this.up('electronFormItemView').down('[itemId=sex]').setValue('男');
                } else {
                    this.up('electronFormItemView').down('[itemId=sex]').setValue('女');
                }
                this.up('electronFormItemView').down('[itemId=borrowmanId]').setValue(resultObj.resultContent.partyName); //姓名
                this.up('electronFormItemView').down('[itemId=certificatenumberId]').setValue(resultObj.resultContent.certNumber);//身份证号
                this.up('electronFormItemView').down('[itemId=comaddressId]').setValue(resultObj.resultContent.certAddress);//地址
                this.up('electronFormItemView').down('[itemId=nation]').setValue(resultObj.resultContent.nation);//民族
                this.up('electronFormItemView').down('[itemId=birthday]').setValue(resultObj.resultContent.bornDay);//出生日期
            } else {
                Ext.Msg.alert('失败', resultObj.errorMsg);
            }
            CertCtl.disconnect();
          }
        },
        {text: '提交', itemId: 'electronFormSubmit'},
        {text: '关闭', itemId: 'electronFormClose'}
    ]
});