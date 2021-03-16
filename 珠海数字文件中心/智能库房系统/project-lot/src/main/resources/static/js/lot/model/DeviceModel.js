/**
 * Created by Rong on 2019-03-01.
 */
Ext.define('Lot.model.DeviceModel',{
    extend:'Ext.data.Model',
    fields:[{
        name:'id'
    },{
        name:'type'
    },{
        name:'name'
    },{
        name:'status'
    },{
        name:'area'
    }, {
        name:'statusStr',
        convert:function(value, record){
            if(record.get('status') == 0){
                value = '<span style="color: red">离线</span>';
            } else {
                value = '<span style="color: green">在线</span>';
            }
            return value;
        }
    }]
});