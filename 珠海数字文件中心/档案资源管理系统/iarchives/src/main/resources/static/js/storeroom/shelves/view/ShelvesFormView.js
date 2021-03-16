/**
 * Created by Rong on 2018/4/27.
 */
Ext.define('Shelves.view.ShelvesFormView',{
    extend:'Ext.window.Window',
    layout:'fit',
    xtype:'shelveswindow',
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
            hidden: true,
            name:'zoneid'
        },{
            columnWidth:.5,
            fieldLabel: '城区名称',
            name:'citydisplay'
        },{
            columnWidth:.5,
            xtype:'numberfield',
            maxLength:2,
            fieldLabel: '城区编码',
            value:'01',
            name:'city'
        },{
            columnWidth:.5,
            fieldLabel: '单位名称',
            name:'unitdisplay'
        },{
            columnWidth:.5,
            xtype:'numberfield',
            maxLength:2,
            fieldLabel: '单位编码',
            value:'01',
            name:'unit'
        },{
            columnWidth:1,
            fieldLabel: '楼层名称',
            name:'floor',
            xtype : 'combo',
            store : 'FloorStore',
            itemId:'floorId',
            editable:false,
            displayField : 'floorName',
            valueField : 'floorid',
            allowBlank:false,
            emptyText:'选择楼层',
            blankText : '选择楼层',
            listeners: {
                select:function (combo) {
                    var form = combo.up('form');
                    var roomDisplay = form.down('[itemId = floordisplayId]');
                    roomDisplay.setValue(combo.rawValue);

                    form.down('[itemId = roomId]').setValue();
                    form.down('[itemId = deviceId]').setValue();
                    var roomStore = form.down('[itemId = roomId]').getStore();
                    roomStore.load({
                        url:'/deviceArea/getRooms',
                        params:{
                            floorid:this.value.trim()
                        }
                    });
                }
            }
        },{
            hidden:true,
            itemId:'floordisplayId',
            name:'floordisplay'
        },{
            columnWidth:.5,
            fieldLabel: '库房名称',
            name:'room',
            xtype : 'combo',
            store : 'RoomStore',
            itemId:'roomId',
            editable:false,
            style: "margin-left:12px",
            displayField : 'name',
            valueField : 'id',
            allowBlank:false,
            emptyText:'选择库房',
            blankText : '选择库房',
            listeners: {
                expand:function (combo) {
                    var form = combo.up('form');
                    var floorcombo = form.down('[itemId = floorId]');
                    var floorvalue =floorcombo.getValue();
                    if(floorvalue == null){
                        XD.msg('请选择楼层')
                        return
                    }
                },
                select:function (combo) {
                    var form = combo.up('form');
                    var roomDisplay = form.down('[itemId = roomdisplayId]');
                    roomDisplay.setValue(combo.rawValue);

                    form.down('[itemId = deviceId]').setValue();
                    var deviceStore = form.down('[itemId = deviceId]').getStore();
                    deviceStore.load({
                        url:'/device/getMJJ',
                        params:{
                            area:this.value.trim()
                        }
                    });
                }
            }
        },{
            hidden:true,
            name:'roomdisplay',
            itemId:'roomdisplayId',
        },{
            columnWidth:.5,
            xtype:'numberfield',
            maxLength:2,
            fieldLabel: '库房编码',
            value:'01',
            name:''
        },{
            columnWidth:.5,
            fieldLabel: '架区名称',
            name:'zonedisplay'
        },{
            columnWidth:.5,
            xtype:'numberfield',
            maxLength:2,
            fieldLabel: '架区编码',
            value:'01',
            name:'zone'
        },{
            columnWidth:.5,
            xtype:'numberfield',
            maxLength:2,
            fieldLabel: '列数',
            value:'01',
            name:'countcol'
        },{
            columnWidth:.5,
            xtype:'numberfield',
            maxLength:2,
            fieldLabel: '节数',
            value:'01',
            name:'countsection'
        },{
            columnWidth:.5,
            xtype:'numberfield',
            maxLength:2,
            fieldLabel: '层数',
            value:'01',
            name:'countlayer'
        },{
            columnWidth:.5,
            xtype:'numberfield',
            fieldLabel: '单元格容量',
            value:'100',
            name:'capacity'
        },{
            columnWidth:.5,
            xtype:'numberfield',
            fieldLabel: '固定列',
            maxLength:2,
            name:'fixed'
        },{
            columnWidth: .5,
            fieldLabel: '所属设备',
            name: 'device',
            xtype: 'combo',
            store: 'DeviceStore',
            itemId: 'deviceId',
            displayField: 'name',
            valueField: 'id',
            blankText: '选择设备',
            listeners:{
                expand:function (combo) {
                    var form = combo.up('form');
                    var roomcombo = form.down('[itemId = roomId]');
                    var roomvalue =roomcombo.getValue();
                    if(roomvalue == null){
                        XD.msg('请选择库房')
                        return
                    }
                }
            }
        }]
    }],
    buttons:[{text:'保存',itemId:'save'},{text:'取消',itemId:'cancel'}]
})