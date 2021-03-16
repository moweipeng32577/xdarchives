/**
 * Created by yl on 2017/10/26.
 */
Ext.define('DestructionBill.store.DestructionBillGridStore',{
    extend:'Ext.data.Store',
    model:'DestructionBill.model.DestructionBillGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/destructionBill/getBill',
        timeout:XD.timeout,
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});