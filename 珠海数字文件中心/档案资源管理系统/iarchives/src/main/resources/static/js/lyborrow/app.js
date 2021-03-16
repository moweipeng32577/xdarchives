
Ext.Loader.setConfig({
	disableCaching : false
});
Ext.application({
	requires : [ 'Ext.container.Viewport' ],
	
	name : 'Borrow', // 定义的命名空间
	appFolder : '../js/lyborrow', // 指明应用的根目录

	controllers : ['BorrowController'],

	launch : function() {
		Ext.create('Ext.container.Viewport', {
			layout : 'fit',
			items : {
				xtype : 'borrowView'
			}
		});
	}
});