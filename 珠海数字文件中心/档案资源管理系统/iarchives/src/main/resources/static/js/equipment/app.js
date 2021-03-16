
Ext.Loader.setConfig({
    disableCaching : false
});

Ext.application({
    requires:['Ext.container.Viewport'],

    name : 'Equipment', // 定义的命名空间
    appFolder : '../js/equipment', // 指明应用的根目录

    controllers : ['EquipmentController'],

    launch : function() {
        Ext.create('Ext.container.Viewport', {
            layout : 'fit',
            items : {
                xtype : 'equipmentManageView'
            }
        });
    }
});