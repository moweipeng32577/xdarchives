/**
 * Created by Administrator on 2020/10/15.
 */




Ext.define('BusinessYearlyCheck.view.BusinessYearlyCheckSubmitFormView', {
    extend: 'Ext.panel.Panel',
    xtype: 'businessYearlyCheckSubmitFormView',
    itemId: 'businessYearlyCheckSubmitFormViewId',
    region: 'center',
    autoScroll: true,
    layout: 'fit',
    items: [{
        xtype: 'form',
        layout: 'column',
        bodyPadding: 16,
        items: [{
            columnWidth: .3,
            xtype: 'textfield',
            itemId: 'submiterId',
            fieldLabel: '提交人',
            name: 'submiter',
            allowBlank: false,
            labelWidth: 85
        }, {
            columnWidth: .05,
            xtype: 'displayfield'
        }, {
            columnWidth: .3,
            xtype: 'textfield',
            itemId: 'submittimeId',
            fieldLabel: '提交时间',
            allowBlank: false,
            name: 'submittime',
            labelWidth: 85
        }, {
            columnWidth: .05,
            xtype: 'displayfield'
        }, {
            columnWidth: .3,
            xtype: 'combo',
            itemId: 'spmanId',
            store: 'ApproveManStore',
            queryMode: 'local',
            fieldLabel: '审批人',
            labelWidth: 85,
            displayField: 'realname',
            valueField: 'userid',
            allowBlank: false,
            editable: false,
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
                }
            }
        }, {
            columnWidth: 1,
            xtype: 'textarea',
            name: 'remark',
            fieldLabel: '备注',
            labelWidth: 85,
            margin: '10 0 0 0'
        }]
    }],
    buttons: [
        {text: '提交', itemId: 'approveSubmit'},
        {text: '关闭', itemId: 'approveClose'}
    ]
});
