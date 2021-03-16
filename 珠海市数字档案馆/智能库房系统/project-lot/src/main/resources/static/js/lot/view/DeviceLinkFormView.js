/**
 * 设备表单视图
 * Created by Rong on 2019-01-18.
 */
Ext.define('Lot.view.DeviceLinkFormView',{
    extend:'Ext.form.FormPanel',
    xtype:'deviceLinkForm',
    bodyPadding:15,
    defaults:{
        xtype:'textfield',
        anchor:'98%'
    },
    items:[{
        name:'id',
        xtype:'hidden'
    },{
        name:'device',
        xtype:'combo',
        store:'DeviceStore',
        valueField:'id',
        displayField:'name',
        forceSelection:true,
        typeAhead:true,
        queryMode:'local',
        fieldLabel: '名称设备<span style="color: #CC3300; padding-right: 2px;">*</span>',
        allowBlank: false,
        blankText: '该输入项为必输项'
    },{
        name:'event',
        xtype:'textfield',
        fieldLabel:'事件名称'
    },{
        name:'linkArea',
        xtype:'combo',
        store:'DeviceAreaStore',
        valueField:'id',
        displayField:'name',
        forceSelection:true,
        typeAhead:true,
        queryMode:'local',
        fieldLabel: '联动分区<span style="color: #CC3300; padding-right: 2px;">*</span>',
        allowBlank: false,
        blankText: '该输入项为必输项'
    },{
        name:'linkDevice',
        xtype:'combo',
        store:'DeviceStore',
        valueField:'id',
        displayField:'name',
        forceSelection:true,
        typeAhead:true,
        queryMode:'local',
        fieldLabel: '联动设备<span style="color: #CC3300; padding-right: 2px;">*</span>',
        allowBlank: false,
        blankText: '该输入项为必输项'
    },{
        name:'linkEvent',
        fieldLabel:'联动事件'
    }]
});