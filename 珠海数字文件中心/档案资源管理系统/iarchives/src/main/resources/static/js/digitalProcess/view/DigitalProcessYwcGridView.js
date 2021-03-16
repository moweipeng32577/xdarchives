Ext.define('DigitalProcess.view.DigitalProcessYwcGridView',{
    extend:'Comps.view.BasicGridView',
    xtype:'DigitalProcessYwcGridView',
    searchstore: [
        {item: "archivecode", name: "档号"},
        {item: "entrysigner", name: "实体签收人"}
    ],
    tbar:{
        overflowHandler:'scroller',
        items:[
           {
                text:'打印',
                hidden:true,
                iconCls:'fa fa-check',
                itemId:'print'
            }
        ]
    },
    columns:[
        {text: 'id', dataIndex: 'id', flex: 2, menuDisabled: true,hidden:true},
        {text: '档号', dataIndex: 'archivecode', flex: 2, menuDisabled: true},
        {text: '实体签收人', dataIndex: 'entrysigner', flex: 2, menuDisabled: true},
        {text: '扫描状态', dataIndex: 'scanstate', flex: 2, menuDisabled: true},
    ],
    store: 'DigitalProcessYwcGridStore',
    hasSelectAllBox:true
});