/**
 * Created by RonJiang on 2018/04/17.
 */
Ext.Loader.setConfig({
    disableCaching : false
});
Ext.application({
    requires : [ 'Ext.container.Viewport' ],

    name : 'Feedback', // 定义的命名空间
    appFolder : '../js/feedback', // 指明应用的根目录

    controllers : ['FeedbackController'],

    launch : function() {
        Ext.create('Ext.container.Viewport', {
            layout : 'fit',
            items : {
                xtype : 'feedback'
            }
        });
    }
});