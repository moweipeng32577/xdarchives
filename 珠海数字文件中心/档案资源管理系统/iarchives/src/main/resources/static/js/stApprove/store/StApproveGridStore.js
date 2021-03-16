/**
 * Created by yl on 2017/10/26.
 */
Ext.define('StApprove.store.StApproveGridStore',{
    extend:'Ext.data.Store',
    model:'StApprove.model.StApproveGridModel',
    pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/stApprove/getEntryIndex',
        extraParams: {
            taskid:taskid
        },
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});