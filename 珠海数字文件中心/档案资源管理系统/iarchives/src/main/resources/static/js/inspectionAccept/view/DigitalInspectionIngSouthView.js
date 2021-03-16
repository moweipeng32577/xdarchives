var innerGrid = {
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
                    store:'DigitalInspectionEntryGridStore',
                    columns:[
                        {text: '批次号', dataIndex: 'batchcode', flex: 2, menuDisabled: true},
                        {text: '案卷号', dataIndex: 'filecode', flex: 2, menuDisabled: true},
                        {text: '档号', dataIndex: 'archivecode', flex: 2, menuDisabled: true},
                        {text: '页数', dataIndex: 'pagenum', flex: 2, menuDisabled: true},
                        {text: '状态', dataIndex: 'status', flex: 2, menuDisabled: true},
                        {text: '抽检人', dataIndex: 'checker', flex: 2, menuDisabled: true}
                    ]
                };
Ext.define('DigitalInspection.view.DigitalInspectionIngSouthView', {
    extend: 'Ext.tab.Panel',
    xtype:'DigitalInspectionIngSouthView',
    requires: [
        'Ext.layout.container.Border'
    ],
    tabPosition: 'top',
    tabRotation: 0,
    activeTab: 0,
    items: [{
        title: '我的抽检',
        layout: 'border',
        items:[
            innerGrid
        ]
    }, {
        title: '他人抽检',
        layout: 'border',
        items:[
            innerGrid
        ]
    }, {
        title: '未抽检',
        layout: 'border',
        items:[
            innerGrid
        ]
    }
    ],
});

