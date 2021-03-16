/**
 * Created by RonJiang on 2018/1/23 0023.
 */
Ext.define('Backup.view.BackupSettingView', {
    extend:'Ext.panel.Panel',
    xtype:'backupSetting',
    itemId:'backupSettingViewId',
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
            itemId: 'settingBackupBtn',
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
            itemId: 'settingBackupStrategy',
            margin:'5 5 5 5'
        }]
    },{
        region:'center',
        xtype:'treepanel',
        itemId:'settingTreeId',
        rootVisible:true,
        store:'BackupSettingStore',
        collapsible:true,
        split:1,
        header:false,
        hideHeaders: true
    }]
});