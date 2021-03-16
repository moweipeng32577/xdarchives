Ext.define('DigitalInspection.view.EntryFormView',{
    extend:'Ext.tab.Panel',
    xtype:'EntryFormView',

    //标签页靠左配置--start
    tabPosition:'top',
    tabRotation:0,
    //标签页靠左配置--end

    activeTab:0,

    items:[{
        title:'条目',
        iconCls: 'x-tab-entry-icon',
        xtype:'dynamicform',
        calurl:'/acquisition/getCalValue',
        items:[{
            xtype:'hidden',
            name:'entryid'
        }]
    },{
        title:'原始文件',
        iconCls:'x-tab-electronic-icon',
        entrytype:'capture',
        xtype:'electronic'
    },{
        title:'利用文件',
        iconCls:'x-tab-electronic-icon',
        entrytype:'solid',
        xtype:'solid'
    }
    ],

    buttons:[
        {
            text:'保存',
            itemId:'save'
        },'-',
        {
            text:'返回',
            itemId:'back'
        }
    ]
});