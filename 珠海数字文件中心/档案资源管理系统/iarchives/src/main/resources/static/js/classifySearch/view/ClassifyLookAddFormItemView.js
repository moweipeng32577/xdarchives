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

Ext.define('ClassifySearch.view.ClassifyLookAddFormItemView', {
    extend: 'Ext.form.Panel',
    xtype: 'classifylookAddFormItemView',
    itemId: 'classifylookAddFormItemViewId',
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
            margin: '10 0 0 0'
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
                        html: "请输入正确的电话号码！固话请输入：区号+号码，区号以0开头，3位或4位；号码由7位或8位数字组成（区号与号码之间可以无连接符，也可以“-”连接）。手机请输入：11位数字，以1开头。"
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
            fieldLabel: '查档者单位',
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
            store: lyModeStore,
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
            store: 'ClassifyJypurposeStore',
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
        }, {
            columnWidth: .03,
            xtype: 'displayfield'
        }, {
            columnWidth: .47,
            xtype: 'textfield',
            fieldLabel: '查档人次',
            name: 'borrowmantime',
            margin: '10 0 0 0'
        }, {
            columnWidth: .03,
            xtype: 'displayfield'
        }, {
            columnWidth: .47,
            xtype: 'textfield',
            fieldLabel: '查档内容',
            name: 'borrowcontent',
            margin: '10 0 0 0'
        }, {
            columnWidth: .03,
            xtype: 'displayfield'
        }, {
            columnWidth: .97,
            xtype: 'combo',
            itemId: 'spmanId',
            store: 'ClassifyApproveManStore',
            name: 'borrowcode',
            fieldLabel: '审批人',
            displayField: 'realname',
            valueField: 'userid',
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
                    store.proxy.extraParams.worktext = "实体查档审批";
                    store.proxy.extraParams.type = "2";
                    store.load();
                }
            }
        }, {
            columnWidth: .03,
            value:'<label style="color:#ff0b23;!important;">*</label>',
            margin: '10 0 0 1',
            xtype: 'displayfield'
        }, {
            columnWidth: .97,
            xtype: 'textarea',
            fieldLabel: '查档描述',
            name: 'desci',
            margin: '10 0 0 0'
        }, {
            columnWidth: .03,
            xtype: 'displayfield'
        }],
    buttons: [
        {text: '提交', itemId: 'classifylookAddFormSubmit'},
        {text: '关闭', itemId: 'classifylookAddFormClose'}
    ]
});