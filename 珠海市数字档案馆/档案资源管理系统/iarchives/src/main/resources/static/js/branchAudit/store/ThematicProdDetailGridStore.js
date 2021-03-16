/**
 * Created by yl on 2017/11/1.
 */
Ext.define('BranchAudit.store.ThematicProdDetailGridStore',{
    extend:'Ext.data.Store',
    model:'BranchAudit.model.ThematicProdDetailGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/infoCompilation/getThematicDetail',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});