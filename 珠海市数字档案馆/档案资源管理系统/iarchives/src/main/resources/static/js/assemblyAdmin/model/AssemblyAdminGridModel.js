Ext.define('AssemblyAdmin.model.AssemblyAdminGridModel', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string'},
        {name: 'title', type: 'string'},
        {name: 'code', type: 'string'},
        {name: 'creator', type: 'string'},
        {
            name: 'createtime',type:"string"
            // convert: function (value) {
            //     return new Date(parseInt(value)).format("yyyy-MM-dd hh:mm:ss");
            // }
        }
    ]
});
