/**
 * Created by yl on 2017/10/26.
 */
Ext.define('WhthinManage.model.LookBorrowdocMxGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string',mapping:'entryid'},
        {name: 'entrystorage', type: 'string'},//borrowmsgid
        {name: 'title', type: 'string'},
        // {name: 'title', type: 'string',convert:function (value,record) {
        //     if(record.data.responsible<getDateStr(0)){
        //         value = "***";
        //     }
        //     return value;
        // }},
        {name: 'filenumber', type: 'string'},
        // {name: 'filenumber', type: 'string',convert:function (value,record) {
        //     if(record.data.responsible<getDateStr(0)){
        //         value = "***";
        //     }
        //     return value;
        // }},
        {name: 'archivecode', type: 'string'},
        {name: 'funds', type: 'string'},
        {name: 'catafog', type: 'string'},
        {name: 'serial', type: 'string'},//审批通过时间
        {name: 'entrysecurity', type: 'string'},//查档天数
        {name: 'responsible',type:'string'},//到期时间
        {name:'pages',type:'string'}//归还状态
    ]
});

function getDateStr(AddDayCount) {
    var dd = new Date();
    dd.setDate(dd.getDate()+AddDayCount);//获取AddDayCount天后的日期
    var y = dd.getFullYear();
    var m = dd.getMonth()+1;//获取当前月份的日期
    var d = dd.getDate();
    if (m >= 1 && m <= 9) {
        m = "0" + m;
    }
    if (d >= 0 && d <= 9) {
        d = "0" + d;
    }
    return y+""+m+""+d;
}