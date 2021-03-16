/**
 * Created by Administrator on 2020/6/24.
 */


Ext.define('CarManage.store.CarDefendGridStore',{
    extend:'Ext.data.Store',
    model:'CarManage.model.CarDefendGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/carManage/getCarDefendByCarId',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
