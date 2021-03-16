/**
 * Created by Administrator on 2019/3/15.
 */

Ext.define('Management.model.ManagementMissPageDetailModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string', mapping: 'id'},
        {name: 'archivecode', type: 'string'},
        {name: 'page', type: 'string'},
        {name: 'elenumber', type: 'string'},
        {name: 'result', type: 'string'}
    ]
});
