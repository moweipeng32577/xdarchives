/**
 * Created by yl on 2017/11/1.
 */
Ext.define('BranchAudit.store.ThematicProdGridStore',{
    extend:'Ext.data.Store',
    model:'BranchAudit.model.ThematicProdGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/thematicProd/getThematic',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});