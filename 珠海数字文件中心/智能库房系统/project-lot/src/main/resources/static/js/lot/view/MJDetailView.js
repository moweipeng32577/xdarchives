/*
 门禁页面
 * */

var pageSize=25;
Ext.define('Lot.view.MJDetailView',{
    extend:'Lot.view.DeviceDetailView',  //DeviceDetailView
    xtype:'MJDetail',
    views:{
        scrollable: true,
        xtype: 'grid',
        store: 'MJDetailStore',
        columns: [
            {xtype: 'rownumberer', align: 'center', width: 40},
            {text: '采集时间', dataIndex: 'captureTime', flex: 1,align:'center'},
            {text: '操作人', dataIndex: 'operator', flex: 1,align:'center'},
            {text: '操作方式', dataIndex: 'operateType', flex: 1,align:'center'},
            {text: '门号', dataIndex: 'door', flex: 1,align:'center'}
        ],
        tbar: ['选择日期：', {
            xtype: 'datefield',
            blankText: '起始日期',
            itemId: 'beginDate'
        }, '-', {
            xtype: 'datefield',
            blankText: '结束日期',
            itemId: 'endDate'
        },'-', {
            text:'查询',
            itemId:'query',
            handler:function () {
                var MJ = this.up('MJDetail');
                var startTime = MJ.down('[itemId=beginDate]').getValue().format("yyyy-MM-dd HH:mm:ss");
                var endTime = MJ.down('[itemId=endDate]').getValue().format("yyyy-MM-dd") + ' 23:59:59';
                var deviceid = MJ.device.id;
                var store = MJ.down('grid').getStore();
                store.proxy.url = '/device/histories';
                store.proxy.extraParams.deviceid = deviceid;
                store.proxy.extraParams.startTime = startTime;
                store.proxy.extraParams.endTime = endTime;
                store.load();
            }
        }, '-', {
            text: '开门',
            xtype: 'button',
            itemId: 'open'
        },'-',{
            text: '常开',
            xtype: 'button',
            itemId: 'normalOpen'
        },'-',{
            text: '常关',
            xtype: 'button',
            itemId: 'normalClose'
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
                        var gridstore=comboBox.ownerCt.findParentByType('MJDetail').down('grid').getStore();
                        gridstore.setPageSize(pageSize);
                        gridstore.loadPage(1);
                    },
                    change:function(comboBox){
                        var pSize = comboBox.getValue();
                        var gridstore=comboBox.ownerCt.findParentByType('MJDetail').down('grid').getStore(); //重写加载store
                        comboBox.ownerCt.pageSize=parseInt(pSize);
                        gridstore.setPageSize(parseInt(pSize));
                        gridstore.loadPage(1);
                    }
                }}]
        }
    }
});