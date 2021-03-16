/**
 * Created by Administrator on 2019/5/18.
 */


Ext.define('MetadataSearch.model.ApplyPrintEleGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string'},
        {name: 'filename', type: 'string'},
        {name: 'printstate', type: 'string'},
        {name: 'scopepage', type: 'string'},
        {name: 'copies', type: 'int'}
    ]
});
