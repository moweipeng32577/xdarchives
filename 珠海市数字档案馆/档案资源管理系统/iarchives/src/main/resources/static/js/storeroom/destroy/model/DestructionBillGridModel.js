/**
 * Created by yl on 2017/10/26.
 */
Ext.define('Destroy.model.DestructionBillGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string', mapping: 'billid'},
        {name: 'title', type: 'string'},
        {name: 'approvaldate', type: 'string'},
        {name: 'reason', type: 'string'}
    ]
});