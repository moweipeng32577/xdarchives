/**
 * Created by yl on 2017/10/31.
 */
Ext.define('DestructionBill.view.DestructionBillInfoView', {
    extend: 'Ext.form.Panel',
    xtype: 'destructionBillInfoView',
    autoScroll: true,
    height: '28%',
    fieldDefaults: {
        labelWidth: 70
    },
    region: 'north',
    layout: 'column',
    items: [{
        columnWidth: 0.5,
        xtype: 'textfield',
        fieldLabel: '单据题名',
        editable: false,
        name: 'title',
        margin: '5 5 5 5'
    }, {
        columnWidth: 0.5,
        xtype: 'datefield',
        fieldLabel: '单据时间',
        editable: false,
        name: 'approvaldate',
        format: 'Y-m-d H:i:s',
        margin: '5 5 5 5'
    }, {
        columnWidth: 0.5,
        xtype: 'textfield',
        fieldLabel: '送审人',
        editable: false,
        name: 'submitter',
        margin: '5 5 5 5'
    }, {
        columnWidth: 0.5,
        xtype: 'textfield',
        fieldLabel: '条目总数',
        editable: false,
        name: 'total',
        margin: '5 5 5 5'
    }, {
        columnWidth: 1,
        xtype: 'textfield',
        fieldLabel: '销毁原因',
        name: 'reason',
        margin: '5 5 5 5',
        editable: false,
        height: 65
    }]
});