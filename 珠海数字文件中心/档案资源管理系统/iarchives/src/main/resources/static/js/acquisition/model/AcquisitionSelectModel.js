Ext.define('Acquisition.model.AcquisitionSelectModel', {
    extend: 'Ext.data.Model',
    fields: [{name: "id", type: "string",mapping: "OrganID"},
        {name: "text", type: "string", mapping: "text"},
        {name: "leaf", type: "boolean"}]
});