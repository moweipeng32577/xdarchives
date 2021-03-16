/**
 * Created by yl on 2017/11/29.
 */
Ext.define('BillApproval.store.DestructionBillDetailGridStore',{
    extend:'Ext.data.Store',
    model:'BillApproval.model.DestructionBillDetailGridModel',
    pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/destructionBill/getDetailBill',
        extraParams: {
            billId:''
        },
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});