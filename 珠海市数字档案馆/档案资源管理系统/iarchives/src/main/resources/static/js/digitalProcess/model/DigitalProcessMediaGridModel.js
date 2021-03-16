/**
 * Created by Administrator on 2019/9/20.
 */


Ext.define('DigitalProcess.model.DigitalProcessMediaGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string'},
        {name: 'filename', type: 'string'},
        {name: 'status', type: 'string'}
    ]
});
