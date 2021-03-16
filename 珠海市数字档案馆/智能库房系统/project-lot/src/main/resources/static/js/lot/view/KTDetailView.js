/**
 * 空调视图
 * Created by Rong on 2019-01-17.
 */
Ext.define('Lot.view.KTDetailView',{
    extend:'Lot.view.DeviceDetailView',
    xtype:'KTDetail',
    views:{
        listeners:{
            render : function(){
                var panel = this.up('KTDetail');
                var setform = panel.down('environmentSettingForm');
                //屏蔽启动和停止按钮
                // setform.controlHidden();
                // panel.up('window').setHeight(280);
            }
        }
    },
    views:{
        scrollable: true,
        xtype: 'grid',
        store: new Ext.data.Store({
            fields:['use','icard','date','operation'],
        }),
        columns: [
            {xtype: 'rownumberer', align: 'center', width: 20},
            {text: '采集时间', dataIndex: 'time', flex: 1},
            {text: '类型', dataIndex: 'type', flex: 1},
            {text: '值', dataIndex: 'value', flex: 1},
            {text: '是否正常', dataIndex: 'isNormal', flex: 1}
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
        },'-',{
            text:'设备诊断',
            itemId:'deviceDiagnoseBtn'
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
                        var gridstore=comboBox.ownerCt.findParentByType('KTDetail').down('grid').getStore();
                        gridstore.setPageSize(pageSize);
                        gridstore.loadPage(1);
                    },
                    change:function(comboBox){
                        var pSize = comboBox.getValue();
                        var gridstore=comboBox.ownerCt.findParentByType('KTDetail').down('grid').getStore(); //重写加载store
                        comboBox.ownerCt.pageSize=parseInt(pSize);
                        gridstore.setPageSize(parseInt(pSize));
                        gridstore.loadPage(1);
                    }
                }
            }]
        },
        listeners:{
            render : function(){
                var panel = this.up('KTDetail');
                var setform = panel.down('environmentSettingForm');
                //屏蔽启动和停止按钮
                // setform.controlHidden();
            }
        }
    },

    setting:{
        layout:'column',
        xtype:'environmentSettingForm'
    },

    command:function(temp, humi){
        var prop = Ext.decode(this.device.get('prop'));
        Ext.Ajax.request({
            url:'/environment/command/' + prop.ip + '/' + prop.sensor + '/' + temp + '/' + humi,
            method:'GET',
            success:function (response) {
                if(response.responseText == 0){
                    XD.msg('操作成功！');
                }else{
                    XD.msg('操作失败！');
                }
            }
        });
    }
});