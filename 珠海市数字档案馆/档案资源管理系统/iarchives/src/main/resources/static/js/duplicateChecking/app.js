
Ext.Loader.setConfig({
	disableCaching : false
});
Ext.application({
	requires : [ 'Ext.container.Viewport' ],
	
	name : 'DuplicateChecking', // 定义的命名空间
	appFolder : '../js/duplicateChecking', // 指明应用的根目录

	controllers : ['DuplicateCheckingController'],

	launch : function() {
		Ext.create('Ext.container.Viewport', {
			layout : 'fit',
			items : {
				xtype : 'duplicateChecking'
			}
		});
	}
});