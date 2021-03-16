/**
 * Created by Rong on 2018/4/27.
 */
Ext.define('Lot.view.deviceWork.DeviceWorkFormView',{
    extend:'Ext.window.Window',
    layout:'fit',
    xtype:'deviceWorkFormView',
    modal:true,
    items:[{
        xtype: 'form',
        layout: 'column',
        bodyPadding: 10,
        defaults: {
            xtype: 'textfield',
            labelAlign: 'right',
            labelWidth: 70,
            margin: '5 5 0 5'
        },
        items: [
            {
                name: 'workId',
                hidden:true
            },{
                name: 'mode',
                xtype: 'combo',
                itemId: 'modeId',
                store: [['open', '开启（布防）'], ['close', '关闭（撤防）']],
                forceSelection: true,
                typeAhead: true,
                columnWidth: 1,
                fieldLabel: '作业模式<span style="color: #CC3300; padding-right: 2px;">*</span>',
                allowBlank: false,
                blankText: '该输入项为必输项',
            }, {
                columnWidth: .5,
                fieldLabel: '设备类型<span style="color: #CC3300; padding-right: 2px;">*</span>',
                blankText: '该输入项为必输项',
                xtype: 'combo',
                itemId:'daviceTypeId',
                store: new Ext.data.JsonStore({
                    root: 'list',
                    autoLoad: true,
                    totalProperty: 'count',
                    idProperty: 'id',
                    fields: ['typeCode', 'typeName'],
                    proxy: new Ext.data.HttpProxy({
                        url: '/deviceType/getDeviceType'
                    })
                }),
                valueField: 'typeCode',
                displayField: 'typeName',
                queryMode: 'all',
                name: 'deviceType',
                editable: false,
            }, {
                columnWidth: .5,
                fieldLabel: '设备<span style="color: #CC3300; padding-right: 2px;">*</span>',
                allowBlank: false,
                blankText: '该输入项为必输项',
                xtype: 'combo',
                itemId: 'deviceId',
                store: new Ext.data.JsonStore({
                    root: 'list',
                    autoLoad: false,
                    totalProperty: 'count',
                    idProperty: 'id',
                    fields: ['id', 'name'],
                    proxy: new Ext.data.HttpProxy({
                        url: '/device/devices',
                    })
                }),
                valueField: 'id',
                displayField: 'name',
                queryMode: 'all',
                name: 'device',
                allowBlank: false,
                editable: false,
                listeners:{
                    expand:function (comboBox,record, index) {
                        var device= this.up('deviceWorkFormView').down('[itemId=deviceId]');
                        var type= this.up('deviceWorkFormView').down('[itemId=daviceTypeId]').getValue();
                        var deviceStore = device.getStore();
                        deviceStore.proxy.extraParams.deviceType =type;
                        deviceStore.load();
                    }
                }
            }, {
                columnWidth: .5,
                itemId: 'period',
                xtype: 'combo',
                fieldLabel: '周期<span style="color: #CC3300; padding-right: 2px;">*</span>',
                allowBlank: false,
                blankText: '该输入项为必输项',
                store:[['day','每天'],['workday','工作日']],
                name: 'period',
                value: 'day'
            }, {
                xtype: "timefield",
                columnWidth: .5,
                fieldLabel: '作业时间<span style="color: #CC3300; padding-right: 2px;">*</span>',
                allowBlank: false,
                blankText: '该输入项为必输项',
                name: 'workTime',
                itemId:"workTimeId",
                format: "H:i:s",
                invalidText: "时间格式无效",
                value:'08:30:00'
            }]
    }],
    buttons:[{text:'保存',itemId:'save'},{text:'取消',itemId:'cancel'}]
})