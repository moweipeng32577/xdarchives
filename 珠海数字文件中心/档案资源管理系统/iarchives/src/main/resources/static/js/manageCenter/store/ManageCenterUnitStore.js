/**
 * Created by Administrator on 2020/7/21.
 */


Ext.define('ManageCenter.store.ManageCenterUnitStore', {
    extend: 'Ext.data.Store',
    model: 'ManageCenter.model.ManageCenterUnitModel',
    pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/manageCenter/getManageCenterUnitNum',
        timeout:XD.timeout,
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});