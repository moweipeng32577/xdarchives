/**
 * 告警信息管理控制器
 * Created by Rong on 2019-06-13.
 */
Ext.define('Lot.controller.DeviceAlarmController', {

    extend: 'Ext.app.Controller',

    models: ['DeviceAlarmModel'],
    stores: ['DeviceAlarmStore'],

    init: function () {
        this.control({
            '[itemId=alarmId] [itemId=alarmAffirmBtn]': { //确认
                click :function (btn) {
                    this.alarmCheck(btn,'1')
                }
            },
            '[itemId=alarmId] [itemId=alarmDenyBtn]':{
                click :function (btn) {
                    this.alarmCheck(btn,'2')
                }
            }
        });
    },


    alarmCheck:function (btn,status) {
        var alarmGrid = btn.up('visualization').down('[itemId=alarmId]');
        var select = alarmGrid.getSelectionModel();
        if (select.getSelection().length == 0) {
            XD.msg('请选择需要修改的数据！！');
            return;
        }
        XD.confirm('确认修改这 ' + select.getSelection().length + ' 条告警信息的状态？', function () {
            var warningIds = [];
            for (var i = 0; i < select.getSelection().length; i++) {
                warningIds.push(select.getSelection()[i].get('warningId'));
            }
            var ids = warningIds.join(',');
            Ext.Msg.wait('正在操作，请耐心等待……');
            Ext.Ajax.request({
                method: 'POST',
                scope: this,
                url: '/deviceAlarm/alarmCheck',
                params: {
                    ids:ids,
                    status:status
                },
                timeout: 5000,
                success: function (response) {
                    var alarmStore = alarmGrid.getStore();
                    alarmStore.reload();
                    Ext.MessageBox.hide();
                }
            })
        })
    }


});