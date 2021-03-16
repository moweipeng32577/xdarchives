Ext.define('DigitalProcess.store.DigitalProcessTreeStore',{
    extend:'Ext.data.TreeStore',
    model:'DigitalProcess.model.DigitalProcessTreeModel',
    autoLoad:true,
    proxy: {
        type: 'ajax',
        url: '/digitalProcess/getFlowsTree',
        extraParams:{assemblyid:''},
        reader: {
            type: 'json',
            expanded: true
        }
    },
    root: {
        text: '流程环节',
        expanded: true,
    }
});