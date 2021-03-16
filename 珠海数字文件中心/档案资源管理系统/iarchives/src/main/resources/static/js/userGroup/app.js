/**
 * Created by xd on 2017/10/21.
 */
Ext.Loader.setConfig({
    disableCaching : false
});
Ext.application({
    requires : [ 'Ext.container.Viewport' ],

    name : 'UserGroup', // 定义的命名空间
    appFolder : '../js/userGroup', // 指明应用的根目录

    controllers : ['UserGroupController'],

    launch : function() {
        var showView='userGroupView';
        if(userType=='bm'){//安全保密员显示tab页面
            showView='UserXitongTabView'
        }
        Ext.create('Ext.container.Viewport', {
            layout : 'fit',
            items : {
                xtype : showView
            }
        });
    }
});