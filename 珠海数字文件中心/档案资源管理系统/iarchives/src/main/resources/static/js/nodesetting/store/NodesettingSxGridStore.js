/**
 * Created by tanly on 2017/10/24 0024.
 */
Ext.define('Nodesetting.store.NodesettingSxGridStore',{
    extend:'Ext.data.Store',
    model:'Nodesetting.model.NodesettingGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/nodesetting/nodes',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});