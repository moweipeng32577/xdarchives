/**
 * Created by RonJiang on 2018/1/23 0023.
 */
Ext.define('Backup.store.BackupDataStore',{
    extend:'Ext.data.TreeStore',
    model:'Backup.model.BackupDataModel',
    autoLoad:true,
    proxy: {
        type: 'ajax',
        url: '/backupRestore/getDataBackups',
        reader: {
            type: 'json',
            expanded: true
        }
    },
    root: {
        text: '系统数据',
        expanded: true,
        checked: true
    }
});