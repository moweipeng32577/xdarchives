/**
 * Created by Rong on 2017/10/25.
 */
Ext.define('ReturnWare.view.ManagementGridView',{
    extend:'Comps.view.EntryGridView',
    xtype:'managementgridView',
    dataUrl:'/management/storageNoEntries',//要筛选未在库状态的条目
    //tbar:functionButton,
    tbar:[/*{
        text:'确认',
        itemId:'idsBack'
    },*/{
        text:'添加',
        itemId:'setBookmarks',
        iconCls:'fa fa-plus-circle'
    },{
        text:'查看添加',
        iconCls:'fa fa-eye',
        itemId:'viewInvare'
    },{
        text:'著录',
        iconCls:'fa fa-plus-circle',
        itemId:'save'
    },{
        text:'高级检索',
        iconCls:'fa fa-tripadvisor',
        itemId:'advancedsearch'
    },{
        text:'返回',
        iconCls:'fa fa-arrow-left',
        itemId:'back'
    }],
    title:'当前节点：',
    searchstore:{
        proxy: {
            type: 'ajax',
            url:'/template/queryName',
            extraParams:{nodeid:0},
            reader: {
                type: 'json',
                rootProperty: 'content',
                totalProperty: 'totalElements'
            }
        }
    },
    hasSelectAllBox:true
});