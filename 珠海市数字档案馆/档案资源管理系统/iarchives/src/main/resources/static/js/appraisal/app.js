/**
 * Created by yl on 2017/10/25.
 */
Ext.Loader.setConfig({
    disableCaching : false
});
Ext.application({
    requires : [ 'Ext.container.Viewport' ],

    name : 'Appraisal', // 定义的命名空间
    appFolder : '../js/appraisal', // 指明应用的根目录

    controllers : ['AppraisalController'],

    launch : function() {
        Ext.create('Ext.container.Viewport', {
            layout : 'fit',
            items : {
                xtype : 'appraisalView'
            }
        });
    }
});