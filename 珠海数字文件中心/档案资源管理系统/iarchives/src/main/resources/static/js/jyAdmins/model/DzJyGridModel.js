/**
 * Created by xd on 2017/10/21.
 */
Ext.define('JyAdmins.model.DzJyGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string',mapping:'userid'},
        {name: 'borrowman', type: 'string'},
        {name: 'borrowmd', type: 'string'},
        {name: 'borroworgan', type: 'string'},
        {name: 'borrowdate', type: 'string'},
        {name: 'desci', type: 'string'},
        {name: 'borrowts', type: 'int'},
        {name: 'borrowcode', type: 'string'}
    ]
});