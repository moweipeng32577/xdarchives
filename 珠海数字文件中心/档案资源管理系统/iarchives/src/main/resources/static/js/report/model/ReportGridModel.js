/**
 * Created by RonJiang on 2018/2/27
 */
Ext.define('Report.model.ReportGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string',mapping:'reportid'},
        {name: 'reportname', type: 'string'},
        {name: 'modul', type: 'string'},
        {name: 'reporttype', type: 'string'},
        {name: 'printfieldnamelist', type: 'string'},
        {name: 'orderfieldname', type: 'string'}
    ]
});