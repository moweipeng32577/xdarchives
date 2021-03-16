/**
 * Created by yl on 2017/11/29.
 */
Ext.define('DestructionBill.store.DestructionBillDetailGridStore',{
    extend:'Ext.data.Store',
    model:'DestructionBill.model.DestructionBillDetailGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
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