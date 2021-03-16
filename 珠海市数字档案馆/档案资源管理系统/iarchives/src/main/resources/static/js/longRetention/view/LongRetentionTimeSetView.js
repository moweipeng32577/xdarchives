/**
 * Created by yl on 2020/1/3.
 * 定时处理配置
 */
var weekly = Ext.create("Ext.data.Store", {
    fields: ["Name", "Value"],
    data: [
        {Name: "星期一", Value: 'MON'},
        {Name: "星期二", Value: 'TUE'},
        {Name: "星期三", Value: 'WED'},
        {Name: "星期四", Value: 'THU'},
        {Name: "星期五", Value: 'FRI'},
        {Name: "星期六", Value: 'SAT'},
        {Name: "星期日", Value: 'SUN'}
    ]
});
var monthly = Ext.create("Ext.data.Store", {
    fields: ["Name", "Value"],
    data: [
        {Name: "一月", Value: 'JAN'},
        {Name: "二月", Value: 'FEB'},
        {Name: "三月", Value: 'MAR'},
        {Name: "四月", Value: 'APR'},
        {Name: "五月", Value: 'MAY'},
        {Name: "六月", Value: 'JUN'},
        {Name: "七月", Value: 'JUL'},
        {Name: "八月", Value: 'AUG'},
        {Name: "九月", Value: 'SEP'},
        {Name: "十月", Value: 'OCT'},
        {Name: "十一月", Value: 'NOV'},
        {Name: "十二月", Value: 'DEC'}
    ]
});
var runcycle = Ext.create("Ext.data.Store", {
    fields: ["Name", "Value"],
    data: [
        {Name: "每日", Value: 'day'},
        {Name: "每月", Value: 'month'},
        {Name: "每周", Value: 'week'}
    ]
});
var startTime = Ext.create("Ext.data.Store", {
    fields: ["Name", "Value"],
    data: [
        {Name: "上午00时", Value: '00'},
        {Name: "上午01时", Value: '01'},
        {Name: "上午02时", Value: '02'},
        {Name: "上午03时", Value: '03'},
        {Name: "上午04时", Value: '04'},
        {Name: "上午05时", Value: '05'},
        {Name: "上午06时", Value: '06'},
        {Name: "上午07时", Value: '07'},
        {Name: "上午08时", Value: '08'},
        {Name: "上午09时", Value: '09'},
        {Name: "上午10时", Value: '10'},
        {Name: "上午11时", Value: '11'},
        {Name: "上午12时", Value: '12'},
        {Name: "下午01时", Value: '13'},
        {Name: "下午02时", Value: '14'},
        {Name: "下午03时", Value: '15'},
        {Name: "下午04时", Value: '16'},
        {Name: "下午05时", Value: '17'},
        {Name: "下午06时", Value: '18'},
        {Name: "下午07时", Value: '19'},
        {Name: "下午08时", Value: '20'},
        {Name: "下午09时", Value: '21'},
        {Name: "下午10时", Value: '22'},
        {Name: "下午11时", Value: '23'}
    ]
});
Ext.define('LongRetention.view.LongRetentionTimeSetView', {
    extend: 'Ext.window.Window',
    xtype: 'longRetentionTimeSetView',
    itemId: 'longRetentionTimeSetViewid',
    title: '定时任务设置',
    width: 500,
    height: 300,
    modal: true,
    closeToolText: '关闭',
    closeAction: "hide",
    layout: 'fit',
    items: [{
        layout: {
            type: 'vbox',
            align: 'stretch'
        },
        xtype: 'form',
        itemId: 'formitemid',
        margin: '20',
        items: [{
            columnWidth: .1,
            xtype: 'combobox',
            store: runcycle,
            name: 'runcycle',
            itemId: 'runcycleid',
            fieldLabel: '运行周期',
            queryMode: 'local',
            allowBlank: false,
            displayField: 'Name',
            valueField: 'Value',
            editable: false,
            listeners: {
                select: function (combo, record) {
                    var monthly = combo.up("longRetentionTimeSetView").down("[itemId=monthlyid]");
                    var weekly = combo.up("longRetentionTimeSetView").down("[itemId=weeklyid]");
                    if (combo.getValue() == 'day') {
                        monthly.setDisabled(true);
                        weekly.setDisabled(true);
                        monthly.setValue(null);
                        weekly.setValue(null);
                    }else if (combo.getValue() == 'month'){
                        monthly.select(monthly.getStore().getAt(0));
                        monthly.setDisabled(false);
                        weekly.setDisabled(true);
                        weekly.setValue(null);
                    }else if (combo.getValue() == 'week'){
                        weekly.select(weekly.getStore().getAt(0));
                        weekly.setDisabled(false);
                        monthly.setDisabled(true);
                        monthly.setValue(null);
                    }else{
                        monthly.setDisabled(false);
                        weekly.setDisabled(false);
                    }
                }
            }
        }, {
            columnWidth: .1,
            xtype: 'combobox',
            store: monthly,
            name: 'monthly',
            itemId: 'monthlyid',
            fieldLabel: '每月设置',
            queryMode: "local",
            allowBlank: false,
            displayField: 'Name',
            valueField: 'Value',
            editable: false,
            disabled:true
        }, {
            columnWidth: .1,
            xtype: 'combobox',
            store: weekly,
            name: 'weekly',
            itemId: 'weeklyid',
            fieldLabel: '每周设置',
            queryMode: 'local',
            allowBlank: false,
            displayField: 'Name',
            valueField: 'Value',
            editable: false,
            disabled:true
        }, {
            columnWidth: .1,
            xtype: 'combobox',
            name: 'starttime',
            fieldLabel: '任务开始时间',
            store: startTime,
            editable: false,
            displayField: 'Name',
            valueField: 'Value',
            queryMode: 'local'
        }, {
            columnWidth: .1,
            xtype: 'textfield',
            fieldLabel: '任务状态',
            name: 'jobstate',
            style: 'width: 100%',
            readOnly:true
        }]
    }]
    ,
    buttons: [{
        text: '停止',
        itemId: 'stop',
        hidden:true
    }, {
        text: '启动',
        itemId: 'start',
        hidden:true
    }, {
        text: '保存',
        itemId: 'save'
    }, {
        text: '返回', handler: function (view) {
            view.up('longRetentionTimeSetView').close();
        }
    }]
});
