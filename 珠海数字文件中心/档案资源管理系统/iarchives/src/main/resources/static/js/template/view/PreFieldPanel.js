Ext.define('Template.view.PreFieldPanel',{
    extend:'Ext.form.Panel',
    xtype:'PreFieldPanel',
    margin:'15 15 15 40',
    region:'west',
    layout:'fit',
    height:'60%',
    width:'30%',
    itemId:'PreFieldPanel',
    flex:1,
    items:[{
        // height:'60%',
        xtype: 'itemselector',
        anchor: '100%',
        imagePath: '../ux/images/',
        store: 'TemplateUnselectFormStore',
        displayField: 'fieldname',
        valueField: 'fieldcode',
        allowBlank: false,
        msgTarget: 'side',
        fromTitle: '可选字段(按Ctrl+F查找)',
        toTitle: '已选字段'
    }],
    buttons:[{
    //     text:'字段管理',
    //     itemId:'FieldManagement'
    // },{
        text:'属性配置',
        itemId:'editOtherOptionsBtn'
    },{
        text:'提交',
        itemId:'submit'
    },{
        text:'关闭',
        itemId:'close'
    },{
        text:'预览',
        itemId:'reflash'
    }]
})