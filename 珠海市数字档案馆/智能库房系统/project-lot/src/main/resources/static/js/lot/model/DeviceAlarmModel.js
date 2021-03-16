Ext.define('Lot.model.DeviceAlarmModel',{
    extend:'Ext.data.Model',
    fields:[
        {name:'warningType'},
        {name:'device',mapping:'device.name'},
        {name:'description'},
        {name:'warningTime'},
        {name:'createTime'},
        {name:'status',
            convert:function(value){
                if(value == '1'){
                    return '已确认'
                }
                else{
                    return '未确认'
                }
            }}
        ]
});