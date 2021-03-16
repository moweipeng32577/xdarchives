/**
 * 设备查询管理控制器
 * Created by Rong on 2019-06-13.
 */
Ext.define('Lot.controller.DeviceInformationController',{
    extend: 'Ext.app.Controller',
    stores:['DeviceInformationStore'],
    models:['DeviceInformationModel'],
    views:['DeviceInformationAddView'],
    init:function(){
        this.control({
            'visualization [itemId=selectBtn]':{//设备信息-查询
                click:function (btn) {
                    var visualization = btn.up('visualization');
                    var informationview = visualization.down('[itemId=deviceInformation]');
                    var content = informationview.down('[itemId=content]').getValue();//查询内容
                    var searchcombo = informationview.down('[itemId=searchcombo]').getValue();//查询字段
                    if(searchcombo==null){
                        XD.msg('请选择需要查询的关键字');
                        return;
                    }
                    informationview.getStore().proxy.url='/deviceInformation/findDevicesBySearch';
                    informationview.getStore().proxy.extraParams.searchcombo = searchcombo;
                    informationview.getStore().proxy.extraParams.content = content;
                    informationview.getStore().load();
                }
            },
            'visualization [itemId=expBtn]':{//设备信息-导出
                click:function (view) {
                    var visualization = view.up('visualization');
                    var informationview = visualization.down('[itemId=deviceInformation]');
                    // 获取到当前表单中的已选择数据
                    var record = informationview.getSelectionModel().selected;
                    if (record.length < 1) {
                        XD.msg('请选择需要导出的数据');
                        return;
                    }
                    var inforids = [];
                    for (var i = 0; i < record.length; i++) {
                        inforids.push(record.items[i].get('inforid'));
                    }
                    var downloadForm = document.createElement('form');
                    document.body.appendChild(downloadForm);
                    var inputTextElement = document.createElement('input');
                    inputTextElement.name ='inforids';
                    inputTextElement.value = inforids;
                    downloadForm.appendChild(inputTextElement);
                    downloadForm.action='/deviceInformation/expDeviceInformation';
                    downloadForm.method = "post";
                    downloadForm.submit();
                }
            },
            'visualization [itemId=modifyBtn]':{//设备信息-修改
                click:function (view) {
                    var visualization = view.up('visualization');
                    var informationview = visualization.down('[itemId=deviceInformation]');
                    if (informationview.getSelectionModel().selected.length == 0) {
                        XD.msg("请选择一条数据");
                        return;
                    }
                    if (informationview.getSelectionModel().selected.length >1) {
                        XD.msg("只能选择一条数据");
                        return;
                    }else {
                        var win = Ext.create('Lot.view.DeviceInformationAddView',{
                            title:'修改设备信息',
                            width:600,
                        });
                        win.down('form').loadRecord(informationview.getSelectionModel().selected.items[0]);
                    }
                    win.show();
                }
            },
            'visualization [itemId=addBtn]':{//设备信息-录入
                click:function (btn) {
                    var win = Ext.create('Lot.view.DeviceInformationAddView',{
                        title:'新增设备信息',
                        width:600,
                    });
                    win.show();
                }
            },
            'visualization [itemId=delBtn]':{//设备信息-删除
                click:function (view) {
                    var visualization = view.up('visualization');
                    var informationview = visualization.down('[itemId=deviceInformation]')
                    // 获取到当前表单中的已选择数据
                    var record = informationview.getSelectionModel().selected;
                    if (record.length < 1) {
                        XD.msg('请选择需要删除的数据！');
                        return;
                    }
                    var inforids = [];
                    for (var i = 0; i < record.length; i++) {
                        inforids.push(record.items[i].get('inforid'));
                    }
                    Ext.MessageBox.confirm('确认信息','确定要删除这' + record.length + '条数据吗?', function (btn) {
                        if (btn == 'yes') {
                            Ext.Ajax.request({
                                method: 'post',
                                scope: this,
                                params: {inforIds: inforids},
                                url: '/deviceInformation/delInformation',
                                success: function (response) {
                                    var funds = Ext.decode(response.responseText);
                                    XD.msg(funds.msg);
                                    informationview.getStore().load();
                                }
                            });
                        }
                    });
                }
            },
            'deviceInformationAddView [itemId=save]' :{
                click:function (view) {
                    var formPanel = view.up('deviceInformationAddView').down('form');
                    if(!formPanel.isValid()){
                        XD.msg('请输入正确的信息');
                        return;
                    }
                    formPanel.submit({
                        url:'/deviceInformation/saveInformation',
                        scope: this,
                        success: function (form, action) {
                            var respText = Ext.decode(action.response.responseText);
                            if (respText.success){
                                XD.msg(respText.msg);
                                view.up('deviceInformationAddView').close();
                                var gridStore = Ext.ComponentQuery.query('[itemId=deviceInformation]')[0].getStore();
                                gridStore.reload();
                            }
                        },
                        failure: function (form, action) {
                            XD.msg('操作失败');
                        }
                    });
                }
            },'deviceInformationAddView [itemId=cancel]':{//取消
                click:function (view) {
                    view.up('deviceInformationAddView').close();
                }
            }
        })
    }
});

XD.msg = function(text,closetime){
    var autoCloseTime=2000;
    if(closetime){
        autoCloseTime=closetime;
    }
    Ext.toast({
        autoCloseDelay: autoCloseTime,
        minWidth: 400,
        maxWidth: 600,
        title:'提示信息',
        iconCls:'x-fa fa-exclamation-circle',
        html: "<font>" + text + "</font>"
    });
};