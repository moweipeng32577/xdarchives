/**
 * Created by SunK on 2018/10/26 0026.
 */
var UpdatelyModeStore = Ext.create("Ext.data.Store", {
    fields: ["text", "value"],
    data: [
        {text: "查阅", value: "查阅"},
        {text: "外借", value: "外借"}
    ]
});

var UpdateJypurposeStore = Ext.create("Ext.data.Store", {
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


Ext.define('Restitution.view.RestitutionUpdateItemView', {
    extend: 'Ext.form.Panel',
    xtype: 'restitutionUpdateItemView',
    itemId: 'restitutionUpdateItemView',
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
            margin: '10 0 0 0'
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
            margin: '10 0 0 0',
        }, {
            columnWidth: .03,
            value:'<label style="color:#ff0b23;!important;">*</label>',
            margin: '10 0 0 1',
            xtype: 'displayfield'
        }, {
            columnWidth: .47,
            xtype: 'textfield',
            itemId:'borrowmantelId',
            fieldLabel: '查档者电话号码',
            name: 'borrowmantel',
            allowBlank: false,
            margin: '10 0 0 0',
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
            value:'<label style="color:#ff0b23;!important;">*</label>',
            margin: '10 0 0 1',
            xtype: 'displayfield'
        }, {
            columnWidth: .47,
            xtype: 'textfield',
            itemId:'borroworganId',
            fieldLabel: '查档（接收）单位',
            name: 'borroworgan',
            allowBlank: false,
            margin: '10 0 0 0'
        }, {
            columnWidth: .03,
            value:'<label style="color:#ff0b23;!important;">*</label>',
            margin: '10 0 0 1',
            xtype: 'displayfield'
        }, {
            columnWidth: .47,
            xtype: 'numberfield',
            itemId:'borrowtsId',
            fieldLabel: '查档天数',
            name: 'borrowts',
            allowBlank: false,
            margin: '10 0 0 0'
        }, {
            columnWidth: .03,
            value:'<label style="color:#ff0b23;!important;">*</label>',
            margin: '10 0 0 1',
            xtype: 'displayfield'
        }, {
            columnWidth: .47,
            xtype: 'textfield',
            fieldLabel: '证件类型',
            name: 'certificatetype',
            margin: '10 0 0 0'
        }, {
            columnWidth: .03,
            xtype: 'displayfield'
        }, {
            columnWidth: .47,
            xtype: 'textfield',
            fieldLabel: '证件号码',
            name: 'certificatenumber',
            margin: '10 0 0 0'
        }, {
            columnWidth: .03,
            xtype: 'displayfield'
        }, {
            columnWidth: .47,
            xtype: 'combo',
            store: UpdatelyModeStore,
            name: 'lymode',
            fieldLabel: '查档类型',
            displayField: 'text',
            valueField: 'value',
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
        }, {
            columnWidth: .03,
            xtype: 'displayfield'
        }, {
            columnWidth: .47,
            xtype: 'combo',
            store: UpdateJypurposeStore,
            name: 'borrowmd',
            fieldLabel: '查档目的',
            displayField: 'value',
            valueField: 'value',
            margin: '10 0 0 0',
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
            value:'<label style="color:#ff0b23;!important;">*</label>',
            margin: '10 0 0 1',
            xtype: 'displayfield'
        }, {
            columnWidth: .97,
            xtype: 'textarea',
            fieldLabel: '查档描述',
            name: 'desci',
            allowBlank: false,
            margin: '10 0 0 0'
        }, {
            columnWidth: .03,
            value:'<label style="color:#ff0b23;!important;">*</label>',
            margin: '10 0 0 1',
            xtype: 'displayfield'
        }],
    buttons: [
        {text: '保存', itemId: 'UpdateSave'},
        {text: '返回', itemId: 'goBack'}
    ]
});