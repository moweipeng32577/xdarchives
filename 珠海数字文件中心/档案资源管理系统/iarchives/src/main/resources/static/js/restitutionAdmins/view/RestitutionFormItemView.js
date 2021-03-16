/**
 * Created by RonJiang on 2017/10/31 0031.
 */
var lyModeStore = Ext.create("Ext.data.Store", {
    fields: ["text", "value"],
    data: [
        {text: "查阅", value: "查阅"},
        {text: "外借", value: "外借"}
    ]
});

var JypurposeStore = Ext.create("Ext.data.Store", {
    fields: ['configid', 'value'],
    autoLoad: true,
    proxy: {
        type: 'ajax',
        url: '/electron/getJypurpose',
        extraParams: {
        },
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});


Ext.define('Restitution.view.RestitutionFormItemView', {
    extend: 'Ext.form.Panel',
    xtype: 'restitutionFormItemView',
    itemId: 'restitutionFormItemViewId',
    region: 'center',
    autoScroll: true,
    fieldDefaults: {
        labelWidth: 120
    },
    layout: 'column',
    bodyPadding: 15,
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
            margin: '5 0 0 0'
        }, {
            columnWidth: .7,
            xtype: 'displayfield'
        }, {
            columnWidth: .47,
            xtype: 'textfield',
            itemId:'borrowmanId',
            fieldLabel: '查档者姓名',
            name: 'borrowman',
            allowBlank: false,
            margin: '5 0 0 0'
        }, {
            columnWidth: .03,
            value:'<label style="color:#ff0b23;!important;">*</label>',
            margin: '5 0 0 1',
            xtype: 'displayfield'
        }, {
            columnWidth: .47,
            xtype: 'textfield',
            itemId:'borrowmantelId',
            fieldLabel: '查档者电话号码',
            name: 'borrowmantel',
            // allowBlank: false,
            regex: /^((0\d{2,3})-)(\d{7,8})(-(\d{3,}))?$|0?1[3|4|5|8][0-9]\d{8}/,
            regexText: '请输入正确电话号码',
            margin: '5 0 0 0',
            listeners: {
                render: function(sender) {
                    new Ext.ToolTip({
                        target: sender.el,
                        trackMouse: true,
                        dismissDelay: 0,
                        anchor: 'buttom',
                        html: "请输入正确的11位手机号码和座机号码（格式：区号+座机号码+分机号码）!"
                    });
                }
            }
        }, {
            columnWidth: .03,
            // value:'<label style="color:#ff0b23;!important;">*</label>',
            margin: '5 0 0 1',
            xtype: 'displayfield'
        }, {
            columnWidth: .47,
            xtype: 'textfield',
            itemId:'borroworganId',
            fieldLabel: '查档（接收）单位',
            name: 'borroworgan',
            margin: '5 0 0 0'
        }, {
            columnWidth: .03,
            margin: '5 0 0 1',
            xtype: 'displayfield'
        }, {
            columnWidth: .47,
            xtype: 'numberfield',
            itemId:'borrowtsId',
            fieldLabel: '查档天数',
            name: 'borrowts',
            // allowBlank: false,
            margin: '5 0 0 0'
        }, {
            columnWidth: .03,
            // value:'<label style="color:#ff0b23;!important;">*</label>',
            margin: '5 0 0 1',
            xtype: 'displayfield'
        }, {
            columnWidth: .47,
            xtype: 'textfield',
            fieldLabel: '证件类型',
            name: 'certificatetype',
            itemId:'certificatetypeId',
            margin: '5 0 0 0'
        }, {
            columnWidth: .03,
            xtype: 'displayfield'
        }, {
            columnWidth: .47,
            xtype: 'textfield',
            fieldLabel: '证件号码',
            name: 'certificatenumber',
            itemId:'certificatenumberId',
            margin: '5 0 0 0'
        }, {
            columnWidth: .03,
            xtype: 'displayfield'
        }, {
            columnWidth: .47,
            xtype: 'combo',
            store: lyModeStore,
            name: 'lymode',
            fieldLabel: '查档类型',
            displayField: 'text',
            valueField: 'value',
            editable: false,
            margin: '5 0 0 0',
            listeners: {
                afterrender: function (combo) {
                    var store = combo.getStore();
                    if (store.getCount() > 0) {
                        combo.select(store.getAt(0));
                    }
                }
            }
        }, {
            columnWidth: .03,
            xtype: 'displayfield'
        }, {
            columnWidth: .47,
            xtype: 'combo',
            store: JypurposeStore,
            name: 'borrowmd',
            fieldLabel: '查档目的',
            displayField: 'value',
            valueField: 'value',
            margin: '5 0 0 0',
            editable: false,
            listeners: {
                afterrender: function (combo) {
                    var store = combo.getStore();
                    if (store.getCount() > 0) {
                        combo.select(store.getAt(0));
                    }
                }
            }
        },
        {
            columnWidth: .03,
            // value:'<label style="color:#ff0b23;!important;">*</label>',
            margin: '5 0 0 1',
            xtype: 'displayfield'
        }, {
            columnWidth: .97,
            xtype: 'textarea',
            fieldLabel: '查档描述',
            name: 'desci',
            margin: '5 0 0 0'
        }, {
            columnWidth: .03,
            margin: '10 0 0 1',
            xtype: 'displayfield'
        }],
    buttons: [
        {text: '读取身份证',itemId:'readuid',handler: function () {
            var result = CertCtl.getStatus();
            var str = JSON.parse(result);
            if (str.status == 0) {
            } else if (str.status == 1) {
                CertCtl.connect();
            }

            result = CertCtl.readCert();
            var resultObj = JSON.parse(result);
            if (resultObj.resultFlag == 0) {
                this.up('restitutionFormItemView').down('[itemId=borrowmanId]').setValue(resultObj.resultContent.partyName); //姓名
                this.up('restitutionFormItemView').down('[itemId=certificatenumberId]').setValue(resultObj.resultContent.certNumber);//身份证号
                this.up('restitutionFormItemView').down('[itemId=certificatetypeId]').setValue("身份证件");
            } else {
                Ext.Msg.alert('失败', resultObj.errorMsg);
            }
            CertCtl.disconnect();
        }},
        {text: '提交', itemId: 'formSubmit'},
        {text: '关闭', itemId: 'formClose'}
    ]
});