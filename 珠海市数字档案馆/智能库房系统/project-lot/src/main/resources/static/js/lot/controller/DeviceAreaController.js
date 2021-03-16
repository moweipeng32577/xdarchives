/**
 * 设备分区管理控制器
 * Created by Rong on 2019-06-13.
 */
Ext.define('Lot.controller.DeviceAreaController',{

    extend: 'Ext.app.Controller',

    models:['DeviceSeletorModel'],

    stores:['DeviceAreaStore','DeviceAreaFloorStore','DeviceSeletorStore'],

    views:['deviceArea.DeviceAreaAddFromView','deviceArea.DeviceAreaGridView','deviceArea.DeviceFloorGridView' ,
            'deviceArea.DeviceFloorAddFromView'],

    init:function(){
        this.control({
            '[itemId=areaPanel] [itemId=openStatus]':{        //打开设备状态
                click:this.updateStatus
            },

            '[itemId=areaPanel] [itemId=closeStatus]':{        //关闭设备状态
                click:this.updateStatus
            },

            'DeviceAreaGridView [itemId=add]':{        //增加分区
                click:function (btn) {
                    var areaPanel = btn.up('[itemId = areaPanel]');
                    var DeviceFloorGridView = areaPanel.down('DeviceFloorGridView');
                    var sels = DeviceFloorGridView.getSelectionModel().getSelection();
                    if(sels.length < 1){
                        XD.msg('提示:请选择楼层！');
                        return;
                    };
                    var floorid = sels[0].get('floorid');
                    var window = Ext.create('Lot.view.deviceArea.DeviceAreaAddFromView',{
                        visualization:btn.up('visualization'),
                        operate:'add'
                    });
                    var DeviceAreaAddFromView =window.down('form');
                    DeviceAreaAddFromView.down('[itemId =floorid]').setValue(floorid);
                    DeviceAreaAddFromView.down('[itemId =floorid]').setReadOnly(true);
                    window.show();
                }
            },

            'DeviceAreaGridView [itemId=delete]':{        //删除分区
                click:function (btn) {
                    var grid = btn.up('DeviceAreaGridView');
                    var select = grid.getSelectionModel();
                    if (select.getSelection().length == 0) {
                        XD.msg('请选择需要删除的数据！！');
                        return;
                    }
                    XD.confirm('确定要删除这 ' + select.getSelection().length + ' 条数据吗?', function () {
                        var tmp = [];
                        for (var i = 0; i < select.getSelection().length; i++) {
                            tmp.push(select.getSelection()[i].get('id'));
                        }
                        var ids = tmp.join(',');
                        Ext.Msg.wait('正在删除数据，请耐心等待……', '正在操作');
                        Ext.Ajax.request({
                            method: 'post',
                            scope: this,
                            url: '/deviceArea/deleteDeviceArea',
                            params: {
                                ids:ids
                            },
                            // timeout: XD.timeout,
                            success: function (response) {
                                var areaPanelStore = grid.getStore();
                                areaPanelStore.reload();
                                Ext.MessageBox.hide();
                            }
                        })
                    })
                }
            },

            'DeviceAreaGridView [itemId=edit]':{        //修改分区
                click:function (btn) {
                    var grid = btn.up('DeviceAreaGridView');
                    var select = grid.getSelectionModel();
                    if (select.getSelection().length == 0) {
                        XD.msg('请选择需要修改的数据！！');
                        return;
                    }
                    else if (select.getSelection().length > 1) {
                        XD.msg('修改操作只能选择一条数据！！');
                        return;
                    }
                    var window = Ext.create('Lot.view.deviceArea.DeviceAreaAddFromView',{
                        visualization:btn.up('visualization'),
                        operate:'modify'
                    });
                    window.title = '修改分区';
                    var record = select.getSelection()[0];
                    window.down('form').loadRecord(record);
                    window.down('form').down('[itemId= areaId]').setValue(record.data.type);//设置下拉框值
                    window.down('form').down('[itemId =floorid]').setValue(record.data.floor.floorid);//设置下拉框值
                    window.show();
                }
            },

            'DeviceAreaAddFromView [itemId = saveBtnID]':{ // 保存
                click:function (btn) {
                    var deviceAreaAddFromView = btn.up('DeviceAreaAddFromView');
                    var form = deviceAreaAddFromView.down('form');
                    var value = form.getValues();
                    if(form.isValid() == false){
                        XD.msg('请输入正确的数据!');
                        return;
                    }
                    form.submit({
                        method: 'POST',
                        url: '/deviceArea/savaDeviceArea',
                        params : { // 此处可以添加额外参数
                            operate:deviceAreaAddFromView.operate
                        },
                        scope: this,
                        success: function (form, action) {
                            var content = action.result;
                             XD.msg(content.msg);
                            //刷新
                            var areaPanel = deviceAreaAddFromView.visualization.down('[itemId=areaPanel]');
                            var DeviceAreaGridView = areaPanel.down('DeviceAreaGridView');
                            var DeviceFloorGridView = areaPanel.down('DeviceFloorGridView');
                            var record = DeviceFloorGridView.getSelectionModel().selected.items[0];
                            var DeviceAreaGridViewStore = DeviceAreaGridView.getStore().load({
                                params:{floorid:record.data.floorid},
                            });

                            deviceAreaAddFromView.close();
                        },
                        failure: function () {
                            XD.msg('操作失败');
                        }
                    });

                }
            },

            'DeviceAreaAddFromView [itemId = BackBtnID]':{ // 返回
                click:function (btn) {
                    var deviceAreaAddFromView = btn.up('DeviceAreaAddFromView');
                    deviceAreaAddFromView.close();
                }
            },

            'DeviceFloorGridView [itemId =floorAddBtn]' :{ //增加楼层
                click:function (btn) {
                    var window = Ext.create('Lot.view.deviceArea.DeviceFloorAddFromView',{
                        visualization:btn.up('visualization'),
                        operate:'add'
                    });
                    window.show();
                }
            },

            'DeviceFloorGridView [itemId =floorModifyBtn]' :{ //修改楼层
                click:function (btn) {
                    var grid = btn.up('DeviceFloorGridView');
                    var select = grid.getSelectionModel();
                    if (select.getSelection().length == 0) {
                        XD.msg('请选择需要修改的数据！！');
                        return;
                    }
                    else if (select.getSelection().length > 1) {
                        XD.msg('修改操作只能选择一条数据！！');
                        return;
                    }
                    var window = Ext.create('Lot.view.deviceArea.DeviceFloorAddFromView',{
                        visualization:btn.up('visualization'),
                        operate:'modify'
                    });
                    window.title = '修改楼层';
                    var record = select.getSelection()[0];
                    window.down('form').loadRecord(record);
                    window.show();
                }
            },

            'DeviceFloorGridView [itemId =floorDelBtn]' :{ //删除楼层
                click:function (btn) {
                    var grid = btn.up('DeviceFloorGridView');
                    var select = grid.getSelectionModel();
                    if (select.getSelection().length == 0) {
                        XD.msg('请选择需要删除的数据！！');
                        return;
                    }
                    XD.confirm('确定要删除这 ' + select.getSelection().length + ' 条数据吗?', function () {
                        var tmp = [];
                        for (var i = 0; i < select.getSelection().length; i++) {
                            tmp.push(select.getSelection()[i].get('floorid'));
                        }
                        var ids = tmp.join(',');
                        Ext.Msg.wait('正在删除数据，请耐心等待……', '正在操作');
                        Ext.Ajax.request({
                            method: 'post',
                            scope: this,
                            url: '/deviceArea/deleteDeviceFloor',
                            params: {
                                ids:ids
                            },
                            // timeout: XD.timeout,
                            success: function (response) {
                                var floorStore = grid.getStore();
                                floorStore.reload();
                                Ext.MessageBox.hide();
                            }
                        })
                    })
                }
            },

            'DeviceFloorAddFromView [itemId = saveBtnID]':{ // 保存楼层
                click:function (btn) {
                    var deviceFloorAddFromView = btn.up('DeviceFloorAddFromView');
                    var form = deviceFloorAddFromView.down('form');
                    var value = form.getValues();
                    if(form.isValid() == false){
                        XD.msg('请输入正确的数据!');
                        return;
                    }
                    form.submit({
                        method: 'POST',
                        url: '/deviceArea/savaDeviceFloor',
                        params : { // 此处可以添加额外参数
                            operate:deviceFloorAddFromView.operate
                        },
                        scope: this,
                        success: function (form, action) {
                            var content = action.result;
                            XD.msg(content.msg);
                            //刷新
                            var floorPanel = deviceFloorAddFromView.visualization.down('[itemId=areaPanel]');
                            var DeviceFloorGridView = floorPanel.down('DeviceFloorGridView');
                            DeviceFloorGridView.getStore().reload();
                            deviceFloorAddFromView.close();
                        },
                        failure: function () {
                            XD.msg('操作失败');
                        }
                    });

                }
            },

            'DeviceFloorAddFromView [itemId = BackBtnID]':{ // 返回楼层
                click:function (btn) {
                    var deviceFloorAddFromView = btn.up('DeviceFloorAddFromView');
                    deviceFloorAddFromView.close();
                }
            },

            '[itemId=areaPanel] button[itemId=seletorSubmit]':{ //提交分区设置
                click:function (btn) {
                    var checkedDeviceId =[];
                    var checkDeviceId =[];
                    var grid = btn.up('[itemId = areaPanel]').down('DeviceAreaGridView');
                    var record =  grid.getSelectionModel().getSelection()[0];
                    var areaid = record.id;
                    var itemselector = btn.up('[itemId =areaPanel]').down('itemselector');
                    //已选
                    var seletedStore = itemselector.toField.getStore();
                    seletedStore.each(function(record) {
                        checkedDeviceId.push(record.get('id')) ;
                    });
                    //可选
                    var seletorStore = itemselector.fromField.getStore();
                    seletorStore.each(function(record) {
                        checkDeviceId.push(record.get('id')) ;
                    });
                    Ext.Msg.wait('正在提交...');
                    Ext.Ajax.request({
                        params: {areaId:areaid,checkedDeviceId: checkedDeviceId,checkDeviceId:checkDeviceId},
                        url: '/deviceArea/seletorSubmit',
                        method: 'post',
                        sync: true,
                        success: function (resp) {
                            var respText = Ext.decode(resp.responseText);
                            XD.msg(respText.msg);
                            Ext.MessageBox.hide();
                        },
                        failure: function () {
                            XD.msg('操作中断');
                        }
                    });
                }
            },

            '[itemId=areaPanel]': { //刷新頁面
                init: function (view) {
                    var DeviceAreaGridView = view.down('DeviceAreaGridView');
                    //重新加载floor
                    var DeviceFloorGridView = view.down('DeviceFloorGridView');
                    var floorRecord = DeviceFloorGridView.getSelectionModel().selected.items[0]
                    var DeviceAreaGridViewStore = DeviceAreaGridView.getStore().reload({
                        params: {floorid: floorRecord.data.floorid},
                        callback: function () {
                            if (DeviceAreaGridViewStore.getCount() > 0) {
                                var record = DeviceAreaGridViewStore.getAt(0);
                                DeviceAreaGridView.getSelectionModel().select(record);

                                var itemselector = view.down('itemselector');
                                // 重新加载数据
                                itemselector.getStore().reload({params: {areaid: record.id}});
                                Ext.Msg.wait('正在读取数据...');
                                setTimeout(function () {
                                    Ext.Ajax.request({
                                        params: {areaid: record.id},
                                        url: '/enabledOrDisableDevice',
                                        method: 'post',
                                        sync: true,
                                        success: function (resp) {
                                            var respText = Ext.decode(resp.responseText);
                                            var data = [];
                                            for (var i = 0; i < respText.length; i++) {
                                                data.push(respText[i].id);
                                            }
                                            itemselector.toField.getStore().load({
                                                params: {areaid: record.id},
                                                callback: function () {
                                                    itemselector.setValue(data);
                                                }
                                            });
                                            Ext.MessageBox.hide();
                                        },
                                        failure: function () {
                                            XD.msg('操作中断');
                                        }
                                    });
                                }, 500)

                            }
                        }
                    });
                }
            }
        });
    },

    updateStatus:function (btn) {
        var demo = btn.up('visualization');
        var pan=demo.down('[itemId=areaPanel]');
        var demo2 = btn.up('grid');
        var st =pan.down('grid').getStore();
        var sels = demo2.getSelectionModel().getSelection();
        if(sels.length != 1){
            Ext.Msg.alert('提示','必须选择一条数据进行修改');
            return;
        };

        console.log(st);
        var status=0;
        if ("电源开"==btn.text)
        {
            status=1;
        }
        Ext.Ajax.request({
            url:'/devicestatus',
            method:'POST',
            params:{
                status:status,
                name:sels[0].get('name')
            },
            success:function(){
                if (1==status){Ext.Msg.alert('提示','成功开启电源');}

                if (0==status){Ext.Msg.alert('提示','成功关闭电源');}

                var total=demo.down('[itemId=totalPanel]');
                var store = total.down('devicelist').getStore();
                store.on('load',function(){
                    demo2.up('[itemId=areaPanel]').down('itemselector').reset();
                })
                store.reload();
            }
        });
    }
});

XD.confirm = function(text, callbackyes, scope, callbackno){
    Ext.MessageBox.confirm('确认信息',text, function (btn) {
        if (btn == 'yes') {
            if(typeof callbackyes=='undefined'){
                return;
            }
            var fn = callbackyes.bind(this);
            fn();
        }
        if (btn == 'no') {
            if(typeof callbackno=='undefined'){
                return;
            }
            var fn = callbackno.bind(this);
            fn();
        }
    }, scope);
}