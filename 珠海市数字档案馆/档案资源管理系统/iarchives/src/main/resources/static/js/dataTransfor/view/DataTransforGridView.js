Ext.define('DataTransfor.view.DataTransforGridView',{
    extend:'Comps.view.EntryGridView',
    xtype:'dataTransforGridView',
    dataUrl:'/management/entries',
    tbar: [{
        xtype: 'button',
        text: '数据转移',
        iconCls:'fa fa-columns',
        itemId: 'getPreview'
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
    },
    hasSelectAllBox:true
});