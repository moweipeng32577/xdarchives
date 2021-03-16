/**
 * 温湿度传感器详细界面
 * Created by Rong on 2019-01-17.
 */
Ext.define('Lot.view.HTDetailView',{
    extend:'Lot.view.DeviceDetailView',
    xtype:'HTDetail',
    views:{
        layout:'border',
        items:[{
            region:'center',
            xtype: 'grid',
            itemId:'temgrid',
            store: 'SpeedHistoryStore',
            columns: [
                {xtype: 'rownumberer', align: 'center', width: 40},
                {text: '采集时间', dataIndex: 'captureTime', flex: 2},
                {text: '温度(℃)', dataIndex: 'tem', flex: 1},
                {text: '湿度(％)', dataIndex: 'hum', flex: 1}
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
                    var HT = this.up('HTDetail');
                    var deviceid = HT.device.id;
                    var store = HT.down('grid').getStore();
                    store.proxy.extraParams.deviceid = deviceid;
                    var startValue = HT.down('[itemId=beginDate]').getValue();
                    var endValue = HT.down('[itemId=endDate]').getValue();
                    var startTime;
                    var endTime;
                    if(startValue != null && endValue != null){
                        startTime = startValue.format("yyyy-MM-dd") + ' 00:00:01';
                        endTime = endValue.format("yyyy-MM-dd") + ' 23:59:59';
                    }else{
                        startTime = null;
                        endTime = null;
                    }
                    store.proxy.extraParams.startTime = startTime;
                    store.proxy.extraParams.endTime = endTime;
                    store.load();
                }
            }
            // ,'-',{
            //     text:'设备诊断',
            //     itemId:'deviceDiagnoseBtn'
            // }
            , '-', {
                text: '打印',
                itemId: 'printBtn',
                menu: [{
                    text: '记录表',
                    itemId: 'HTRecordPrint'
                }, {
                    text: '曲线表',
                    itemId: 'HTCruvePrint'
                }]
            }, '（标准温度：14-24℃，标准湿度：45-60％）'],
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
                            var gridstore=comboBox.ownerCt.findParentByType('HTDetail').down('grid').getStore();
                            gridstore.setPageSize(pageSize);
                            gridstore.loadPage(1);
                        },
                        change:function(comboBox){
                            var pSize = comboBox.getValue();
                            var gridstore=comboBox.ownerCt.findParentByType('HTDetail').down('grid').getStore(); //重写加载store
                            comboBox.ownerCt.pageSize=parseInt(pSize);
                            gridstore.setPageSize(parseInt(pSize));
                            gridstore.loadPage(1);
                        }
                    }}]
            }
        }]
    }
});