/**
 * Created by wangmh on 2019/3/5.
 */

Ext.define('DigitalProcess.store.DigitalReportStore',{
    extend:'Ext.data.Store',
    model:'DigitalProcess.model.DigitalReportModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    data: [
        {reportid:"1",reportname:"房地产产权处理档案"},
        {reportid:"1",reportname:"房地产产权处理档案_套打"},
        {reportid:"1",reportname:"房地产交易登记档案"},
        {reportid:"1",reportname:"房地产交易登记档案_套打"},
        {reportid:"2",reportname:"卷内目录"},
        {reportid:"2",reportname:"卷内目录_套打"},
        {reportid:"3",reportname:"成品完成量统计报表"}
    ],
    fields:["reportid,reportname"],

});
