/**
 * Created by Administrator on 2020/4/17.
 */


Ext.define('CarManage.store.CarManageGridStore',{
    extend:'Ext.data.Store',
    model:'CarManage.model.CarManageGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/carManage/getCarManages',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
