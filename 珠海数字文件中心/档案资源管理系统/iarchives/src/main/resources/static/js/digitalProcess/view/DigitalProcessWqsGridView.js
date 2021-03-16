Ext.define('DigitalProcess.view.DigitalProcessWqsGridView',{
    extend:'Comps.view.BasicGridView',
    xtype:'DigitalProcessWqsGridView',
    searchstore: [
        {item: "archivecode", name: "档号"},
        {item: "entrysigner", name: "实体签收人"}
    ],
    tbar:{
        overflowHandler:'scroller',
        items:[
            {
                text:'签收',
                iconCls:'fa fa-check',
                itemId:'sign'
            }
        ]
    },
    columns:[
        {text: 'id', dataIndex: 'id', width: 150, menuDisabled: true,hidden:true},
        {text: '档号', dataIndex: 'archivecode', width: 150, menuDisabled: true},
        {text: '实体签收人', dataIndex: 'entrysigner', width: 150, menuDisabled: true},
        {text: 'A0页数', dataIndex: 'a0', width: 150, menuDisabled: true},
        {text: 'A1页数', dataIndex: 'a1', width: 150, menuDisabled: true},
        {text: 'A2页数', dataIndex: 'a2', width: 150, menuDisabled: true},
        {text: 'A3页数', dataIndex: 'a3', width: 150, menuDisabled: true},
        {text: 'A4页数', dataIndex: 'a4', width: 150, menuDisabled: true},
        {text: '折算A4页数', dataIndex: 'za4', width: 150, menuDisabled: true},
        {text: 'A4页数', dataIndex: 'a4', width: 150, menuDisabled: true},
        {text: '折算A4页数', dataIndex: 'za4', width: 150, menuDisabled: true}
    ],
    store: 'DigitalProcessWqsGridStore',
    hasSelectAllBox:true
});