Ext.define('Lot.view.DeviceDiagnoseAddView',{
    extend:'Ext.window.Window',
    layout:'fit',
    xtype:'deviceDiagnoseAddView',
    modal:true,
    items:[{
        xtype:'form',
        layout:'column',
        bodyPadding:10,
        defaults:{
            xtype:'textfield',
            labelAlign:'right',
            labelWidth:70,
            margin:'5 5 0 5'
        },
        items:[{
            fieldLabel: 'id',
            itemId:'id',
            name:'id',
            xtype: 'textfield',
            columnWidth:1,
            hidden:true
        },{
            fieldLabel: '故障名<span style="color: #CC3300; padding-right: 2px;">*</span>',
            itemId:'diagnosename',
            name:'diagnosename',
            xtype: 'textfield',
            columnWidth:1,
            allowBlank: false,
            blankText: '该输入项为必输项'
        },{
            columnWidth:.5,
            fieldLabel: '故障编号',
            itemId:'diagnosecode',
            name:'diagnosecode',
            xtype: 'textfield',

        },{
            columnWidth:.5,
            fieldLabel: '故障描述',
            itemId:'faultcause',
            name:'faultcause',
            xtype: 'textfield',

        },{
            columnWidth:1,
            fieldLabel: '建议',
            itemId:'suggest',
            name:'suggest',
            xtype: 'textfield',
            height:'100px'
        },{
            columnWidth:1,
            fieldLabel: '创建时间',
            itemId:'createdate',
            name:'createdate',
            xtype: 'textfield',
            hidden:true
        },{
            columnWidth:1,
            fieldLabel: '修改时间',
            itemId:'modifydate',
            name:'modifydate',
            xtype: 'textfield',
            hidden:true
        }]
    }],
    buttons:[{text:'保存',itemId:'save'},{text:'取消',itemId:'cancel'}]
});