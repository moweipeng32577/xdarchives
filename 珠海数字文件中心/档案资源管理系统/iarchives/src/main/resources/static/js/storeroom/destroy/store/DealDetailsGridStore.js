/**
 * Created by RonJiang on 2018/4/21 0021.
 */
Ext.define('Destroy.store.DealDetailsGridStore',{
    extend:'Ext.data.Store',
    model:'Destroy.model.DealDetailsGridModel',
    pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/destructionBill/getDealDetails',
        extraParams: {},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});