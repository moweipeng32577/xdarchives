/**
 * Created by xd on 2017/10/21.
 */
Ext.define('Borrow.model.ElectronBorrowTreeModel', {
    extend: 'Ext.data.Model',
    fields: [{name: "id", type: "string",mapping: "fnid"},
        {name: "text", type: "string", mapping: "text"},
        {name: "leaf", type: "boolean"}]
});
