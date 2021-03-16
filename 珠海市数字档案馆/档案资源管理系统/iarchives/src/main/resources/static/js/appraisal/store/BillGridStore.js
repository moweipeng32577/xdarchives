/**
 * Created by RonJiang on 2018/4/20 0020.
 */
Ext.define('Appraisal.store.BillGridStore',{
    extend:'Ext.data.Store',
    model:'Appraisal.model.BillGridModel',
    pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/appraisal/getNodeBill',
        extraParams: {},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});