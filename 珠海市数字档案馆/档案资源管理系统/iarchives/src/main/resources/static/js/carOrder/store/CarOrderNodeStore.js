/**
 * Created by Administrator on 2020/4/21.
 */


Ext.define('CarOrder.store.CarOrderNodeStore',{
    extend:'Ext.data.Store',
    fields: ['id', 'text'],
    proxy: {
        type: 'ajax',
        url: '/dataopen/getNode',
        extraParams: {
            workname:'公车预约审批'
        },
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
