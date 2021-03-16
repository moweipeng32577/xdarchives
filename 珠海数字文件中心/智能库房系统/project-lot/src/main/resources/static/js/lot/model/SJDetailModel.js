/**
 * Created by Rong on 2019-03-01.
 */
Ext.define('Lot.model.SJDetailModel',{
    extend:'Ext.data.Model',
    fields:[{
        name:'warningTime',
        convert:function(value, record){
            return new Date(value).format("yyyy-MM-dd HH:mm:ss");
        }
    },{
        name:'createTime',
        convert:function(value, record){
            return new Date(value).format("yyyy-MM-dd HH:mm:ss");
        }
    },{
        name:'host',
        convert:function(value, record){
            var host = record.get('description').split(":")[0];
            return host;
        }
    },{
        name:'warningType',
        convert:function(value, record){
            var warningType = record.get('warningType');
            return warningType;
        }}]
});