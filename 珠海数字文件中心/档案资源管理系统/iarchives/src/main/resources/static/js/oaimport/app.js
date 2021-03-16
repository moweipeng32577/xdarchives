Ext.Loader.setConfig({
    disableCaching : false
});
Ext.application({
    requires: ['Ext.container.Viewport'],
    name: 'OAImport',
    appFolder: '../js/oaimport',
    controllers: ['OAImportController'],
    launch: function () {
        Ext.create('Ext.container.Viewport', {
            layout: 'fit',
            items: {
                xtype: 'oaimporttab'
            }
        });
    }
});