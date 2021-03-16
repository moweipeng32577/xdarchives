/**
 * Created by yl on 2017/10/26.
 */
Ext.define('ElectronApprove.model.ElectronApproveGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string',mapping:'entryid'},
        {name: 'title', type: 'string'},
        {name: 'filenumber', type: 'string'},
        {name: 'archivecode', type: 'string'},
        {name: 'funds', type: 'string'},
        {name: 'catafog', type: 'string'},
        {name: 'lyqx', type: 'string'}
    ]
});