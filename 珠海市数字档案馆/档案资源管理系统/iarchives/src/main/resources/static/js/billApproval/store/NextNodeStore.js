/**
 * Created by yl on 2017/10/26.
 */
Ext.define('BillApproval.store.NextNodeStore',{
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