Ext.define('Template.view.TemplateFormView',{
    extend:'Ext.window.Window',
    xtype:'templateFormView',
    itemId:'templateFormViewId',
    width:'100%',
    height:'100%',
    title:'预览表单',
    // overflow: 'auto',
    //标签页靠左配置--start
    // tabPosition:'top',
    // tabRotation:0,
    // //标签页靠左配置--end
    //
    // activeTab:0,
    layout:'border',
    items:[{
        // extend:'Ext.form.Panel',
        xtype:'PreFieldPanel',
        // margin:'15 15 15 40',
        // region:'west',
        // layout:'fit',
        // height:'60%',
        // width:'30%',
        // itemId:'PreFieldPanel',
        // flex:1,
        // items:[{
        //     // height:'60%',
        //     xtype: 'itemselector',
        //     anchor: '100%',
        //     imagePath: '../ux/images/',
        //     store: 'TemplateUnselectFormStore',
        //     displayField: 'fieldname',
        //     valueField: 'fieldcode',
        //     allowBlank: false,
        //     msgTarget: 'side',
        //     fromTitle: '可选字段(按Ctrl+F查找)',
        //     toTitle: '已选字段'
        // }],
        // buttons:[{
        //     text:'字段管理',
        //     itemId:'FieldManagement'
        // },{
        //     text:'提交',
        //     itemId:'submit'
        // },{
        //     text:'关闭',
        //     itemId:'close'
        // },{
        //     text:'预览',
        //     itemId:'reflash'
        // }]
    },{
        xtype:'panel',
        region:'center',
        layout:'fit',
        heigth:'100%',

        // margin:'0 0 0 40',
        // title:'条目',
        // iconCls: 'x-tab-entry-icon',
        items:[{
            xtype:'dynamicform',
            calurl:'/management/getCalValue',
            items:[{
                xtype:'hidden',
                name:'entryid'
            }]
        }]
    }],

    // buttons:[{
    //     text:'返回',
    //     itemId:'back'
    // }]
});