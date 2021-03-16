Ext.define('Dataopen.view.DataopenDocumentInfoView',{
    extend: 'Ext.form.Panel',
    xtype: 'dataopenDocumentInfoView',
    itemId:'dataopenDocumentInfoViewId',
    region: 'center',
    autoScroll: true,
    fieldDefaults: {
        labelWidth: 110
    },
    layout:'column',
    bodyPadding: '10 30 5 30',
    items:[{ xtype: 'textfield',name:'id',hidden:true},
        {
            columnWidth:.96,
            xtype: 'textfield',
            fieldLabel: '单据题名',
            name:'doctitle',
            itemId:'doctitleItem',
            editable:false,
            margin:'5 0 0 0'
        },{
            columnWidth: .47,
            xtype: 'textfield',
            fieldLabel: '送审人',
            itemId:'submitterItem',
            name:'submitter',
            editable:false,
            margin:'10 0 0 0'
        },{
	        columnWidth: .02,
	        margin:'10 0 0 5'
	    },{
            columnWidth: .47,
            xtype: 'textfield',
            fieldLabel: '开放批次号',
            name:'batchnum',
            editable:false,
            margin:'10 0 0 0'
        },{
            columnWidth: .47,
            fieldLabel: '送审时间',
            xtype: 'textfield',
            name: 'submitdate',
            format: 'Ymd',
            editable:false,
            margin:'10 0 0 0'
        },{
	        columnWidth: .02,
	        margin:'10 0 0 5'
	    },{
            columnWidth: .47,
            xtype: 'textfield',
            fieldLabel: '条目总数',
            allowBlank:false,
            name:'entrycount',
            editable:false,
            margin:'10 0 0 0'
        },{
            columnWidth: .47,
            xtype : 'textfield',
            name:'opentype',
            fieldLabel: '开放类型',
            displayField : 'text',
            valueField : 'value',
            editable:false,
            margin:'10 0 0 0'
        },{
	        columnWidth: .02,
	        margin:'10 0 0 5'
	    },{
        	columnWidth: .47,
            xtype: 'textfield',
            fieldLabel: '单据状态',
            name:'state',
            editable:false,
            margin:'10 0 0 0'
        },{
        	columnWidth: .96,
            xtype: 'textarea',
            fieldLabel: '单据批示',
            name:'approve',
            editable:false,
            margin:'10 0 0 0'
        },{
            columnWidth: .96,
            xtype: 'textarea',
            fieldLabel: '备注信息',
            name:'remarks',
            editable:false,
            margin:'10 0 0 0'
        }
    ],
    buttons: [
        { text: '返回',itemId:'back'}
    ]
});