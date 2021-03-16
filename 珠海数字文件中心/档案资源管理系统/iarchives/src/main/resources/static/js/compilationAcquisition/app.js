Ext.Loader.setConfig({
    disableCaching : false
});
Ext.application({
	requires : [ 'Ext.container.Viewport' ],
	
	name : 'CompilationAcquisition', // 定义的命名空间
	appFolder : '../js/compilationAcquisition', // 指明应用的根目录

	controllers : ['ManagementController',
	'ImportController'],

	launch : function() {
		Ext.create('Ext.container.Viewport', {
			layout : 'fit',
			items : {
				xtype : 'managementFormAndGrid'
			}
		});
	}
});