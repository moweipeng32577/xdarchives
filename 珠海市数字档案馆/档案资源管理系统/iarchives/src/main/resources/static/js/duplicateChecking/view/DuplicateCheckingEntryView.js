/**
 * Created by tanly on 2018/2/8 0008.
 */
Ext.define('DuplicateChecking.view.DuplicateCheckingEntryView',{
    extend:'Ext.tab.Panel',
    xtype:'duplicateCheckingEntryView',

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
    },{
        title:'原始文件',
        iconCls:'x-tab-electronic-icon',
        entrytype:'management',
        xtype:'electronic'
    },{
        title:'利用文件',
        iconCls:'x-tab-electronic-icon',
        itemId:'solid',
        entrytype:'solid',
        xtype:'solid'
    }
    // ,{
    //     title:'长期存储文件',
    //     iconCls:'x-tab-electronic-icon',
    //     itemId:'long',
    //     entrytype:'long',
    //     xtype:'long'
    // }
    ],

    buttons:[{
        text:'保存(Ctrl+S)',
        itemId:'save'
    },'-',{
        text:'返回',
        itemId:'back'
    }]
});