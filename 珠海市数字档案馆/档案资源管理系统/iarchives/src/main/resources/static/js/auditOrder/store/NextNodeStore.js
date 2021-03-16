/**
 * Created by Administrator on 2020/6/16.
 */


Ext.define('AuditOrder.store.NextNodeStore',{
    extend:'Ext.data.Store',
    xtype:'nextNodeStore',
    fields: ['id', 'text'],
    proxy: {
        type: 'ajax',
        url: '/carOrder/getNextNodeByType',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
