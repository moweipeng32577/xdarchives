/**
 * Created by Administrator on 2019/2/26.
 */

Ext.define('Acquisition.model.ElectronicVersionGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string', mapping:'id'},
        {name: 'version', type: 'string'},
        {name: 'createtime', type: 'string'},
        {name: 'size', type: 'string',convert: function(value) {
            var filesize = parseFloat(value)/1024;
            return filesize.toFixed(1)+"k";
        }},
        {name: 'createname', type: 'string'},
        {name: 'remark', type: 'string'}
    ]
});
