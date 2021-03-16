Ext.Loader.setConfig({
    disableCaching : false
});
Ext.application({
    requires : [ 'Ext.container.Viewport' ],

    name : 'PartyBuilding', // 定义的命名空间
    appFolder : '../js/partyBuilding', // 指明应用的根目录

    controllers : ['PartyBuildingController'],

    launch : function() {
        Ext.create('Ext.container.Viewport', {
            layout : 'fit',
            items : {
                xtype : 'partyBuildingGridView'
            }
        });
    }
});