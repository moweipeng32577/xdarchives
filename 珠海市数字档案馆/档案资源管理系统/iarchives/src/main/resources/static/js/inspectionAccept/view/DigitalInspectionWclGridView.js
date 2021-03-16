Ext.define('DigitalInspection.view.DigitalInspectionWclGridView',{
    extend:'Comps.view.BasicGridView',
    xtype:'DigitalInspectionWclGridView',
    searchstore: [
        {item: "batchcode", name: "批次号"},
        {item: "batchname", name: "批次名"},
        {item: "archivetype", name: "档案类型"},
        {item: "inspector", name: "抽检员"}
    ],
    tbar:{
        overflowHandler:'scroller',
        items:[
            {
                text:'新增批次',
                iconCls:'fa fa-plus-circle',
                itemId:'add'
            },'-',{
                text:'设置抽检',
                iconCls:'fa fa-pencil-square-o',
                itemId:'sampling'
            },'-',{
                text:'删除',
                iconCls:'fa fa-trash-o',
                itemId:'del'
            }
        ]
    },
    columns:[
        {text: '批次号', dataIndex: 'batchcode', flex: 2, menuDisabled: true},
        {text: '批次名', dataIndex: 'batchname', flex: 2, menuDisabled: true},
        {text: '档案类型', dataIndex: 'archivetype', flex: 2, menuDisabled: true},
        {text: '份数', dataIndex: 'copies', flex: 2, menuDisabled: true},
        {text: '页数', dataIndex: 'pagenum', flex: 2, menuDisabled: true},
        {text: '抽检员', dataIndex: 'inspector', flex: 2, menuDisabled: true},
        {text: '抽检率', dataIndex: 'checkcount', flex: 2, menuDisabled: true},
        {text: '状态', dataIndex: 'status', flex: 2, menuDisabled: true}
    ],
    store: 'DigitalInspectionWclGridStore',
    hasSelectAllBox:true
});