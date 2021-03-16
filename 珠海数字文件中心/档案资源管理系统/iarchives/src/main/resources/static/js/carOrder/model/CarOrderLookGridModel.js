/**
 * Created by Administrator on 2020/4/23.
 */


Ext.define('CarOrder.model.CarOrderLookGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'node', type: 'string'},
        {name: 'spman', type: 'string'},
        {name: 'status', type: 'string',convert: function(value, record) {
            if(value=='完成'){
                return "已审核";
            }
            return "未审核";
        }},
        {name: 'spdate', convert: function(value, record) {
            if(!value){
                return "";
            }
            var year = value.substring(0,4);
            var month = value.substring(4,6);
            var day = value.substring(6,8);
            var time = value.substring(8,10);
            var minute = value.substring(10);
            return year+'年'+month+"月"+day+"日"+time+"时"+minute+"分";
        }},
        {name: 'approve', type: 'string'}
    ]
});
