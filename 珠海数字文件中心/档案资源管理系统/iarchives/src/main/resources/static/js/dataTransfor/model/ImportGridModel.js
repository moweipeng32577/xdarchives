/**
 * Created by yl on 2017/10/25.
 */
Ext.define('DataTransfor.model.ImportGridModel', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'fieldName', type: 'string'},
        {name: 'targetFieldName', type: 'string'},
        {name: 'fieldCode', type: 'string'},
        {name: 'targetFieldCode', type: 'string'}
    ]
});