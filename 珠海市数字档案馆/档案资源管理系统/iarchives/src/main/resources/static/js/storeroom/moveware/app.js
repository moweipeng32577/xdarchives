Ext.Loader.setConfig({
	disableCaching : false
});
Ext.application({
	requires : [ 'Ext.container.Viewport' ],
	
	name : 'Moveware', // 定义的命名空间
	appFolder : '../js/storeroom/moveware', // 指明应用的根目录

	controllers : ['MovewareController'],

	launch : function() {
		Ext.create('Ext.container.Viewport', {
			layout : 'fit',
			items : {
				xtype : 'movewareView'
			}
		});
	}
});
