/**
 * Created by Leo on 2020/8/12 0012.
 */
Ext.define('ArchivesMigrate.model.ArchivesMigrateModel', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string',mapping:'migid'},
        {name: 'migratedesc', type: 'string'},
        {name: 'migratedate', type: 'string'},
        {name: 'migrateuser', type: 'string'},
        {name: 'migratecount', type: 'string'},
        {name: 'migratestate', type: 'string'},
        {name: 'remarks', type: 'string'}
    ]
});