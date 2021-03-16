/**
 * Created by Administrator on 2020/4/24.
 */


Ext.define('PlaceOrder.store.NextNodeStore',{
    extend:'Ext.data.Store',
    xtype:'nextNodeStore',
    fields: ['id', 'text'],
    proxy: {
        type: 'ajax',
        url: '/placeOrder/getNextNode',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
