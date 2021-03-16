/**
 * Created by RonJiang on 2018/4/21 0021.
 */
Ext.define('Appraisal.store.DealDetailsGridStore',{
    extend:'Ext.data.Store',
    model:'Appraisal.model.DealDetailsGridModel',
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