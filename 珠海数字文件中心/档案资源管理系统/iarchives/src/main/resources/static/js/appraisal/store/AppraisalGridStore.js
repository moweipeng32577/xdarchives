/**
 * Created by yl on 2017/10/26.
 */
Ext.define('Appraisal.store.AppraisalGridStore',{
    extend:'Ext.data.Store',
    model:'Appraisal.model.AppraisalGridModel',
    autoLoad: true,
    pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/appraisal/getEntryIndex',
        extraParams: {
            nodeid:''
        },
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});