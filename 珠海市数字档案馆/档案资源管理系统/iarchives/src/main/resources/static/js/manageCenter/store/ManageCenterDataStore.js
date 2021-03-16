/**
 * Created by Administrator on 2020/7/21.
 */


Ext.define('ManageCenter.store.ManageCenterDataStore',{
    extend:'Ext.data.Store',
    sortOnLoad: true,
    model:'ManageCenter.model.ManageCenterDataModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/manageCenter/getManageCenterData',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
