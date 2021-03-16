/**
 * Created by Rong on 2017/10/31.
 */
Ext.define('CompilationAcquisition.view.ManagementFormView',{
    extend:'Ext.tab.Panel',
    xtype:'managementform',

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
        text:'保存(Ctrl+S)',
        itemId:'save'
    },'-',{
        text:'连续录入(Ctrl+Shift+S)',
        itemId:'continuesave'
    }
    // ,'-',{
    //     text:'上一条',
    //     itemId:'backEntry'
    // },'-',{
    //     text:'下一条',
    //     itemId:'nextEntry'
    // }
    ,'-',{
        text:'返回',
        itemId:'back'
    }]
});