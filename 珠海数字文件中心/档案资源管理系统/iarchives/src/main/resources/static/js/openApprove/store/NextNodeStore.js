/**
 * Created by tanly on 2017/12/5.
 */
Ext.define('OpenApprove.store.NextNodeStore',{
    extend:'Ext.data.Store',
    xtype:'nextNodeStore',
    fields: ['id', 'text'],
    autoLoad: true,
    proxy: {
        type: 'ajax',
        url: '/electronApprove/getNextNode',
        extraParams: {
            nodeId:nodeId
        },
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});