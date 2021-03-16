/**
 * Created by Rong on 2019-01-17.
 */
Ext.define('Lot.view.AFDetailView',{
    extend:'Lot.view.DeviceDetailView',
    xtype:'AFDetail',
    views:{
        scrollable: true,
        xtype: 'grid',
        store: 'DeviceAlarmStore',
        columns: [
            {xtype: 'rownumberer', align: 'center', width: 40},
            {text: '时间', dataIndex: 'createTime', flex: 1},
            {text: '报警类型', dataIndex: 'warningType', flex: 1},
            {text: '描述', dataIndex: 'description', flex: 1}
        ],
        tbar: ['选择日期：', {
            xtype: 'datefield',
            blankText: '起始日期',
            itemId: 'beginDate'
        }, '-', {
            xtype: 'datefield',
            blankText: '结束日期',
            itemId: 'endDate'
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
                        var gridstore=comboBox.ownerCt.findParentByType('AFDetail').down('grid').getStore();
                        gridstore.setPageSize(XD.pageSize);
                        gridstore.loadPage(1);
                    },
                    change:function(comboBox){
                        var pSize = comboBox.getValue();
                        var gridstore=comboBox.ownerCt.findParentByType('AFDetail').down('grid').getStore(); //重写加载store
                        comboBox.ownerCt.pageSize=parseInt(pSize);
                        gridstore.setPageSize(parseInt(pSize));
                        gridstore.loadPage(1);
                    }
                }}]
        }
    },
    setting:{
        xtype:'AFSettingForm'
    },

    onRender:function(){
        this.callParent();
        var status = this.device.get('status');
        this.renderProtectStatus(this.down('[itemId=northPanel]'), status);

        var store = this.down('grid').getStore();
        store.proxy.url = '/deviceAlarm/grid/'+ this.device.get('id');
        store.load();
    },

    command:function(opt){
        var status = opt == 1?2:3;
        Ext.Ajax.request({
            url:'/device/status',
            params:{
                deviceid:this.device.get('id'),
                status:status
            },
            method:'POST',
            scope:this,
            success:function (response) {
                var res = Ext.decode(response.responseText).status;
                XD.msg('操作成功！');

                //修改设备状态后，更改数据源中的状态
                var deviceStore = Ext.getStore('DeviceStore');
                deviceStore.queryRecords('id',this.device.get('id'))[0].set('status', res);

                //渲染布防文字
                var panel = this.down('[itemId=northPanel]');
                this.renderProtectStatus(panel, res);
            }
        });
    },

    renderProtectStatus:function(panel, status){
        panel.remove(panel.down('[itemId=prostatus]'));
        if(status == 2){
            this.down('[itemId=northPanel]').add({
                itemId:'prostatus',
                margin:'30 10 30 10',
                html:'<span style="font: 16px sans-serif;color:red;">布防中...</span>'
            });
        }
    }
});