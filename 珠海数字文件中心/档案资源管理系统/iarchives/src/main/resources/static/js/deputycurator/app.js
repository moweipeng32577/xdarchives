/**
 * Created by Administrator on 2020/7/20.
 */

Ext.Loader.setConfig({
    disableCaching : false
});
Ext.application({
    requires : [ 'Ext.container.Viewport' ],

    name : 'Deputycurator', // 定义的命名空间
    appFolder : '../js/deputycurator', // 指明应用的根目录

    controllers : ['DeputycuratorController'],

    launch : function() {
        Ext.create('Ext.container.Viewport', {
            layout : 'fit',
            items : {
                xtype : 'curatorView'
            }
        });
    }
});