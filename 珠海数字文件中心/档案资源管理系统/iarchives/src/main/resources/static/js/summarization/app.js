
Ext.Loader.setConfig({
	disableCaching : false
});
Ext.application({
	requires : [ 'Ext.container.Viewport' ],
	
	name : 'Summarization', // 定义的命名空间
	appFolder : '../js/summarization', // 指明应用的根目录

	controllers : ['SummarizationController'],

	launch : function() {
		Ext.create('Ext.container.Viewport', {
			layout : 'fit',
			items : {
				xtype : 'summarization'
			}
		});
	}
});