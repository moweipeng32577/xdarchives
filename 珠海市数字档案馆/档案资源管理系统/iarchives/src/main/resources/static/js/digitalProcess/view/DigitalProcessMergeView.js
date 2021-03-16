Ext.define('DigitalProcess.view.DigitalProcessMergeView', {
    extend: 'Ext.window.Window',
    width:'100%',
    height:'100%',
    header:false,
    xtype:'DigitalProcessMergeView',
    layout:'border',
    items:[{
        region:'center',//中间
        flex:2,//占三分之二
        itemId:'northform',//上方的表单视图
        xtype:'EntryFormView1'
    },{
        region:'south',//南部
        flex:1,//占三分之一
        itemId:'southviewgrid',//下方的表格视图
        xtype:'MergeGridView',//表格类型
        split:true,
        collapsible:true,
        collapseToolText:'收起',
        expandToolText:'展开'
    }]
});

Ext.define('DigitalProcess.view.MergeGridView',{
    extend:'Comps.view.EntryGridView',
    xtype:'MergeGridView',
    dataUrl:'/digitalProcess/szhEntries',
    templateUrl:'/digitalProcess/gridHeader',
    title:'条目字段差异对比 <span style="color:red;">(可双击字段修改表单对应值)</span>',
    tbar: [{
        xtype: 'button',
        text: '提交',
        style:'background:#00EE76;',
        itemId:'save',
        iconCls:'fa fa-check'
    }, '-', {
        text:'退回环节',
        style:'background:#00EE76;',
        iconCls:'fa fa-reply',
        itemId:'backLink'
    },{
        xtype: 'button',
        text: '返回',
        style:'background:#EEC900;',
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
    },
    hasSelectAllBox:true,
    hasSearchBar:false,
});