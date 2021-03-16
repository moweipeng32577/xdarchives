
Ext.Loader.setConfig({
	disableCaching : false
});
Ext.application({
	requires : [ 'Ext.container.Viewport' ],
	
	name : 'Audit', // 定义的命名空间
	appFolder : '../js/audit', // 指明应用的根目录

	controllers : ['AuditController'],

	launch : function() {
		Ext.create('Ext.container.Viewport', {
			layout : 'fit',
			items : {
				xtype : 'auditAdminView'
			}
		});
	}
});