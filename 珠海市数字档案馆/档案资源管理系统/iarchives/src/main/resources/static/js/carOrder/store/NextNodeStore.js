/**
 * Created by Administrator on 2020/4/24.
 */


Ext.define('CarOrder.store.NextNodeStore',{
    extend:'Ext.data.Store',
    xtype:'nextNodeStore',
    fields: ['id', 'text'],
    proxy: {
        type: 'ajax',
        url: '/carOrder/getNextNode',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
