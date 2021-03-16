Ext.Loader.setConfig({
	disableCaching : false
});
Ext.application({
	requires : [ 'Ext.container.Viewport' ],
	
	name : 'DataEvent', // 定义的命名空间
	appFolder : '../js/dataEvent', // 指明应用的根目录

	controllers : ['DataEventController'],

	launch : function() {
		Ext.create('Ext.container.Viewport', {
			layout : 'fit',
			items : {
				xtype : 'dataEventView'
			}
		});
	}
});