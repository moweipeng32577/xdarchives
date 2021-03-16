
Ext.Loader.setConfig({
	disableCaching : false
});
Ext.application({
	requires : [ 'Ext.container.Viewport' ],

	name : 'Backup', // 定义的命名空间
	appFolder : '../js/backup', // 指明应用的根目录

	controllers : ['BackupController'],

	launch : function() {
		Ext.create('Ext.container.Viewport', {
			layout : 'fit',
			items : {
				xtype : 'backup'
			}
		});
	}
});