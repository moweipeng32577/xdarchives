/**
 * Created by Rong on 2019-01-17.
 */
Ext.define('Lot.view.XFDetailView',{
    extend:'Lot.view.DeviceDetailView',
    xtype:'XFDetail',
    views:{
        scrollable: true,
        xtype: 'grid',
        store:'DeviceHistoryStore',
        columns: [
            {xtype: 'rownumberer', align: 'center', width: 40},
            {text: '采集时间', dataIndex: 'captureTime', flex: 1},
            {text: '报警类型', dataIndex: 'operation', flex: 1}
        ],
        tbar: ['选择日期：', {
            xtype: 'datefield',
            blankText: '起始日期',
            itemId: 'beginDate'
        }, '-', {
            xtype: 'datefield',
            blankText: '结束日期',
            itemId: 'endDate'
        },'-',{
            text:'设备诊断',
            itemId:'deviceDiagnoseBtn'
        }, '-', {
            text: '打印',
            itemId: 'printBtn'
        }],
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
                value:XD.pageSize,//使用默认分页大小
                valueField: 'value',
                editable: false,
                width: 80,
                listeners:{
                    render:function(comboBox){
                        var gridstore=comboBox.ownerCt.findParentByType('XFDetail').down('grid').getStore();
                        gridstore.setPageSize(XD.pageSize);
                        gridstore.loadPage(1);
                    },
                    change:function(comboBox){
                        var pSize = comboBox.getValue();
                        var gridstore=comboBox.ownerCt.findParentByType('XFDetail').down('grid').getStore(); //重写加载store
                        comboBox.ownerCt.pageSize=parseInt(pSize);
                        gridstore.setPageSize(parseInt(pSize));
                        gridstore.loadPage(1);
                    }
                }}]
        }
    }
});