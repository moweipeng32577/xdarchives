/**
 * Created by SunK on 2020/5/23 0023.
 */
Ext.define('MetadataSearch.view.EntryFormView',{
    extend:'Ext.tab.Panel',
    xtype:'EntryFormView',
    itemId:'EntryFormView',
    //标签页靠左配置--start
    tabPosition:'top',
    tabRotation:0,
    //标签页靠左配置--end

    activeTab:0,

    items:[{
        title:'实体元数据',
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
        entrytype:'management',
        xtype:'electronicPro'
    },{
        title:'利用文件',
        iconCls:'x-tab-electronic-icon',
        entrytype:'solid',
        xtype:'solid'
    }
        // ,{
        //     title:'长期存储文件',
        //     iconCls:'x-tab-electronic-icon',
        //     entrytype:'long',
        //     xtype:'long'
        // }
    ],

    buttons:[{
        xtype:'label',
        itemId:'etips',
        hidden: true,
    },{
        text:'返回',
        itemId:'back'
    }]
});