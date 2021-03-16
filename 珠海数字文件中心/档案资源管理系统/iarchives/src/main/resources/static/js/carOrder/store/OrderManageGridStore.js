/**
 * Created by Administrator on 2020/4/21.
 */


Ext.define('CarOrder.store.OrderManageGridStore',{
    extend:'Ext.data.Store',
    model:'CarOrder.model.OrderManageGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/carOrder/getCarOrderByCarid',
        extraParams: {
            id:'@'//默认
        },
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
