Ext.Loader.setConfig({
    disableCaching : false
});
Ext.namespace("XD");
XD.pageSize = 25;
Ext.application({
	requires : [ 'Ext.container.Viewport' ],
	
	name : 'Lot', // 定义的命名空间
	appFolder : '../../js/lot', // 指明应用的根目录

	controllers : ['VisualizationController','EnvController','SecurityController','DeviceController',
        'DeviceAreaController','DeviceLinkController','DeviceWorkController','MJJController','DeviceInformationController'
    ,'DeviceDiagnoseController','DeviceAlarmController','ManagementHistoryController'],

	launch : function() {
		Ext.create('Ext.container.Viewport', {
			layout : 'fit',
			items : {
				xtype : 'visualization'//修改成表单与表格视图
			}
		});
	}
});

Date.prototype.format = function(fmt) {
    var o = {
        "M+" : this.getMonth()+1,                 //月份
        "d+" : this.getDate(),                    //日
        "H+" : this.getHours(),                   //小时
        "m+" : this.getMinutes(),                 //分
        "s+" : this.getSeconds(),                 //秒
        "q+" : Math.floor((this.getMonth()+3)/3), //季度
        "S"  : this.getMilliseconds()             //毫秒
    };
    if(/(y+)/.test(fmt))
        fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));
    for(var k in o)
        if(new RegExp("("+ k +")").test(fmt))
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));
    return fmt;
};