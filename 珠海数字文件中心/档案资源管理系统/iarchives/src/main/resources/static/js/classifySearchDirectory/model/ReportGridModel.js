/**
 * Created by Administrator on 2019/9/23.
 */
Ext.define('ClassifySearchDirectory.model.ReportGridModel',{
    extend:'Ext.data.Model',
    fields:[
        {name:'id',type:'string',mapping:'reportid'},
        {name:'reportname',type:'string'},
        {name:'modul',type:'string'},
        {name:'reporttype',type:'string'},
        {name:'printfieldnamelist',type:'string'},
        {name:'orderfieldname',type:'string'}
    ]
});