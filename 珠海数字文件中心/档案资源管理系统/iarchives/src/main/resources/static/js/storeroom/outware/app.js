Ext.Loader.setConfig({
	disableCaching : false
});
Ext.application({
	requires : [ 'Ext.container.Viewport' ],
	
	name : 'Outware', // 定义的命名空间
	appFolder : '../js/storeroom/outware', // 指明应用的根目录

	controllers : ['OutwareController'],

	launch : function() {
		Ext.create('Ext.container.Viewport', {
			layout : 'fit',
			items : {
				xtype : 'outwareView'
			}
		});
	}
});
