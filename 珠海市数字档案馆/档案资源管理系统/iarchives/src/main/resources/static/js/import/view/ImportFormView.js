/**
 * Created by SunK on 2018/7/31 0031.
 */
Ext.define('Import.view.ImportFormView',{
    extend:'Ext.tab.Panel',
    xtype:'importform',

    //标签页靠左配置--start
    tabPosition: 'top',
    tabRotation: 0,
    //标签页靠左配置--end

    activeTab: 0,

    items: [{
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
    ],

    buttons: [{
        text: '保存(Ctrl+S)',
        itemId: 'save'
    }, '-', {
        text: '连续录入(Ctrl+Shift+S)',
        itemId: 'continuesave'
    }, '-', {
        text: '返回',
        itemId: 'back'
    }]
});
