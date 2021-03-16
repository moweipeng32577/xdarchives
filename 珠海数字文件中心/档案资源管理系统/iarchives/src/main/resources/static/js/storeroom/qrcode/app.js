Ext.Loader.setConfig({
	disableCaching : false
});
Ext.application({
	requires : [ 'Ext.container.Viewport' ],
	
	name : 'Qrcode', // 定义的命名空间
	appFolder : '../js/storeroom/qrcode', // 指明应用的根目录

	controllers : ['QrcodeController'],

	launch : function() {
		Ext.create('Ext.container.Viewport', {
			layout : 'fit',
			items : {
				xtype : 'qrcodeView'
			}
		});
	}
});
