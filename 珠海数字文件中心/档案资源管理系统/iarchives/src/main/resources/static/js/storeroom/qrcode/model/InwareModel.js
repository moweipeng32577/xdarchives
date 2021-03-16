/**
 * Created by zengdw on 2018/05/09 0001.
 */
Ext.define('Qrcode.model.InwareModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'inid', type: 'string'},
        {name: 'confirmtime', type: 'string'},
        {name: 'description', type: 'string'},
        {name: 'warenum', type: 'string'},
        {name: 'waretime', type: 'string'},
        {name: 'waretype', type: 'string'},
        {name: 'wareuser', type: 'string'}
    ]
});