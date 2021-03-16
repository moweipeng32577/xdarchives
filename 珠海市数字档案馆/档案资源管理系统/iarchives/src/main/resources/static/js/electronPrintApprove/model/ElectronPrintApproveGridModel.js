/**
 * Created by Administrator on 2019/5/23.
 */


Ext.define('ElectronPrintApprove.model.ElectronPrintApproveGridModel',{
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