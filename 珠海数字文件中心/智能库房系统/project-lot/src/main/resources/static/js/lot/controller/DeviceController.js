/**
 * 设备管理控制器
 * 主要功能包括设备的增加、修改和删除
 * Created by Rong on 2019-01-17.
 */
Ext.define('Lot.controller.DeviceController',{

    extend: 'Ext.app.Controller',

    views:['device.DeviceFormView','device.DeviceGridView','device.DeviceTypeView','device.DeviceTypeFormView'],

    stores:['DeviceAreaStore','DeviceTypeStore'],

    init:function(){
        this.control({
            'DeviceTypeView [itemId=deviceTypeAddBtn]':{        //添加设备类型
                click:this.addTypeHandler
            },
            'DeviceTypeView [itemId=deviceTypemodifyBtn]':{        //修改设备类型
                click:this.updateTypeHandler
            },
            'DeviceTypeView [itemId=deviceTypedelBtn]':{        //删除设备类型
                click:this.delTypeHandler
            },
            'DeviceTypeFormView [itemId=saveBtn]':{        //添加修改设备类型提交按钮
                click:this.saveTypeHandler
            },
            'DeviceTypeFormView [itemId=closeBtn]':{        //设备类型表单关闭按钮
                click:this.closeHandler
            },
            'DeviceGridView [itemId=deviceAddBtn]':{        //添加设备
                click:this.addHandler
            },
            'DeviceGridView [itemId=devicemodifyBtn]':{     //修改设备
                click:this.modifyHandler
            },
            'DeviceGridView [itemId=devicedelBtn]':{        //删除设备
                click:this.delHandler
            },
            'DeviceGridView [itemId=DisableDevice]':{        //禁用设备
                click:this.DisableDevice
            },
            'DeviceGridView [itemId=EnabledDevice]':{        //启用设备
                click:this.EnabledDevice
            },
            'DeviceFormView [itemId=saveBtn]':{         //表单保存
                click:this.saveHandler
            },
            'DeviceFormView [itemId=closeBtn]':{        //表单关闭
                click:this.closeHandler
            },
            'DeviceFormView [itemId=jionbtn]': {        //接入设备
                click: function (view, record, item, index, e) {
                    var deviceid = view.getStore().getAt(item).id;
                    var result = this.isHasDevicePremissions(deviceid);
                    if(result){
                        Ext.Ajax.request({
                            url:'/enabledDevice',
                            method:'post',
                            params:{
                                deviceid:deviceid,
                                state:'0',
                                type:'hasPremissions'
                            },
                            success:function(resopnse){
                                Ext.Msg.alert('提示','设备接入成功');
                                //设备删除后刷新列表
                                view.getStore().reload();
                            }
                        });
                    }else{
                        Ext.Msg.show({
                            title:'提示',
                            message: '设备不在库房接入白名单中，请联系库房管理员，将该设备加入白名单中再接入!',
                            buttons: Ext.Msg.OKCANCEL,
                            buttonText: {ok:'继续接入',cancel:'取消接入'},
                            fn:function (btn) {
                                if(btn == 'ok'){
                                    Ext.Ajax.request({
                                        url:'/enabledDevice',
                                        method:'post',
                                        params:{
                                            deviceid:deviceid,
                                            state:'0',
                                            type:'noPremissions'
                                        },
                                        success:function(resopnse){
                                            Ext.Msg.alert('提示','设备接入成功');
                                            //设备删除后刷新列表
                                            view.getStore().reload();
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            }
        });
    },

    /**
     * 获取设备列表组件
     * @param btn
     * @returns {*}
     */
    findDevicePanel:function(btn){
        return btn.up('[itemId=devicePanel]');
    },

    /**
     * 获取设备表单组件
     * @param btn
     * @returns {*}
     */
    findDeviceForm:function(btn){
        return btn.up('form');
    },

    /**
     * 创建设备表单并返回
     * 添加和修改设备共用
     * @returns {Ext.window.Window}
     */
    createDeviceWindow:function(devicetypeid){
        var win = Ext.create('Ext.window.Window',{
            width:500,
            height:490,
            title:'设备管理',
            layout:'fit',
            items:[{xtype:'DeviceFormView'}],
            modal:true
        });
        var deviceForm = win.down('DeviceFormView');
        deviceForm.down('[itemId = typeId]').setValue(devicetypeid);
        deviceForm.down('[itemId = typeId]').setReadOnly(true);
        return win;
    },

    /**
     * 创建设备类型表单并返回
     * 添加和修改设备类型共用
     * @returns {Ext.window.Window}
     */
    createDeviceTypeWindow:function(){
        var win = Ext.create('Ext.window.Window',{
            width:500,
            height:240,
            title:'设备管理',
            layout:'fit',
            items:[{xtype:'DeviceTypeFormView'}],
            modal:true
        });
        return win;
    },

    /**
     * 添加设备类型
     * @param btn
     */
    addTypeHandler:function(btn){
        var devicePanel = this.findDevicePanel(btn);
        var deviceTypeGrid = devicePanel.down('DeviceTypeView');
        var win = this.createDeviceTypeWindow();
        //将当前列表传递到表单，用于表单保存后刷新列表
        win.parent = deviceTypeGrid;
        win.show();
    },

    /**
     * 修改设备类型
     * @param btn
     */
    updateTypeHandler:function(btn){
        var devicePanel = this.findDevicePanel(btn);
        var deviceTypeGrid = devicePanel.down('DeviceTypeView');
        var sels = deviceTypeGrid.getSelectionModel().getSelection();
        if(sels.length < 1){
            XD.msg('提示:请选择设备类型！');
            return;
        };
        var win = this.createDeviceTypeWindow();
        win.down('form').loadRecord(sels[0]);
        win.parent = deviceTypeGrid;
        win.show();
    },

    /**
     * 设备删除，支持单条与批量
     * @param btn
     */
    delTypeHandler:function(btn){
        var devicePanel = this.findDevicePanel(btn);
        var deviceGrid = devicePanel.down('DeviceTypeView');
        var sels = deviceGrid.getSelectionModel().getSelection();
        if(sels.length < 1){
            XD.msg('提示:至少选择一条数据进行删除');
            return;
        };
        var ids = [];
        for(var i=0;i<sels.length;i++){
            ids.push(sels[i].get('id'))
        }
        XD.confirm('确定要删除这 ' + ids.length + ' 条数据吗?', function () {
            Ext.Msg.wait('正在删除数据，请耐心等待……', '正在操作');
            Ext.Ajax.request({
                url: '/deviceType/deviceType/' + ids.join(","),
                method: 'DELETE',
                success: function (resopnse) {
                    var respText = Ext.decode(resopnse.responseText);
                    XD.msg(respText.msg);
                    //设备删除后刷新列表
                    deviceGrid.getStore().reload();
                    Ext.Msg.hide();
                },
                failure: function () {
                    Ext.Msg.hide();
                    XD.msg('删除失败！');
                }
            })
        })
    },

    /**
     * 添加设备
     * @param btn
     */
    addHandler:function(btn){
        var devicePanel = this.findDevicePanel(btn);
        var deviceTypeGrid = devicePanel.down('DeviceTypeView');
        var deviceGrid = devicePanel.down('DeviceGridView');
        var sels = deviceTypeGrid.getSelectionModel().getSelection();
        if(sels.length < 1){
            XD.msg('提示:请选择设备类型！');
            return;
        };
        var devicetypeid = sels[0].get('id');

        var win = this.createDeviceWindow(devicetypeid);
        //将当前列表传递到表单，用于表单保存后刷新列表
        win.parent = deviceGrid;
        win.show();
    },

    /**
     * 修改设备
     * @param btn
     */
    modifyHandler:function(btn){
        var devicePanel = this.findDevicePanel(btn);
        var deviceGrid = devicePanel.down('DeviceGridView');
        var sels = deviceGrid.getSelectionModel().getSelection();
        if(sels.length != 1){
            XD.msg('提示:请选择一条数据进行修改');
            return;
        };

        var win = this.createDeviceWindow();
        //将当前列表传递到表单，用于表单保存后刷新列表
        win.parent = deviceGrid;
        win.show();
        //设备修改，将当前列表选中的数据加载到表单上
        win.down('form').loadRecord(sels[0]);
        win.down('form').down('[itemId=typeId]').setValue(sels[0].data.type==null?'':sels[0].data.type.id);//设置下拉框值
        win.down('form').down('[itemId=areaId]').setValue(sels[0].data.area==null?'':sels[0].data.area.id);//设置下拉框值
    },

    /**
     * 设备删除，支持单条与批量
     * @param btn
     */
    delHandler:function(btn){
        var devicePanel = this.findDevicePanel(btn);
        var deviceGrid = devicePanel.down('DeviceGridView');
        var sels = deviceGrid.getSelectionModel().getSelection();
        if(sels.length < 1){
            XD.msg('提示:至少选择一条数据进行删除');
            return;
        };
        var ids = [];
        for(var i=0;i<sels.length;i++){
            ids.push(sels[i].get('id'))
        }
        XD.confirm('确定要删除这 ' + ids.length + ' 条数据吗?', function () {
            Ext.Msg.wait('正在删除数据，请耐心等待……', '正在操作');
            Ext.Ajax.request({
                url: '/device/' + ids.join(","),
                method: 'DELETE',
                success: function (resopnse) {
                    var respText = Ext.decode(resopnse.responseText);
                    XD.msg(respText.msg);
                    //设备删除后刷新列表
                    deviceGrid.getStore().reload();
                    Ext.MessageBox.hide();
                },
                failure: function () {
                    XD.msg('删除失败！');
                }
            })
        })
    },

    /**
     * 设备表单保存
     * @param btn
     */
    saveHandler:function(btn){
        var devicefrom = btn.up('DeviceFormView');
        var values = devicefrom.getValues();
        devicefrom.submit({
            url:'/saveDevice',
            method:'POST',
            scope:this,
            success:function(form,action){
                //保存成功后刷新列表，并关闭表单窗口
                devicefrom.up('window').parent.getStore().reload();
                devicefrom.up('window').close();
            },
            failure: function () {
                XD.msg('添加失败！');
            }
        });
    },

    /**
     * 设备类型表单保存
     * @param btn
     */
    saveTypeHandler:function(btn){
        var devicefrom = btn.up('DeviceTypeFormView');
        devicefrom.submit({
            url:'/deviceType/saveDeviceType',
            method:'POST',
            scope:this,
            success:function(form,action){
                //保存成功后刷新列表，并关闭表单窗口
                devicefrom.up('window').parent.getStore().reload();
                devicefrom.up('window').close();
            },
            failure: function () {
                XD.msg('添加失败！');
            }
        });
    },

    /**
     * 表单关闭
     * @param btn
     */
    closeHandler:function(btn){
        btn.up('window').close();
    },

    EnabledDevice:function(btn){
        this.EnabledOrDisableDevice(btn,"0");
    },

    DisableDevice:function(btn){
        this.EnabledOrDisableDevice(btn,"1");
    },
    /**
     *  启用或禁用设备
     * @param btn
     */
    EnabledOrDisableDevice:function (btn,type) {
        var grid = this.findDevicePanel(btn);
        var sels = grid.getSelection();
        var ids = [];
        if(sels.length != 1){
            Ext.Msg.alert('提示','必须选择一条数据进行修改');
            return;
        };
        for(var i=0;i<sels.length;i++){
            ids.push(sels[i].get('id'));
        }
        Ext.Msg.show({
            title:'提示',
            message: '确认对这'+sels.length+'条数据进行修改吗？',
            buttons: Ext.Msg.OKCANCEL,
            buttonText: {ok:'确认',cancel:'取消'},
            fn:function (btn) {
                if(btn == 'ok'){
                    Ext.Ajax.request({
                        url:'/enabledOrDisableDevice',
                        method:'post',
                        params:{ids:ids.join(","),type:type},
                        success:function(resopnse){
                            Ext.Msg.alert('提示','执行成功');
                            //设备删除后刷新列表
                            grid.getStore().reload();
                        }
                    });
                }
            }
        });
    },

    //判断是否拥有设备接入权限
    isHasDevicePremissions:function (deviceid) {
        var result;
        Ext.Ajax.request({
            url:'/user/isHasDevicePremissions',
            method:'post',
            async:false,
            params:{
                deviceid:deviceid
            },
            success:function(resopnse){
                result = Ext.decode(resopnse.responseText);
            }
        });
        return result;
    }
});