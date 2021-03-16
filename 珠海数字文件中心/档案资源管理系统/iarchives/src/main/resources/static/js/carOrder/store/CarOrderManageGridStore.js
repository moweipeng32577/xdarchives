/**
 * Created by Administrator on 2020/4/21.
 */


Ext.define('CarOrder.store.CarOrderManageGridStore',{
    extend:'Ext.data.Store',
    model:'CarOrder.model.CarOrderManageGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/carOrder/getAllCarManages',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
