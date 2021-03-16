/**
 * Created by xd on 2017/10/21.
 */
Ext.define('ArchiveManagement.store.ArchiveGridStore',{
    extend:'Ext.data.Store',
    model:'ArchiveManagement.model.ArchiveGridModel',
    autoLoad: true,
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/user/getUnitUser',
        extraParams: {},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
