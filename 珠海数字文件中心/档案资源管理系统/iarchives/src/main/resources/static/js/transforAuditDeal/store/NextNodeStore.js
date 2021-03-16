/**
 * Created by Administrator on 2019/9/25.
 */


Ext.define('TransforAuditDeal.store.NextNodeStore',{
    extend:'Ext.data.Store',
    xtype:'nextNodeStore',
    fields: ['id', 'text'],
    autoLoad: true,
    proxy: {
        type: 'ajax',
        url: '/electronApprove/getNextNode',
        extraParams: {
            nodeId:nodeId,
            taskid:taskid,
            type:"audit"
        },
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
