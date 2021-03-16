/**
 * Created by tanly on 2017/12/5.
 */
Ext.define('OpenApprove.store.OpenApproveGridStore',{
    extend:'Ext.data.Store',
    model:'OpenApprove.model.OpenApproveGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/openApprove/getEntryIndex',
        timeout:XD.timeout,
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});