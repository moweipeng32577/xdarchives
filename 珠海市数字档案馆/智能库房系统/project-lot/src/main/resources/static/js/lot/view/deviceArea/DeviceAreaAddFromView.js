/**
 * Created by Administrator on 2019/11/12.
 */

 var daviceAreaStore=  Ext.create('Ext.data.Store',{
    fields: [
        {name: 'code'},
        {name: 'name'}
    ],
    data:[
        ['kf','库房'],
        ['area','区域'],
    ]
});

Ext.define('Lot.view.deviceArea.DeviceAreaAddFromView', {
    extend: 'Ext.window.Window',
    xtype: 'DeviceAreaAddFromView',
    title: '增加分区',
    width: 500,
    height: 400,
    closeToolText: '关闭',
    modal: true,
    resizable: false,
    layout: 'fit',
    items: [
        {
            xtype: 'form',
            layout: 'column',
            bodyPadding: 20,
            items: [
                {
                    xtype:'textfield',
                    name:'id',
                    hidden:true

                }, {
                xtype: 'textfield',
                fieldLabel: '名称<span style="color: #CC3300; padding-right: 2px;">*</span>',
                columnWidth: 1,
                allowBlank: false,
                blankText: '该输入项为必输项',
                name: 'name',
                labelWidth: 100
            }, {
                xtype: 'textfield',
                fieldLabel: '编码',
                columnWidth: 1,
                name: 'code',
                labelWidth: 100,
                margin:'10 0 0 0'
            }, {
                itemId:'areaId',
                xtype: 'combo',
                store:daviceAreaStore,
                value:'kf',
                valueField:'code',
                displayField:'name',
                fieldLabel: '类型<span style="color: #CC3300; padding-right: 2px;">*</span>',
                columnWidth: 1,
                allowBlank: false,
                blankText: '该输入项为必输项',
                name: 'type',
                labelWidth: 100,
                margin:'10 0 0 0'
            },{
                columnWidth: 1,
                xtype : 'combobox',
                store : 'FloorStore' ,
                name:'floor',
                fieldLabel: '楼层<span style="color: #CC3300; padding-right: 2px;">*</span>',
                allowBlank: false,
                blankText: '该输入项为必输项',
                emptyText: '请选择楼层',
                itemId:'floorid',
                displayField: "floorName",
                valueField: "floorid",
                queryMode: "local",
                margin:'10 0 0 0'
            },{
                xtype: 'textfield',
                fieldLabel: '档案类别',
                columnWidth: 1,
                itemId: 'archivestypeId',
                name: 'archivestype',
                labelWidth: 100,
                margin:'10 0 0 0',
                allowBlank: true,
            }]
        }
    ],
    buttons: [{
        itemId: 'saveBtnID',
        text: '保存'
    }, {
        itemId: 'BackBtnID',
        text: '返回'
    }]
});