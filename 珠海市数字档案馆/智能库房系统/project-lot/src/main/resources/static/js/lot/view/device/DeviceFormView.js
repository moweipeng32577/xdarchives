/**
 * 设备表单视图
 * Created by Rong on 2019-01-18.
 */
Ext.define('Lot.view.device.DeviceFormView',{
    extend:'Ext.form.FormPanel',
    xtype:'DeviceFormView',
    bodyPadding:15,
    defaults:{
        xtype:'textfield',
        anchor:'98%'
    },
    items:[{
        name:'id',
        xtype:'hidden'
    },{
        name:'status',
        xtype:'hidden',
        value:1,
    }, {
        name: 'enabled',
        xtype: 'hidden',
    },{
        name:'sort',
        xtype:'hidden',
    },{
        name:'typeName',
        xtype:'hidden',
    },{
        name:'type',
        xtype:'combo',
        itemId:'typeId',
        store:'DeviceTypeStore',
        valueField:'id',
        displayField:'typeName',
        forceSelection:true,
        typeAhead:true,
        fieldLabel:'设备类型',
        listeners : {
            'render':function (combo) {
                var form = combo.up('DeviceFormView')
                form.getForm().findField('typeName').setValue(combo.rawValue)
            }
        }
    },{
        name:'name',
        xtype:'textfield',
        fieldLabel:'设备名称'
    },{
        name:'code',
        fieldLabel:'设备编码'
    },{
        name:'prop',
        xtype:'textarea',
        fieldLabel:'设备属性'
    },{
        name:'brand',
        fieldLabel:'设备品牌'
    },{
        name:'model',
        fieldLabel:'设备型号'
    },{
        name:'coordinate',
        fieldLabel:'坐标大小'
    },{
        xtype : 'combobox',
        store : 'DeviceAreaStore',
        name:'area',
        fieldLabel: '虚拟分区',
        emptyText: '请选择虚拟分区',
        displayField: "name",
        valueField: "id",
        queryMode: "local",
        itemId:'areaId',
        listeners : {
            'render':function (combo) {
               combo.getStore().load();
            }
        }
    }],
    buttons:[{
        text:'保存',
        itemId:'saveBtn'
    },{
        text:'关闭',
        itemId:'closeBtn'
    }]
});