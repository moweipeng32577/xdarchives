/**
 * Created by yl on 2017/10/26.
 */
Ext.define('BillApproval.model.BillApprovalGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string', mapping: 'billid'},
        {name: 'title', type: 'string'},
        {name: 'approvaldate', type: 'string'}
    ]
});