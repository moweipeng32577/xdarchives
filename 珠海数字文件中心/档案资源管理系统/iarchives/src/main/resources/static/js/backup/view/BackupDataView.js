/**
 * Created by RonJiang on 2018/1/23 0023.
 */
Ext.define('Backup.view.BackupDataView', {
    extend:'Ext.panel.Panel',
    xtype:'backupData',
    itemId:'backupDataViewId',
    layout:'border',
    items:[{
        region:'north',
        height: 50,
        layout: {
            type: 'hbox',
            align: 'middle'
        },
        items:[{
            width:150,
            xtype: 'button',
            text: '备份',
            itemId: 'dataBackupBtn',
            margin:'5 5 5 5'
        },{
            width:170,
            xtype: 'button',
            text: '备份管理',
            itemId: 'historygrid',
            margin:'5 5 5 5'
        },{
            width:170,
            xtype: 'button',
            text: '备份策略',
            itemId: 'dataBackupStrategy',
            margin:'5 5 5 5'
        }]
    },{
        region:'center',
        xtype:'treepanel',
        itemId:'dataTreeId',
        rootVisible:true,
        store:'BackupDataStore',
        collapsible:true,
        split:1,
        header:false,
        hideHeaders: true
    }]
});