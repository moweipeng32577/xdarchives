/**
 * Created by RonJiang on 2018/1/23 0023.
 */
Ext.define('Backup.store.BackupDownloadGridStore', {
    extend: 'Ext.data.Store',
    model: 'Backup.model.BackupDownloadGridModel',
    pageSize: XD.pageSize,
    autoLoad: true,
    proxy: {
        type: 'ajax',
        url: '/backupRestore/getBackupList',
        extraParams: {tab: ''},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});