/**
 * Created by RonJiang on 2018/4/21 0021.
 */
Ext.define('Appraisal.store.BillEntryGridStore',{
    extend:'Ext.data.Store',
    model:'Appraisal.model.BillEntryGridModel',
    pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/appraisal/billEntry',
        extraParams: {},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});