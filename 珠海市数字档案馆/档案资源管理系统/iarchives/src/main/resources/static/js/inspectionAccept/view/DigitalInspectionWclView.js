/**
 * 表格与表单视图
 */
Ext.define('DigitalInspection.view.DigitalInspectionWclView',{
    extend:'Ext.panel.Panel',
    xtype:'DigitalInspectionWclView',
    items:[
        {
            itemId:'pairgrid',
            layout:{
                type:'vbox',
                pack: 'start',
                align: 'stretch'
            },
            items:[
                {
                    flex:2.5,
                    itemId:'northgrid',
                    xtype:'DigitalInspectionWclGridView'
                },{
                    flex:2.5,
                    title:'批次条目',
                    itemId:'southgrid',
                    xtype:'entrygrid',
                    collapsible:true,
                    collapseToolText:'收起',
                    expandToolText:'展开',
                    collapsed: true,
                    split:true,
                    allowDrag:true,
                    hasSearchBar:false,
                    expandOrcollapse:'expand',//默认打开
                    store:'DigitalInspectionEntryGridStore',
                    columns:[
                        {text: '批次号', dataIndex: 'batchcode', flex: 2, menuDisabled: true},
                        {text: '案卷号', dataIndex: 'filecode', flex: 2, menuDisabled: true},
                        {text: '档号', dataIndex: 'archivecode', flex: 2, menuDisabled: true},
                        {text: '页数', dataIndex: 'pagenum', flex: 2, menuDisabled: true},
                        {text: '状态', dataIndex: 'status', flex: 2, menuDisabled: true}
                    ],
                    tbar:[]
                }]
        }
        ]
});