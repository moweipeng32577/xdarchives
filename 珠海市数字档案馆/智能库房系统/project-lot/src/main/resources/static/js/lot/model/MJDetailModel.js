/**
 * Created by Rong on 2019-03-01.
 */
Ext.define('Lot.model.MJDetailModel',{

    extend:'Ext.data.Model',
    fields:[{
        name:'captureTime',  //获取创建时间
        convert:function(value, record){
            return new Date(value).format("yyyy-MM-dd HH:mm:ss");
        }
    },{
        name:'operator',   //获取操作员
        convert:function(value, record){
            var result = Ext.decode(record.get('captureValue')).operateMan;
            return result;
        }
    },{
        name:'operateType',   //操作方式
        convert:function(value, record){
            var result = Ext.decode(record.get('captureValue')).operateType;
            return result;
        }
    },{
        name:'door',   //门
        convert:function(value, record){
            var result = Ext.decode(record.get('captureValue')).door;
            return result;
        }
    }]
});