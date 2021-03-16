/**
 * Created by RonJiang on 2018/4/20 0020.
 */
Ext.define('Dataopen.view.DataopenDocEntryGridView',{
    extend:'Comps.view.EntryGridView',
    xtype:'dataopenDocEntryGridView',
    dataUrl:'/dataopen/docEntry',
    hasCloseButton:false,
    tbar:[{
    	text:'查看条目',
    	iconCls:'fa fa-eye',
    	itemId:'look'
    },{
        text:'返回',
        iconCls:'fa fa-undo',
        itemId:'back'
    }],
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
    }
});