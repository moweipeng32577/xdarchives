/**
 * Created by Leo on 2020/8/13 0013.
 */
Ext.define('ArchivesMigrate.store.ArchivesMigrateDetailGridStore',{
    extend:'Ext.data.Store',
    model:'ArchivesMigrate.model.ArchivesMigrateDetailGridModel',
    pageSize: XD.pageSize,//此处设置每页显示记录条数
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url:'/archivesMigrate/findBySearchArchivesMigrateEntry',
        timeout:XD.timeout,
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});