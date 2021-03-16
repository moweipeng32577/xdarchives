/**
 * Created by Administrator on 2020/7/21.
 */
Ext.define('Affair.model.ProjectLogLookGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string'},
        {name: 'operate_user', type: 'string'},
        {name: 'startTime', type: 'string'},
        {name: 'desci', type: 'string',
            convert:function (value,record) {
                if(value!=null){
                    var start = value.indexOf("操作描述：")+5;
                    var end = value.indexOf("记录");
                    return value.slice(start,end)
                }
        }}
    ]
});