Ext.Loader.setConfig({
	disableCaching : false
});
Ext.application({
	requires : [ 'Ext.container.Viewport' ],
	
	name : 'Destroy', // 定义的命名空间
	appFolder : '../js/storeroom/destroy', // 指明应用的根目录

	controllers : ['DestroyController'],

	launch : function() {
		Ext.create('Ext.container.Viewport', {
			layout : 'fit',
			items : {
				xtype : 'destructionBillView'
			}
		});
	}
});
