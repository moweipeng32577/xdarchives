Ext.define('Lot.view.DeviceInformationAddView',{
    extend:'Ext.window.Window',
    layout:'fit',
    xtype:'deviceInformationAddView',
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
            itemId:'inforid',
            name:'inforid',
            xtype: 'textfield',
            columnWidth:1,
            hidden:true
        },{
            fieldLabel: '设备名称<span style="color: #CC3300; padding-right: 2px;">*</span>',
            itemId:'devicename',
            name:'devicename',
            xtype: 'textfield',
            columnWidth:1,
            allowBlank: false,
            blankText: '该输入项为必输项',
        },{
            columnWidth:.5,
            fieldLabel: '设备编号',
            itemId:'devicecode',
            name:'devicecode',
            xtype: 'textfield',

        },{
            columnWidth:.5,
            fieldLabel: '制造商',
            itemId:'manufacturers',
            name:'manufacturers',
            xtype: 'textfield',

        },{
            xtype: 'datefield',
            itemId: 'installdate',
            format: 'Y-m-d',
            style: 'width:100%',
            columnWidth:.5,
            fieldLabel: '安装日期',
            name:'installdate'
        },{
            columnWidth:.5,
            fieldLabel: '联系方式',
            itemId:'pthone',
            name:'pthone',
            xtype: 'textfield',
        },{
            columnWidth:.5,
            fieldLabel: '管理人员',
            itemId:'adminuser',
            name:'adminuser',
            xtype: 'textfield',
        },{
            columnWidth:.5,
            fieldLabel: '保养周期',
            itemId:'maintenance',
            name:'maintenance',
            xtype: 'textfield',
        }]
    }],
    buttons:[{text:'保存',itemId:'save'},{text:'取消',itemId:'cancel'}]
});