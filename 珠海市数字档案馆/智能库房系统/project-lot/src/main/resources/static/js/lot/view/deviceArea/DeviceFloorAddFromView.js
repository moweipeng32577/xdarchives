/**
 * Created by Administrator on 2019/11/12.
 */

Ext.define('Lot.view.deviceArea.DeviceFloorAddFromView', {
    extend: 'Ext.window.Window',
    xtype: 'DeviceFloorAddFromView',
    title: '增加楼层',
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
                    name:'floorid',
                    hidden:true

                }, {
                xtype: 'textfield',
                fieldLabel: '楼层名称<span style="color: #CC3300; padding-right: 2px;">*</span>',
                columnWidth: 1,
                allowBlank: false,
                blankText: '该输入项为必输项',
                name: 'floorName',
                labelWidth: 100
            }, {
                xtype: 'textfield',
                fieldLabel: '楼层编码',
                columnWidth: 1,
                name: 'floorCode',
                labelWidth: 100,
                margin:'10 0 0 0'
            }, {
                xtype: 'textfield',
                fieldLabel: '描述',
                columnWidth: 1,
                name: 'description',
                labelWidth: 100,
                margin:'10 0 0 0'
            },{
                columnWidth: 1,
                xtype : 'textfield',
                name:'floorMap',
                fieldLabel: '楼层平面图',
                labelWidth: 100,
                margin:'10 0 0 0'
            }
            ]
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