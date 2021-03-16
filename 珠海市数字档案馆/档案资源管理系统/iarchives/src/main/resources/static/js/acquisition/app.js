Ext.Loader.setConfig({
    disableCaching : false
});
Ext.application({
	requires : [ 'Ext.container.Viewport' ],
	
	name : 'Acquisition', // 定义的命名空间
	appFolder : '../js/acquisition', // 指明应用的根目录

	controllers : ['AcquisitionController',
		'AcquisitionDictionaryController',
		'AcquisitionTransforController',
		'AcquisitionFilingController',
		'AcquisitionInsertionController',
        'ImportController'],

	launch : function() {
		Ext.create('Ext.container.Viewport', {
			layout : 'fit',
			items : {
				xtype : 'acquisitionFormAndGrid'//修改成表单与表格视图
			}
		});
	}
});