/**
 * Created by Administrator on 2020/7/21.
 */

Ext.define('ManageCenter.store.ManageCenterYearStore', {
    extend: 'Ext.data.Store',
    model: 'ManageCenter.model.ManageCenterYearModel',
    pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/manageCenter/getManageCenterYearNum',
        timeout:XD.timeout,
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
