/**
 * Created by yl on 2017/10/26.
 */
Ext.define('ElectronApprove.store.ElectronApproveGridStore',{
    extend:'Ext.data.Store',
    model:'ElectronApprove.model.ElectronApproveGridModel',
    pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/electronApprove/getEntryIndex',
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