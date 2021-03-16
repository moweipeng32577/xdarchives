/**
 * Created by Rong on 2019-03-25.
 */
Ext.define('Lot.model.DeviceHistoryModel',{
    extend:'Ext.data.Model',
    fields:[{
        name:'captureTime',
        convert:function(value, record){
            return new Date(value).format("yyyy-MM-dd HH:mm:ss");
        }
    },{
        name:'tem',
        convert:function(value, record){
            var tem = Ext.decode(record.get('captureValue')).tem;
            if(tem < 14 || tem > 24){
                return '<span style="color: red">' + tem + '</span>';
            }
            return tem;
        }
    },{
        name:'hum',
        convert:function(value, record){
            var hum = Ext.decode(record.get('captureValue')).hum;
            if(hum < 45 || hum > 60){
                return '<span style="color: red">' + hum + '</span>';
            }
            return hum;
        }
    },{

        name:'operation',
        convert:function(value, record){
            var operation = Ext.decode(record.get('captureValue')).operation;
            return operation;
        }
    }	,{

        name:'operation',
        convert:function(value, record){
            var operation = Ext.decode(record.get('captureValue')).operation;
            return operation;
        }
    },{
        name:'serviceOfAlarm',
        convert:function(value, record) {
            var serviceOfAlarm = Ext.decode(record.get('captureValue')).serviceOfAlarm;
            return serviceOfAlarm;
        }
    },{
        name:'normalNumberOfService',
        convert:function(value, record) {
            var normalNumberOfService = Ext.decode(record.get('captureValue')).normalNumberOfService;
            return normalNumberOfService;
        }
    },{
        name:'normalNumberOfHosts',
        convert:function(value, record) {
            var normalNumberOfHosts = Ext.decode(record.get('captureValue')).normalNumberOfHosts;
            return normalNumberOfHosts;
        }
    },{
        name:'warningNumberOfHosts',
        convert:function(value, record) {
            var warningNumberOfHosts = Ext.decode(record.get('captureValue')).warningNumberOfHosts;
            return warningNumberOfHosts;
        }
    },{
        name:'totalOfAlarm',
        convert:function(value, record) {
            var totalOfAlarm = Ext.decode(record.get('captureValue')).totalOfAlarm;
            return totalOfAlarm;
        }
    }

    ]
});