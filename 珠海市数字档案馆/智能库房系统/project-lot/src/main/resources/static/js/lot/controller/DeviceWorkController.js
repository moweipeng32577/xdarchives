/**
 *   设备作业管理控制器
 *   @author wangmh
 */
Ext.define('Lot.controller.DeviceWorkController',{

    extend: 'Ext.app.Controller',
    stores:['DeviceWorkStore'],
    models:['DeviceWorkModel'],
    views:['deviceWork.DeviceWorkFormView'],
    init:function(){
        this.control({
            'visualization [itemId=workAdd]':{
                click:function () {
                    var win = Ext.create('Lot.view.deviceWork.DeviceWorkFormView',{
                        title:'新增作业计划',
                        width:500,
                        type:'add'
                    });
                    win.show();
                }
            },'visualization [itemId=workUpdate]':{
                click:function (view) {
                    var visualization = view.up('visualization');
                    visualization = visualization.down('[itemId=deviceWorkId]');
                    if (visualization.getSelectionModel().selected.length == 0) {
                        XD.msg("请选择一条数据");
                        return;
                    }
                    if (visualization.getSelectionModel().selected.length >1) {
                        XD.msg("只能选择一条数据");
                        return;
                    }else {
                        var win = Ext.create('Lot.view.deviceWork.DeviceWorkFormView',{
                            title:'修改作业计划',
                            width:500,
                            type:'modify'
                        });

                        win.down('form').loadRecord(visualization.getSelectionModel().selected.items[0]);
                        win.down('form').down('[itemId = daviceTypeId]').setValue(visualization.getSelectionModel().selected.items[0].data.deviceType.typeCode);//设置下拉框值
                        var device =   win.down('form').down('[itemId = deviceId]');
                        var deviceStore = device.getStore();
                        deviceStore.proxy.extraParams.deviceType =visualization.getSelectionModel().selected.items[0].data.deviceType.typeCode;
                        deviceStore.load();
                        device.select(visualization.getSelectionModel().selected.items[0].data.device.id);//设置下拉框值

                        //设备作业修改。只允许修改作业时间
                        var fields = win.down('form').getForm().getFields().items;
                        Ext.each(fields,function (item) {
                            item.setReadOnly(true);//设置查看表单中控件属性为只读
                        });
                        win.down('[itemId = workTimeId]').setReadOnly(false);
                    }
                    win.show();
                }
            },'visualization [itemId=workDel]':{    //删除
                click:function (view) {
                    var grid = view.up('[itemId=deviceWorkId]');
                    var select = grid.getSelectionModel().getSelection();
                    var ids=[];
                    if (select.length==0){
                        XD.msg("请选择数据");
                    }else {
                        for (var i = 0; i < select.length; i++) {
                            ids.push(select[i].get('workId'))
                        }
                    }
                    Ext.Ajax.request({
                        method: 'post',
                        scope: this,
                        params: {ids: ids},
                        url: '/deviceWork/delete',
                        success: function (response) {
                            var funds = Ext.decode(response.responseText);
                            XD.msg(funds.msg);
                            grid.getStore().load();
                        }
                    });
                }
            },'deviceWorkFormView [itemId=save]':{//保存
                click:function (view) {
                    var formPanel = view.up('deviceWorkFormView').down('form');
                    if(!formPanel.isValid()){
                        XD.msg('请输入正确的数据');
                        return;
                    }
                    formPanel.submit({
                        url:'/deviceWork/save',
                        scope: this,
                        params:{
                            "type":view.up('deviceWorkFormView').type
                        },
                        success: function (form, action) {
                            var respText = Ext.decode(action.response.responseText);
                            if (respText.success){
                                XD.msg(respText.msg);
                                view.up('deviceWorkFormView').close();
                                var gridStore = Ext.ComponentQuery.query('[itemId=deviceWorkId]')[0].getStore();
                                gridStore.reload();
                            }
                        },
                        failure: function (form, action) {
                            XD.msg('操作失败');
                        }
                    });
                }
            },'deviceWorkFormView [itemId=cancel]':{//取消
                click:function (view) {
                    view.up('deviceWorkFormView').close();
                }
            }
        });
    }
});