/**
 * Created by xd on 2017/10/21.
 */
Ext.define('Datareceive.model.DatareceiveOpenGridModel', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string'},
        {name: 'filename', type: 'string'},
        {name: 'filepath', type: 'string'},
        {name: 'nodeid', type: 'string'},
        {name: 'currentNode', type: 'string'}
    ]
});