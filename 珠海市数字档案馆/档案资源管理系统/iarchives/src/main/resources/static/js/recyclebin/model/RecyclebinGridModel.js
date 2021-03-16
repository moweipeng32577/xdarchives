/**
 * Created by RonJiang on 2018/04/23
 */
Ext.define('Recyclebin.model.RecyclebinGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string',mapping:'recycleid'},
        {name: 'filename', type: 'string'},
        {name: 'filetype', type: 'string'},
        {name: 'filepath', type: 'string'},
        {name: 'filesize', type: 'string'},
        {name: 'deletetime', type: 'string'},
        {name: 'originaltable', type: 'string'}
    ]
});