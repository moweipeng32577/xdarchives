/**
 * Created by Rong on 2017/10/25.
 */
Ext.define('Outware.view.ManagementGridView',{
    extend:'Comps.view.EntryGridView',
    xtype:'managementgridView',
    dataUrl:'/management/storageEntries',//要筛选已入库状态的条目
    //tbar:functionButton,
    tbar:[{
        text:'添加',
        itemId:'addOutwareBookmarks',
        iconCls:'fa fa-plus-circle'
    },{
        text:'查看添加',
        iconCls:'fa fa-eye',
        itemId:'viewOutware'
    },{
        text:'高级检索',
        iconCls:'fa fa-tripadvisor',
        itemId:'advancedsearch'
    },{
        text:'返回',
        iconCls:'fa fa-arrow-left',
        itemId:'back', handler: function (btn) {
            btn.findParentByType('managementView').close();
        }
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