Ext.define('CompilationAcquisition.model.ManagementSequenceModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string', mapping:'entryid'},
        {name: 'title', type: 'string'},//档号
        {name: 'archivecode', type: 'string'},//档号
        {name: 'order', type: 'string'},//顺序号
        {name: 'newarchivecode', type: 'string'},//新档号
        {name: 'page', type: 'string'},//页号
        {name: 'pagenum', type: 'string'}//页数
    ]
});