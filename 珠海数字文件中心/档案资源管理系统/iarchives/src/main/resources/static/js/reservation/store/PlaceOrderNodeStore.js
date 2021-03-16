/**
 * Created by Administrator on 2020/4/21.
 */


Ext.define('Reservation.store.PlaceOrderNodeStore',{
    extend:'Ext.data.Store',
    fields: ['id', 'text'],
    proxy: {
        type: 'ajax',
        url: '/dataopen/getNode',
        extraParams: {
            workname:'场地预约审批'
        },
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
