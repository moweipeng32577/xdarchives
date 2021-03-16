/**
 * Created by Rong on 2017/10/25.
 */
Ext.define('Moveware.view.ManagementGridView',{
    extend:'Comps.view.EntryGridView',
    xtype:'managementgridView',
    dataUrl:'/management/inwares',//要筛选在库状态的条目
    //tbar:functionButton,
    tbar:[{
        text:'确认',
        itemId:'idsBack'
    },{
        text:'关闭',
        itemId:'idsClose'
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