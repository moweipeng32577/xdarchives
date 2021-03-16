/**
 * Created by Administrator on 2019/2/22.
 */

Ext.define('Management.store.ElectronicVersionGridStore',{
    extend:'Ext.data.Store',
    model:'Management.model.ElectronicVersionGridModel',
    pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/management/getEleVersion',
        extraParams: {
            eleid:""
        },
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
