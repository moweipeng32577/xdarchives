Ext.define('Restitution.view.FormView',{
    extend: 'Ext.window.Window',
    xtype:'formView',
    height: '100%',
    width: '100%',
    header: false,
    draggable: false,//禁止拖动
    resizable: false,//禁止缩放
    layout:'fit',
    items:[{
        itemId:'northform',//上方的表单视图
        xtype:'restitutionFormView'//表单类型
    }]
});