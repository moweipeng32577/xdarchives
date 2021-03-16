/**
 * Created by tanly on 2018/1/26 0026.
 */
Ext.define('Restore.store.RetoreTreeStore',{
    extend:'Ext.data.TreeStore',
    model:'Restore.model.RestoreGridModel',
    autoLoad:true,
    proxy: {
        type: 'ajax',
        url: '/backupRestore/analyzeByZip',
        reader: {
            type: 'json',
            expanded: true
        }
    },
    root: {
        text: '备份数据',
        expanded: true,
        checked: true
    }
});