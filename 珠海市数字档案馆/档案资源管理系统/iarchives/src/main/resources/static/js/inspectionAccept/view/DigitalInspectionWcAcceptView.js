/**
 * 表格与表单视图
 */
Ext.define('DigitalInspection.view.DigitalInspectionWcAcceptView',{
    extend:'Ext.panel.Panel',
    xtype:'DigitalInspectionWcAcceptView',
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
                    flex:3,
                    itemId:'northgrid',
                    xtype:'DigitalInspectionWcAcceptGridView'
                },{
                    flex:2,
                    header:false,
                    itemId:'AcceptSouthgrid',
                    xtype:'DigitalInspectionWcAcceptSouthView',
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
    store:'DigitalInspectionWcAcceptEntryGridStore',
    columns:[
        {text: '批次号', dataIndex: 'batchcode', flex: 2, menuDisabled: true},
        {text: '案卷号', dataIndex: 'filecode', flex: 2, menuDisabled: true},
        {text: '档号', dataIndex: 'archivecode', flex: 2, menuDisabled: true},
        {text: '页数', dataIndex: 'pagenum', flex: 2, menuDisabled: true},
        {text: '状态', dataIndex: 'status', flex: 2, menuDisabled: true}
    ]
};


Ext.define('DigitalInspection.view.DigitalInspectionWcAcceptSouthView', {
    extend: 'Ext.tab.Panel',
    xtype:'DigitalInspectionWcAcceptSouthView',
    requires: [
        'Ext.layout.container.Border'
    ],
    tabPosition: 'top',
    tabRotation: 0,
    activeTab: 0,
    items: [{
        title: '已验收',
        layout: 'border',
        items:[
            WcSouthInnerGrid
        ]
    }, {
        title: '已退回',
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