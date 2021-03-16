Ext.Loader.setConfig({
	disableCaching : false
});
Ext.application({
	requires : [ 'Ext.container.Viewport' ],
	
	name : 'Inventory', // 定义的命名空间
	appFolder : '../js/storeroom/inventory', // 指明应用的根目录

	controllers : ['InventoryController'],

	launch : function() {
		Ext.create('Ext.container.Viewport', {
			layout : 'fit',
			items : {
				xtype : 'inventoryView'
			}
		});
	}
});
