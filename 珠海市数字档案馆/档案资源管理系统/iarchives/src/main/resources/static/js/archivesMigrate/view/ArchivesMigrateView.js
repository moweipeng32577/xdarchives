/**
 * Created by Leo on 2020/8/13 0013.
 */
Ext.define('ArchivesMigrate.view.ArchivesMigrateView', {
    extend: 'Ext.panel.Panel',
    xtype: 'archivesMigrateView',
    layout:'card',
    activeItem:0,
    items: [{
        itemId:'archivesMigrateGridViewId',
        xtype:'archivesMigrateGridView'
    },{
        xtype:'archivesMigrateDetailGridView'
    }]
});