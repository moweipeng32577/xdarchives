/**
 * Created by Rong on 2019-03-01.
 */
Ext.define('Lot.model.DeviceWorkModel',{
    extend:'Ext.data.Model',
    fields:[{name:'deviceName',
            mapping:'device.name'
    },{
        name:'deviceType',
        mapping:'device.type'
    },{
        name:'workType',
        convert:function(value, record){
            if(record.get('workType') == 0){
                value = '恒温恒湿';
            }else if(record.get('workType') == 1){
                value = '安防';
            }
            return value;
        }
    },{
        name:'status',
        convert:function(value, record){
            if(record.get('status') == 0){
                value = '失效';
            }else if(record.get('status') == 1){
                value = '生效';
            }
            return value;
        }
    }]
});