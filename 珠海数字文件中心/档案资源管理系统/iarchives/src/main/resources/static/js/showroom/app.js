/**
 * Created by zdw on 2020/03/20
 */
Ext.Loader.setConfig({
    disableCaching : false
});
Ext.application({
    requires : [ 'Ext.container.Viewport' ],

    name : 'Showroom', // 定义的命名空间
    appFolder : '../js/showroom', // 指明应用的根目录

    controllers : ['ShowroomController'],

    launch : function() {
        Ext.create('Ext.container.Viewport', {
            layout : 'fit',
            items : {
                xtype : 'showroom'
            }
        });
    }
});