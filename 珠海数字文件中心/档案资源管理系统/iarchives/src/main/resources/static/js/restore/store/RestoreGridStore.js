/**
 * Created by tanly on 2018/1/26 0026.
 */
Ext.define('Restore.store.RestoreGridStore', {
    extend: 'Ext.data.Store',
    model: 'Restore.model.RestoreGridModel',
    pageSize: XD.pageSize,
    autoLoad: true,
    proxy: {
        type: 'ajax',
        url: '/backupRestore/getBackupList',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});