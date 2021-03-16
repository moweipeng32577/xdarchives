/**
 * Created by Administrator on 2019/6/24.
 */


Ext.define('AcceptDirectory.model.ImportGridModel', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'source', type: 'string'},
        {name: 'target', type: 'string'}
    ]
});
