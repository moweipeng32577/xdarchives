/**
 * Created by RonJiang on 2017/10/31 0031.
 */
Ext.define('ClassifySearch.view.ClassifyElectronFormItemView', {
    extend: 'Ext.form.Panel',
    xtype: 'classifyElectronFormItemView',
    itemId: 'classifyElectronFormItemViewId',
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
            xtype: 'textfield',
            fieldLabel: '查档人',
            labelWidth: 85,
            editable: false,
            name: 'borrowman',
            margin: '10 0 0 0'
        }, {
            columnWidth: .06,
            xtype: 'displayfield'
        }, {
            columnWidth: .47,
            xtype: 'combo',
            store: 'ClassifyJypurposeStore',
            itemId: 'jypurposeId',
            name: 'borrowmd',
            fieldLabel: '查档目的',
            labelWidth: 85,
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
            columnWidth: .23,
            xtype: 'textfield',
            fieldLabel: '查档部门',
            labelWidth: 85,
            editable: false,
            name: 'borroworgan',
            margin: '10 0 0 0'
        }, {
            columnWidth: .011,
            xtype: 'displayfield'
        }, {
            columnWidth: .23,
            xtype: 'numberfield',
            itemId:'numberfield',
            fieldLabel: '查档天数',
            labelWidth: 85,
            name: 'borrowts',
            margin: '10 0 0 0'
        }, {
            columnWidth: .06,
            xtype: 'displayfield'
        }, {
            columnWidth: .47,
            xtype: 'combo',
            itemId: 'spmanId',
            store: 'ClassifyApproveManStore',
            queryMode:'all',
            name: 'borrowcode',
            fieldLabel: '审批人',
            labelWidth: 85,
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
                    store.proxy.extraParams.worktext = "查档审批";
                    store.proxy.extraParams.type = "1";
                    store.load();
                }
            }
        }, {
            columnWidth: 1,
            xtype: 'textarea',
            fieldLabel: '查档描述',
            labelWidth: 85,
            name: 'desci',
            margin: '10 0 0 0'
        }],
    buttons: [
        {text: '提交', itemId: 'classifyElectronFormSubmit'},
        {text: '关闭', itemId: 'classifyElectronFormClose'}
    ]
});