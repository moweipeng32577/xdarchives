/**
 * Created by Administrator on 2019/5/28.
 */


Ext.define('JyAdmins.model.PrintEleDetailGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string'},
        {name: 'filename', type: 'string'},
        {name: 'printstate', type: 'string'},
        {name: 'scopepage', type: 'string'},
        {name: 'copies', type: 'int'},
        {name: 'pass', type: 'string',convert:function (value,record) {
            if(record.data.state==null||record.data.state==""){
                value = "未申请";
            }else if(record.data.state=="同意"){
                value = "通过";
            }else{
                value = "未通过";
            }
            return value;
        }}
    ]
});
