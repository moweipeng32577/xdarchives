Ext.define('DigitalProcess.store.DigitalProcessCalloutTreeStore',{
    extend:'Ext.data.TreeStore',
    model:'DigitalProcess.model.DigitalProcessTreeModel',
    autoLoad:true,
    proxy: {
        type: 'ajax',
        url: '/digitalProcess/getBatchTree',
        extraParams:{batchcode:null},
        reader: {
            type: 'json',
            expanded: true
        }
    },
    root: {
        text: '',
        expanded: true,
    }
});