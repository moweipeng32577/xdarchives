/**
 * Created by tanly on 2017/10/24 0024.
 */
Ext.define('Organ.model.OrganGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string', mapping: 'organid'},
        {
            name: 'status',
            type: 'string',
            convert:function (value,record) {
                value = "启用";
                if (record.get('usestatus')==0) {
                    value = "停用";
                }
                return value;
            }
        },
        {
            name: 'organtype',
            type: 'string',
            convert:function (value,record) {
                value = "单位";
                if (record.get('organtype')=='department') {
                    value = "部门";
                }
                return value;
            }
        }
    ]
});