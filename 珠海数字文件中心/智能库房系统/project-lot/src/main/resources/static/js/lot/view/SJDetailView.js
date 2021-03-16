/**
 * 温湿度传感器详细界面
 * Created by Rong on 2019-01-17.
 */
//--------------新增的文件------------------------
Ext.define('Lot.view.SJDetailView',{
    extend:'Lot.view.DeviceDetailView',
    xtype:'SJDetail',
    views:{
        scrollable: true,
        xtype: 'grid',
        store: 'DeviceHistoryStore',
        columns: [
            {xtype: 'rownumberer', align: 'center', width: 40},
            {text: '采集时间', dataIndex: 'captureTime', flex: 1,
                renderer:function(value){
                    var reg=new RegExp('-',"g"); //创建正则RegExp对象
                    return value.replace(reg,'/');
                }},
            {text: '报警总数', dataIndex: 'totalOfAlarm', flex: 1},
            {text: '主机正常数', dataIndex: 'normalNumberOfHosts', flex: 2},
            {text: '主机报警数', dataIndex: 'warningNumberOfHosts', flex: 1},
            {text: '服务正常数', dataIndex: 'normalNumberOfService', flex: 1},
            {text: '服务报警数', dataIndex: 'serviceOfAlarm', flex: 1}

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
            text:'查询',
            itemId:'query',
            handler:function () {
                var SJ = this.up('SJDetail');
                var startTime = SJ.down('[itemId=beginDate]').getValue().format("yyyy-MM-dd HH:mm:ss");
                var endTime = SJ.down('[itemId=endDate]').getValue().format("yyyy-MM-dd") + ' 23:59:59';
                var deviceid = SJ.device.id;
                var store = SJ.down('grid').getStore();
                store.proxy.url = '/water/deviceWarning';//水浸url
                store.proxy.extraParams.deviceid = deviceid;
                store.proxy.extraParams.startTime = startTime;
                store.proxy.extraParams.endTime = endTime;
                store.load();
            }
        }, '-',  '（水浸警告列表）'],
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
                        var gridstore=comboBox.ownerCt.findParentByType('SJDetail').down('grid').getStore();
                        gridstore.setPageSize(pageSize);
                        gridstore.loadPage(1);
                    },
                    change:function(comboBox){
                        var pSize = comboBox.getValue();
                        var gridstore=comboBox.ownerCt.findParentByType('SJDetail').down('grid').getStore(); //重写加载store
                        comboBox.ownerCt.pageSize=parseInt(pSize);
                        gridstore.setPageSize(parseInt(pSize));
                        gridstore.loadPage(1);
                    }
                }}]
        }
    }
});