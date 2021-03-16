/**
 * Created by tanly on 2017/11/8 0024.
 */
Ext.define('Template.model.TemplateGridModel', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string', mapping: 'templateid'}
        // {
        //     name: 'gfield',
        //     type: 'string',
        //     renderer:function (value,record) {
        //         value = "否";
        //         if(record.get('gfield')){
        //             value = "是";
        //         }
        //         return value;
        //     }
        // },
        // {
        //     name: 'qfield',
        //     type: 'string',
        //     renderer:function (value,record) {
        //         value = "否";
        //         if(record.get('qfield')){
        //             value = "是";
        //         }
        //         return value;
        //     }
        // },
        // {
        //     name: 'ffield',
        //     type: 'string',
        //     renderer:function (value,record) {
        //         value = "否";
        //         if(record.get('ffield')){
        //             value = "是";
        //         }
        //         return value;
        //     }
        // }
    ]
});