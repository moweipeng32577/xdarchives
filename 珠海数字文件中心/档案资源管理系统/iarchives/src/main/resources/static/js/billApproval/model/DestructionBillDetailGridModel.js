/**
 * Created by yl on 2017/11/29.
 */
Ext.define('BillApproval.model.DestructionBillDetailGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string', mapping: 'ugid'},
        {name: 'entryretention', type: 'string'},
        {name: 'title', type: 'string'},
        {name: 'archivecode', type: 'string'},
        {name: 'filedate', type: 'string'}
    ]
});