Ext.Loader.setConfig({
	disableCaching : false
});
Ext.application({
	requires : [ 'Ext.container.Viewport' ],
	
	name : 'DataTransfor', // 定义的命名空间
	appFolder : '../js/dataTransfor', // 指明应用的根目录

	controllers : ['DataTransforController'],

	launch : function() {
		Ext.create('Ext.container.Viewport', {
			layout : 'fit',
			items : {
				xtype : 'dataTransforView'
			}
		});
	}
});