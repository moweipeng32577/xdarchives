Ext.define('DigitalProcess.view.DigitalProcessWchjGridView',{
    extend:'Comps.view.BasicGridView',
    xtype:'DigitalProcessWchjGridView',
    searchstore: [
        {item: "archivecode", name: "档号"},
        {item: "entrysigner", name: "实体签收人"}
    ],
    tbar:{
        overflowHandler:'scroller',
        items:[
            {
                text:'入库',
                iconCls:'fa fa-check',
                itemId:'putStorage'
            },{
                text:'办理详情',
                iconCls:'fa fa-check    ',
                itemId:'DealDetailsId'
            },{
                text:'打印报表',
                iconCls:'fa fa-print',
                itemId:'printReport'
            }
        ]
    },
    columns:[
        {text: 'id', dataIndex: 'id', width:150, menuDisabled: true,hidden:true},
        {text:'batchcode', dataIndex:'batchcode', hidden:true},
        {text: '档号', dataIndex: 'archivecode', width:150, menuDisabled: true},
        {text: '实体签收人', dataIndex: 'entrysigner', width:150, menuDisabled: true},
        {text: '整理', dataIndex: 'tidy', width:150, menuDisabled: true},
        {text: '扫描', dataIndex: 'scan', width:150, menuDisabled: true},
        {text: '图片处理', dataIndex: 'pictureprocess', width:150, menuDisabled: true},
        {text: '审核', dataIndex: 'audit', width:150, menuDisabled: true},
        {text: '著录', dataIndex: 'record', width:150, menuDisabled: true},
        {text: '属性定义', dataIndex: 'definition', width:150, menuDisabled: true},
        {text: '页数', dataIndex: 'pages', width:150, menuDisabled: true},
        {text: '份数', dataIndex: 'copies', width:150, menuDisabled: true},
        {text: 'A0页数', dataIndex: 'a0', width:150, menuDisabled: true},
        {text: 'A1页数', dataIndex: 'a1', width:150, menuDisabled: true},
        {text: 'A2页数', dataIndex: 'a2', width:150, menuDisabled: true},
        {text: 'A3页数', dataIndex: 'a3', width:150, menuDisabled: true},
        {text: 'A4页数', dataIndex: 'a4', width:150, menuDisabled: true},
        {text: '折算A4页数', dataIndex: 'za4', width:150, menuDisabled: true},
    ],
    store: 'DigitalProcessWchjGridStore',
    hasSelectAllBox:true
});
