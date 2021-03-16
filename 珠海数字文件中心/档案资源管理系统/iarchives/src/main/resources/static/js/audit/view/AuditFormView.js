Ext.define('Audit.view.AuditFormView',{
    extend:'Ext.tab.Panel',
    xtype:'AuditFormView',

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
        entrytype:'capture',
        xtype:'electronic'
    },{
        title:'利用文件',
        iconCls:'x-tab-electronic-icon',
        entrytype:'solid',
        xtype:'solid'
    }],

    buttons:[{
        xtype: "label",
        itemId:'tips',
        style:{color:'red'},
        hidden: true,
        text:'温馨提示：红色外框表示输入非法数据！',
        margin:'6 2 5 4'
    },'-',{
        text:'返回',
        itemId:'back'
    }]
});