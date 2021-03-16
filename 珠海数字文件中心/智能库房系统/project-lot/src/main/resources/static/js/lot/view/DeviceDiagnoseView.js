Ext.define('Lot.view.DeviceDiagnoseView',{
    extend:'Ext.grid.Panel',
    xtype:'DeviceDiagnoseView',
    // itemId:'deviceDianoseView',
    store:'DeviceDiagnoseLookStore',
    // selType:'checkboxmodel',
    columns: [
        {xtype: 'rownumberer', align: 'center', width: 30},
        {text:'id',dataIndex:'id',flex:1,hidden:true},
        {text: '故障名', dataIndex: 'diagnosename', flex: 1},
        {text: '故障号', dataIndex: 'diagnosecode', flex: 1},
        {text: '故障描述', dataIndex: 'faultcause', flex: 1},
        {text: '建议', dataIndex: 'suggest', flex: 1},
    ],
    bbar:{
        xtype: 'pagingtoolbar',
        displayInfo: true,
        displayMsg: '显示 {0} - {1} 条，共{2}条',
        emptyMsg: "没有数据显示",
        items:['-',{
            xtype:'combo',
            store: new Ext.data.ArrayStore({
                fields: ['text', 'value'],
                data: [['5', 5], ['10', 10], ['25', 25], ['50', 50], ['100', 100], ['300', 300],['1000', 1000]]
            }),
            displayField: 'text',
            value:pageSize,//使用默认分页大小
            valueField: '' +
                '',
            editable: false,
            width: 80,
        }]
    }
});