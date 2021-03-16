Ext.Loader.setConfig({
	disableCaching : false
});
Ext.application({
	requires : [ 'Ext.container.Viewport' ],
	
	name : 'ReturnWare', // 定义的命名空间
	appFolder : '../js/storeroom/returnware', // 指明应用的根目录

	controllers : ['InwareController'],

	launch : function() {
		Ext.create('Ext.container.Viewport', {
			layout : 'fit',
			items : {
				xtype : 'inwareView'
                // xtype : 'inwareTabView'
			}
		});
	}
});
