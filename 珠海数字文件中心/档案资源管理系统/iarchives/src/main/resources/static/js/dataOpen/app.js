Ext.Loader.setConfig({
	disableCaching : false
});
Ext.application({
	requires : [ 'Ext.container.Viewport' ],
	
	name : 'Dataopen', // 定义的命名空间
	appFolder : '../js/dataOpen', // 指明应用的根目录

	controllers : ['DataopenController'],

	launch : function() {
		Ext.create('Ext.container.Viewport', {
			layout : 'fit',
			items : {
				xtype : 'dataopenView'
			}
		});
	}
});