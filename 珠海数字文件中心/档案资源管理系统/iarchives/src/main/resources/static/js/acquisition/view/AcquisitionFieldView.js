Ext.define('Acquisition.view.AcquisitionFieldView',{
   	extend: 'Ext.panel.Panel',
    xtype:'acquisitionFieldView',
    header:false,
    layout:'border',
    items:[{
        region:'center',
        itemId:'workspace',
        layout:'border',
        items:[{
        	itemId:'workspace',
            region:'center',
            border:false,
            layout:'border',
            items:[{
                title:'字段设置',
                region:'west',
                width:'100%',
                xtype:'grid',
                itemId:'fieldgrid',
                store:'ImportGridStore',
                columns:[
                    {xtype: 'rownumberer', align: 'center', width:40},
                    {text: '源字段', dataIndex:'fieldName', align: 'center', flex:1},
                    {text: '源字段码', dataIndex: 'fieldCode', hidden: true, menuDisabled: true},
                    {text: '目的字段', dataIndex:'targetFieldName', align: 'center', flex:1, editor:{
                        xtype: 'combo',
                        typeAhead: true,
                        editable: false,
                        triggerAction: 'all',
                        queryMode: 'local',
                        forceSelection:true,
                        store: 'TemplateStore',
                        displayField:'fieldname',
                        valueField:'fieldname'
                    }},
                    {text: '目的字段码', dataIndex: 'targetFieldCode', hidden: true, menuDisabled: true}
                ],
                plugins: {
                    ptype: 'cellediting',
                    clicksToEdit: 1
                }
            }]
        }],
        buttons:[{
	        text:'提交',
	        itemId:'submit'
	    },{
	        text:'退出',
	        itemId:'close'
	    }]
    }]
});