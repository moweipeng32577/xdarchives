/**
 * 故障信息管理控制器
 * Created by Rong on 2019-06-13.
 */
Ext.define('Lot.controller.DeviceDiagnoseController',{
    extend: 'Ext.app.Controller',
    stores:['DeviceDiagnoseStore'],
    models:['DeviceDiagnoseModel'],
    views:['DeviceDiagnoseAddView'],
    init:function(){
        this.control({
            'visualization [itemId=diagnoseAddBtn]':{//设备信息-录入
                click:function (btn) {
                    var win = Ext.create('Lot.view.DeviceDiagnoseAddView',{
                        title:'新增故障信息',
                        width:600,
                    });
                    win.show();
                }
            },
            'visualization [itemId=diagnoseModifyBtn]':{//设备信息-修改
                click:function (view) {
                    var visualization = view.up('visualization');
                    var devicediagnose = visualization.down('[itemId=devicediagnose]');
                    if (devicediagnose.getSelectionModel().selected.length == 0) {
                        XD.msg("请选择一条数据");
                        return;
                    }
                    if (devicediagnose.getSelectionModel().selected.length >1) {
                        XD.msg("只能选择一条数据");
                        return;
                    }else {
                        var win = Ext.create('Lot.view.DeviceDiagnoseAddView',{
                            title:'修改故障信息',
                            width:600,
                        });
                        win.down('form').loadRecord(devicediagnose.getSelectionModel().selected.items[0]);
                    }
                    win.show();
                }
            },
            'visualization [itemId=diagnoseDelBtn]':{//设备信息-删除
                click:function (view) {
                    var visualization = view.up('visualization');
                    var devicediagnose = visualization.down('[itemId=devicediagnose]');
                    // 获取到当前表单中的已选择数据
                    var record = devicediagnose.getSelectionModel().selected;
                    if (record.length < 1) {
                        XD.msg('请选择需要删除的数据！');
                        return;
                    }
                    var ids = [];
                    for (var i = 0; i < record.length; i++) {
                        ids.push(record.items[i].get('id'));
                    }
                    Ext.MessageBox.confirm('确认信息','确定要删除这' + record.length + '条数据吗?', function (btn) {
                        if (btn == 'yes') {
                            Ext.Ajax.request({
                                method: 'post',
                                scope: this,
                                params: {ids: ids},
                                url: '/deviceDiagnose/delDeviceDiagnose',
                                success: function (response) {
                                    var funds = Ext.decode(response.responseText);
                                    XD.msg(funds.msg);
                                    devicediagnose.getStore().load();
                                }
                            });
                        }
                    });
                }
            },'deviceDiagnoseAddView [itemId=save]' :{
                click:function (view) {
                    var formPanel = view.up('deviceDiagnoseAddView').down('form');
                    if (!formPanel.isValid()) {
                        XD.msg('请输入正确的信息!');
                        return;
                    }
                    formPanel.submit({
                        url:'/deviceDiagnose/saveDeviceDiagnose',
                        scope: this,
                        success: function (form, action) {
                            var respText = Ext.decode(action.response.responseText);
                            if (respText.success){
                                XD.msg(respText.msg);
                                view.up('deviceDiagnoseAddView').close();
                                var gridStore = Ext.ComponentQuery.query('[itemId=devicediagnose]')[0].getStore();
                                gridStore.reload();
                            }
                        },
                        failure: function (form, action) {
                            XD.msg('操作失败');
                        }
                    });
                }
            },'deviceDiagnoseAddView [itemId=cancel]':{//取消
                click:function (view) {
                    view.up('deviceDiagnoseAddView').close();
                }
            }
        })
    }
});