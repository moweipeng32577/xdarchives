/**
 * 表格与表单视图
 */
Ext.define('DigitalInspection.view.DigitalInspectionWcView',{
    extend:'Ext.panel.Panel',
    xtype:'DigitalInspectionWcView',
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
                    xtype:'DigitalInspectionWcGridView'
                },{
                    flex:2.5,
                    header:false,
                    itemId:'southgrid',
                    xtype:'DigitalInspectionWcSouthView',
                    collapsible:true,
                    collapseToolText:'收起',
                    expandToolText:'展开',
                    collapsed: false,
                    split:true,
                    allowDrag:true,
                    hasSearchBar:false,
                    expandOrcollapse:'expand',//默认打开
                    // store:'DigitalInspectionEntryGridStore',
                    // columns:[
                    //     {text: '批次号', dataIndex: 'batchcode', flex: 2, menuDisabled: true},
                    //     {text: '案卷号', dataIndex: 'filecode', flex: 2, menuDisabled: true},
                    //     {text: '档号', dataIndex: 'archivecode', flex: 2, menuDisabled: true},
                    //     {text: '页数', dataIndex: 'pagenum', flex: 2, menuDisabled: true},
                    //     {text: '状态', dataIndex: 'status', flex: 2, menuDisabled: true}
                    // ],
                    // tbar:[]
                }]
        }
        ]
});


var WcSouthInnerGrid = {
    layout:'fit',
    region: 'center',
    header:false,
    xtype:'entrygrid',
    collapsible:true,
    collapseToolText:'收起',
    expandToolText:'展开',
    collapsed: false,
    split:true,
    allowDrag:true,
    hasSearchBar:false,
    expandOrcollapse:'expand',//默认打开
    store:'DigitalInspectionWcEntryGridStore',
    columns:[
        {text: '批次号', dataIndex: 'batchcode', flex: 2, menuDisabled: true},
        {text: '案卷号', dataIndex: 'filecode', flex: 2, menuDisabled: true},
        {text: '档号', dataIndex: 'archivecode', flex: 2, menuDisabled: true},
        {text: '页数', dataIndex: 'pagenum', flex: 2, menuDisabled: true},
        {text: '状态', dataIndex: 'status', flex: 2, menuDisabled: true},
        {text: '抽检人', dataIndex: 'checker', flex: 2, menuDisabled: true}
    ]
};


Ext.define('DigitalInspection.view.DigitalInspectionWcSouthView', {
    extend: 'Ext.tab.Panel',
    xtype:'DigitalInspectionWcSouthView',
    requires: [
        'Ext.layout.container.Border'
    ],
    tabPosition: 'top',
    tabRotation: 0,
    activeTab: 0,
    items: [{
        title: '通过',
        layout: 'border',
        items:[
            WcSouthInnerGrid
        ]
    }, {
        title: '退回',
        layout: 'border',
        items:[
            WcSouthInnerGrid
        ]
    }, {
        title: '未检查',
        layout: 'border',
        items:[
            WcSouthInnerGrid
        ]
    }
    ],
});