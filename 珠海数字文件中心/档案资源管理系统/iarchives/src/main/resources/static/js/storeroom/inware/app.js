Ext.Loader.setConfig({
	disableCaching : false
});
Ext.application({
	requires : [ 'Ext.container.Viewport' ],
	
	name : 'Inware', // 定义的命名空间
	appFolder : '../js/storeroom/inware', // 指明应用的根目录

	controllers : ['InwareController','ImportController'],

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
