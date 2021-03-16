/**
 * Created by RonJiang on 2017/11/28 0028.
 */
Ext.define('DestructionBill.view.DestructionBillEntryFormView',{
    extend:'Ext.tab.Panel',
    xtype:'destructionbillEntryFormView',
    itemId:'destructionbillEntryFormView',
    //标签页靠左配置--start
    tabPosition:'top',
    tabRotation:0,
    //标签页靠左配置--end

    activeTab:0,

    items:[{
        title:'条目',
        iconCls: 'x-tab-entry-icon',
        itemId:'destructionbilldynamicform',
        xtype:'destructionbilldynamicform',
        //设置默认calurl（数据审核模块initFormField方法中已对该属性重新赋值为/acquisition/getCalValue，其它模块采用默认url设置）
        //不使用此公共组件时，自行在对应视图中设置calurl属性的值
        calurl:'/management/getCalValue',
        items:[{
            xtype:'hidden',
            name:'entryid'
        }]
    },{
        title:'原始文件',
        iconCls:'x-tab-electronic-icon',
        itemId:'electronic',
        entrytype:'electronic',
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
        text:'返回',
        itemId:'back'
    }]
});