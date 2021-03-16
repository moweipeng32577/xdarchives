/**
 * Created by RonJiang on 2018/03/08
 */
Ext.define('ClassifySearch.model.ReportGridModel',{
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