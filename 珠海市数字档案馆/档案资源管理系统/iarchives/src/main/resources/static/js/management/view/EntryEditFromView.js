/**
 * Created by zengdw on 2019/07/04.
 */
Ext.define('Management.view.EntryEditFromView',{
    extend:'Ext.tab.Panel',
    xtype:'entryEditFromView',
    //标签页靠左配置--start
    tabPosition:'top',
    tabRotation:0,
    //标签页靠左配置--end
    activeTab:0,
    items:[{
        title:'条目',
        xtype:'dynamicform',
        calurl:'/management/getCalValue',
        items:[{
            xtype:'hidden',
            name:'entryid'
        }]
    },{
        title:'原始文件',
        iconCls:'x-tab-electronic-icon',
        entrytype:'manegement',
        xtype:'electronic'
    }],
    buttons:[
        {
            xtype:'button',
            itemId:'ygdPreBtn',
            text:'上一条',
            margin:'-2 2 5 0'
        },{
            xtype:'button',
            itemId:'ygdNextBtn',
            text:'下一条',
            margin:'-2 2 5 2'
        },{
            xtype:'label',
            itemId:'ygdTotalText',
            text:'',
            style:{color:'red'},
            margin:'6 2 5 4'
        },{
            xtype:'label',
            itemId:'ygdNowText',
            text:'',
            style:{color:'red'},
            margin:'6 2 5 6'
        }, {
            text: '保存',
            margin:'-2 2 5 2',
            itemId:'saveArchivecodeBtn'
        }, {
            text: '关闭',
            margin:'-2 2 5 2',
            itemId:'closeEntryWinBtn'
        }]
});
