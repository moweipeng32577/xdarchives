
Ext.Loader.setConfig({
	disableCaching : false
});
Ext.application({
	requires : [ 'Ext.container.Viewport' ],
	
	name : 'CodesettingAlign', // 定义的命名空间
	appFolder : '../js/codesettingAlign', // 指明应用的根目录

	controllers : ['CodesettingAlignController'],

	launch : function() {
		Ext.create('Ext.container.Viewport', {
			layout : 'fit',
			items : {
				xtype : 'codesettingAlign'
			}
		});
	}
});