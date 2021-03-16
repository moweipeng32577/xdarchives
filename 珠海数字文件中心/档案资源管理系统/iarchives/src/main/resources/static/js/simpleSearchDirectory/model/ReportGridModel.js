/**
 * Created by Administrator on 2019/9/12.
 */
Ext.define('SimpleSearchDirectory.model.ReportGridModel',{
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
