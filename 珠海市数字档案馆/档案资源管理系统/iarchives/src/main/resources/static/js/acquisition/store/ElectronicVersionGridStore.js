/**
 * Created by Administrator on 2019/2/26.
 */

Ext.define('Acquisition.store.ElectronicVersionGridStore',{
    extend:'Ext.data.Store',
    model:'Acquisition.model.ElectronicVersionGridModel',
    pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/acquisition/getEleVersion',
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
