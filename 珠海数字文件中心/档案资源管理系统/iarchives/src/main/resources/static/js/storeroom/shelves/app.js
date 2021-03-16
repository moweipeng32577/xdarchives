
Ext.Loader.setConfig({
	disableCaching : false
});
Ext.application({
	requires : [ 'Ext.container.Viewport' ],
	
	name : 'Shelves', // 定义的命名空间
	appFolder : '../js/storeroom/shelves', // 指明应用的根目录

	controllers : ['ShelvesController'],

	launch : function() {
		Ext.create('Ext.container.Viewport', {
			layout : 'fit',
			items : {
				xtype : 'shelves'
			}
		});
	}
});