/**
 * Created by Administrator on 2020/7/21.
 */


Ext.define('ManageCenter.model.ManageCenterDataModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string', mapping: 'entryid'},
        {name: 'name', type: 'string',mapping:'title'},
        {name: 'url', type: 'string',mapping:'background',convert:function (value,record) {
            return encodeURIComponent(value);
        }}
    ]
});
