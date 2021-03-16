/**
 * Created by yl on 2017/10/25.
 */
Ext.define('Import.model.ImportGridModel', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'source', type: 'string'},
        {name: 'target', type: 'string'}
    ]
});