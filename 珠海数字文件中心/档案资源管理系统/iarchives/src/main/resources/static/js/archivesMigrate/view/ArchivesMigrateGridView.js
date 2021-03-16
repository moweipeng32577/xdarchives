/**
 * Created by Administrator on 2017/10/24 0024.
 */
Ext.define('ArchivesMigrate.view.ArchivesMigrateGridView', {
    extend: 'Comps.view.BasicGridView',
    xtype: 'archivesMigrateGridView',
    itemId: 'archivesMigrateGridViewId',
    bodyBorder: false,
    head: false,
    searchstore: [
        {item: "migrateuser", name: "迁移人"},
        {item: "migratecount", name: "迁移数量"},
        {item: "migratedate", name: "迁移时间"},
        {item: "migratestate", name: "迁移状态"},
    ],
    tbar: [
        {
            itemId: 'add',
            xtype: 'button',
            iconCls: 'fa fa-plus-circle',
            text: '迁移登记'
        },'-', {
            itemId: 'lookMigrate',
            xtype: 'button',
            iconCls: 'fa fa-indent',
            text: '查看登记'
        },'-', {
            itemId: 'migrate',
            xtype: 'button',
            iconCls: 'fa fa-indent',
            text: '迁移详情'
        },'-', {
            itemId: 'pack',
            xtype: 'button',
            iconCls: 'fa fa-indent',
            text: '迁移打包'
        },'-', {
            itemId: 'download',
            xtype: 'button',
            iconCls: 'fa fa-indent',
            text: '下载'
        }
    ],
    store: 'ArchivesMigrateStore',
    columns: [
        {text: '内容描述', dataIndex: 'migratedesc', flex: 2, menuDisabled: true},
        {text: '迁移人', dataIndex: 'migrateuser', flex: 1, menuDisabled: true},
        {text: '迁移数量', dataIndex: 'migratecount', flex: 1, menuDisabled: true},
        {text: '迁移时间', dataIndex: 'migratedate', flex: 2, menuDisabled: true},
        {text: '迁移状态', dataIndex: 'migratestate', flex: 1, menuDisabled: true},
        {text: '备注', dataIndex: 'remarks', flex: 2, menuDisabled: true}
    ]
});