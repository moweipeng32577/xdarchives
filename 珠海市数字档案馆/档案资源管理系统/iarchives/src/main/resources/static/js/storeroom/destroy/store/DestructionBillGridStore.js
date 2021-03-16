/**
 * Created by yl on 2017/10/26.
 */
Ext.define('Destroy.store.DestructionBillGridStore',{
    extend:'Ext.data.Store',
    model:'Destroy.model.DestructionBillGridModel',
    pageSize: XD.pageSize,
    autoLoad:true,
    proxy: {
        type: 'ajax',
        url: '/destructionBill/getBill',
        extraParams: {state:'4'},
        timeout:XD.timeout,
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});