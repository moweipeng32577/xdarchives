/**
 * Created by yl on 2017/10/26.
 */
Ext.define('DestructionBill.model.DestructionBillGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string', mapping: 'billid'},
        {name: 'title', type: 'string'},
        {name: 'approvaldate', type: 'string'},
        {name: 'reason', type: 'string'},
        {name: 'destructionappraise', type: 'string'}
    ]
});