Ext.Loader.setConfig({
    disableCaching : false
});
Ext.application({
    requires : [ 'Ext.container.Viewport' ],

    name : 'QuestionnaireManagement', // 定义的命名空间
    appFolder : '../js/questionnaireManagement', // 指明应用的根目录

    controllers : ['QuestionnaireManagementController'],

    launch : function() {
        Ext.create('Ext.container.Viewport', {
            layout : 'fit',
            items : {
                xtype : 'questionnaireManagementGridView'
            }
        });
    }
});