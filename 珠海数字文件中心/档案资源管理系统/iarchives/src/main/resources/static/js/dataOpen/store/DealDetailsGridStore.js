/**
 * Created by RonJiang on 2018/4/21 0021.
 */
Ext.define('Dataopen.store.DealDetailsGridStore',{
    extend:'Ext.data.Store',
    model:'Dataopen.model.DealDetailsGridModel',
    pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/dataopen/getDealDetails',
        extraParams: {},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});