/**
 * Created by Rong on 2019-03-25.
 */
Ext.define('Lot.model.SpeedHistoryModel',{
    extend:'Ext.data.Model',
    fields:[{
        name:'captureTime'
    },{
        name:'tem',
        convert:function(value){
            if(value < 14 || value > 24){           //温度超标
                return '<span style="color: red">' + value + '</span>';
            }
            return value.toFixed(1);
        }
    },{
        name:'hum',
        convert:function(value){
            if(value > 60 || value < 45){     //湿度超标
                return '<span style="color: red">' + value + '</span>';
            }
            return value.toFixed(1);
        }
    }]
});