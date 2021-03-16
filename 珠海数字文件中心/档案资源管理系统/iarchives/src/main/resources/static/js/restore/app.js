
Ext.Loader.setConfig({
	disableCaching : false
});
Ext.application({
	requires : [ 'Ext.container.Viewport' ],
	
	name : 'Restore', // 定义的命名空间
	appFolder : '../js/restore', // 指明应用的根目录

	controllers : ['RestoreController'],

	launch : function() {
		Ext.create('Ext.container.Viewport', {
			layout : 'fit',
			items : {
				xtype : 'restoreView'
			}
		});
	}
});