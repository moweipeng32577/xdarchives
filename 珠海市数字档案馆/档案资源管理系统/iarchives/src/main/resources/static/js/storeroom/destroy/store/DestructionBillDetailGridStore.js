/**
 * Created by yl on 2017/11/29.
 */
Ext.define('Destroy.store.DestructionBillDetailGridStore',{
    extend:'Ext.data.Store',
    model:'Destroy.model.DestructionBillDetailGridModel',
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