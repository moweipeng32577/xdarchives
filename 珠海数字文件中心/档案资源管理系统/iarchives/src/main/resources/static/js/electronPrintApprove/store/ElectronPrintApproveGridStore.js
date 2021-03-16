/**
 * Created by Administrator on 2019/5/23.
 */

Ext.define('ElectronPrintApprove.store.ElectronPrintApproveGridStore',{
    extend:'Ext.data.Store',
    model:'ElectronPrintApprove.model.ElectronPrintApproveGridModel',
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
