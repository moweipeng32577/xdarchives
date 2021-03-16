/**
 * Created by Administrator on 2019/10/26.
 */


Ext.define('TransforAuditDeal.model.ElectronicVersionGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string', mapping:'id'},
        {name: 'version', type: 'string'},
        {name: 'createtime', type: 'string'},
        {name: 'filesize', type: 'string',convert: function(value) {
            var filesize = parseFloat(value)/1024;
            return filesize.toFixed(1)+"k";
        }},
        {name: 'createname', type: 'string'},
        {name: 'remark', type: 'string'}
    ]
});