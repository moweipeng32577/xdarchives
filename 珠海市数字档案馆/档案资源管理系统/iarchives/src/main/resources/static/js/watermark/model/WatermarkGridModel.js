/**
 * Created by tanly on 2017/10/24 0024.
 */
Ext.define('Watermark.model.WatermarkGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string', mapping: 'id'},
        {name: 'isdefault', type: 'string', mapping: 'isdefault',
            convert:function (value,record) {
                if (record.get('isdefault')==1) {
                    value = "是";
                }else{
                    value = "否";
                }
                return value;
            }
        },
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
        {name: 'isrepeat', type: 'string', mapping: 'isrepeat',
            convert:function (value,record) {
                if (record.get('isrepeat')==1) {
                    value = "是";
                }else{
                    value = "否";
                }
                return value;
            }
        },
    ]
});