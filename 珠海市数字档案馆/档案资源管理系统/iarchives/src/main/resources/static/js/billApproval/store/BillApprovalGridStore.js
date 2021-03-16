/**
 * Created by yl on 2017/10/26.
 */
Ext.define('BillApproval.store.BillApprovalGridStore',{
    extend:'Ext.data.Store',
    model:'BillApproval.model.BillApprovalGridModel',
    pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/destructionBill/getBillByTaskid',
        extraParams: {
            taskid:taskid
        },
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});