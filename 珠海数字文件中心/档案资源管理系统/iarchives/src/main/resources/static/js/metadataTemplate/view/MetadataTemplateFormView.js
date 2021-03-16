Ext.define('MetadataTemplate.view.MetadataTemplateFormView',{
    extend:'Ext.tab.Panel',
    xtype:'atemplateFormView',
    itemId:'templateFormViewId',
    //标签页靠左配置--start
    tabPosition:'top',
    tabRotation:0,
    //标签页靠左配置--end

    activeTab:0,

    items:[{
        title:'条目',
        iconCls: 'x-tab-entry-icon',
        xtype:'dynamicform',
        calurl:'/management/getCalValue',
        items:[{
            xtype:'hidden',
            name:'entryid'
        }]
    }],

    buttons:[{
        text:'返回',
        itemId:'back'
    }]
});