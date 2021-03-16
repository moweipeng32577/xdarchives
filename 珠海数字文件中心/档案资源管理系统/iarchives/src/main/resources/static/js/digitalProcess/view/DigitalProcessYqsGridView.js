Ext.define('DigitalProcess.view.DigitalProcessYqsGridView',{
    extend:'Comps.view.BasicGridView',
    xtype:'DigitalProcessYqsGridView',
    searchstore: [
        {item: "archivecode", name: "档号"},
        {item: "entrysigner", name: "实体签收人"}
    ],
    tbar:{
        overflowHandler:'scroller',
        items:[
            // ,'-',
            {
                text:'著录',
                hidden:true,
                iconCls:'fa fa-plus-circle',
                itemId:'record'
            },{
                text:'完成',
                iconCls:'fa fa-check',
                itemId:'finish'
            },
            {
                text:'查看',
                iconCls:'fa fa-eye',
                itemId:'look'
            },
            {
                text:'审核并整理',
                iconCls:'fa  fa-cogs',
                hidden:true,
                itemId:'audit'
            }
        ]
    },
    columns:[
        {text: 'id', dataIndex: 'id', flex: 2, menuDisabled: true,hidden:true},
        {text: '档号', dataIndex: 'archivecode', flex: 2, menuDisabled: true},
        {text: '实体签收人', dataIndex: 'entrysigner', flex: 2, menuDisabled: true},
        {text: '扫描状态', dataIndex: 'scanstate', flex: 2, menuDisabled: true},
    ],
    store: 'DigitalProcessYqsGridStore',
    hasSelectAllBox:true
});