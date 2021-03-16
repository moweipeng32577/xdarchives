/**
 * Created by Leo on 2020/8/12 0012.
 */
Ext.define('ArchivesMigrate.store.ArchivesMigrateStore',{
    extend:'Ext.data.Store',
    model:'ArchivesMigrate.model.ArchivesMigrateModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/archivesMigrate/getArchivesMigrateBySearch',
        extraParams: {},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
