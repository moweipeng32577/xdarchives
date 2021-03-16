Ext.define('DigitalInspection.view.DigitalInspectionWcGridView',{
    extend:'Comps.view.BasicGridView',
    xtype:'DigitalInspectionWcGridView',
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
                text:'验收',
                iconCls:'fa fa-check',
                itemId:'accept'
            },'-',{
                text:'数据检测和校验',
                iconCls:'fa fa-pencil-square-o',
                itemId:'md5'
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
    store: 'DigitalInspectionWcGridStore',
    hasSelectAllBox:true
});