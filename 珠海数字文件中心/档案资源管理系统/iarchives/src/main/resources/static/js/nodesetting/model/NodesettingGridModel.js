/**
 * Created by tanly on 2017/10/24 0024.
 */
Ext.define('Nodesetting.model.NodesettingGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string', mapping: 'nodeid'},
        {
            name: 'leaf',
            type: 'string',
            convert:function (value,record) {
                value = "否";
                if(record.get('leaf')){
                    value = "是";
                }
                return value;
            }
        }
    ]
});