/**
 * Created by Administrator on 2020/4/17.
 */


Ext.define('CarManage.controller.CarManageController', {
    extend: 'Ext.app.Controller',

    views: ['CarManageGridView','CarManageAddView','CarDefendGridView','CarManageView','CarManageEditView'],//加载view
    stores: ['CarManageGridStore','CarDefendGridStore'],//加载store
    models: ['CarManageGridModel','CarDefendGridModel'],//加载model
    init: function () {
        this.control({
            'carManageView': {
                afterrender: function (view) {
                    var carManageGridView = view.down('carManageGridView');
                    carManageGridView.initGrid();
                }
            },
            'carManageGridView button[itemId=addcar]':{  //新增车辆
                click:function (view) {
                    var carManageGridView = view.findParentByType('carManageGridView');
                   var carManageAddView = Ext.create('CarManage.view.CarManageAddView');
                   carManageAddView.carManageGridView = carManageGridView;
                   carManageAddView.show();
                }
            },
            'carManageAddView button[itemId=carSubmit]':{  //新增提交
                click:function (view) {
                    var carManageAddView = view.findParentByType('carManageAddView');
                    var form = carManageAddView.down('form');
                    if(!form.isValid()){
                        XD.msg('存在必填项未填写');
                        return;
                    }
                    form.submit({
                        url:'/carManage/carManageSubmit',
                        method:'POST',
                        scope: this,
                        success:function (form,action) {
                            XD.msg('新增成功');
                            carManageAddView.carManageGridView.getStore().reload();
                            carManageAddView.close();
                        },
                        failure:function () {
                            XD.msg('该车辆已存在');
                        }
                    });
                }
            },
            'carManageAddView button[itemId=carClose]':{  //关闭
                click:function (view) {
                    view.findParentByType('carManageAddView').close();
                }
            },
            'carManageGridView button[itemId=editcar]':{  //修改车辆
                click:function (view) {
                    var carManageGridView = view.findParentByType('carManageGridView');
                    var select = carManageGridView.getSelectionModel().getSelection();
                    if(select.length!=1){
                        XD.msg('只能选择一条数据');
                        return;
                    }
                    if(select[0].get('state').indexOf('使用中')!=-1){
                        XD.msg('车辆 '+select[0].get('carnumber')+' 正使用中，不可修改');
                        return;
                    }
                    var carManageEditView = Ext.create('CarManage.view.CarManageEditView');
                    var form = carManageEditView.down('form');
                    form.load({
                        url:'/carManage/getCarManageByid',
                        method:'POST',
                        params:{
                            id:select[0].get('id')
                        },
                        success:function (form,action) {
                            var respText = Ext.decode(action.response.responseText);
                            if(!respText.success){
                                XD.msg('获取表单信息失败');
                            }
                            carManageEditView.carManageGridView = carManageGridView;
                            carManageEditView.show();
                        },
                        failure:function () {
                            XD.msg('操作失败');
                        }
                    });
                }
            },

            'carManageEditView button[itemId=carSubmit]':{  //修改提交
                click:function (view) {
                    var carManageEditView = view.findParentByType('carManageEditView');
                    var form = carManageEditView.down('form');
                    if(!form.isValid()){
                        XD.msg('存在必填项未填写');
                        return;
                    }
                    form.submit({
                        url:'/carManage/carManageSubmit',
                        method:'POST',
                        scope: this,
                        success:function (form,action) {
                            XD.msg('修改成功');
                            carManageEditView.carManageGridView.getStore().reload();
                            carManageEditView.close();
                        },
                        failure:function () {
                            XD.msg('该车辆已存在');
                        }
                    });
                }
            },
            'carManageEditView button[itemId=carClose]':{  //关闭
                click:function (view) {
                    view.findParentByType('carManageEditView').close();
                }
            },
            'carManageGridView button[itemId=lookcar]':{  //查看车辆
                click:function (view) {
                    var carManageGridView = view.findParentByType('carManageGridView');
                    var select = carManageGridView.getSelectionModel().getSelection();
                    if(select.length!=1){
                        XD.msg('只能选择一条数据');
                        return;
                    }
                    var carManageAddView = Ext.create('CarManage.view.CarManageAddView');
                    var form = carManageAddView.down('form');
                    form.load({
                        url:'/carManage/getCarManageByid',
                        method:'POST',
                        params:{
                            id:select[0].get('id')
                        },
                        success:function (form,action) {
                            var respText = Ext.decode(action.response.responseText);
                            if(!respText.success){
                                XD.msg('获取表单信息失败');
                            }
                            carManageAddView.setTitle('查看车辆');
                            carManageAddView.down('[itemId=carSubmit]').hide();
                            carManageAddView.show();
                        },
                        failure:function () {
                            XD.msg('操作失败');
                        }
                    });
                }
            },

            'carManageGridView button[itemId=deletecar]':{  //删除车辆
                click:function (view) {
                    var carManageGridView = view.findParentByType('carManageGridView');
                    var select = carManageGridView.getSelectionModel().getSelection();
                    if(select.length<1){
                        XD.msg('请至少选择一条数据');
                        return;
                    }
                    var ids = [];
                    for(var i=0;i<select.length;i++){
                        ids.push(select[i].get('id'));
                    }
                    XD.confirm('是否删除这'+select.length+'条数据',function () {
                        Ext.Ajax.request({
                            url:'/carManage/deleteCarManageByid',
                            method:'POST',
                            params:{
                                ids:ids
                            },
                            success:function (rep) {
                                var respText = Ext.decode(rep.responseText);
                                if(!respText.success){
                                    XD.msg('删除失败');
                                }else{
                                    XD.msg('删除成功');
                                    carManageGridView.getStore().reload();
                                }
                            },
                            failure:function () {
                                XD.msg('操作失败');
                            }
                        });
                    });
                }
            },

            'carManageGridView button[itemId=defendcar]': {  //维护记录
                click: function (view) {
                    var carManageGridView = view.findParentByType('carManageGridView');
                    var carManageView = carManageGridView.findParentByType('carManageView');
                    var carDefendGridView = carManageView.down('carDefendGridView');
                    var select = carManageGridView.getSelectionModel().getSelection();
                    if(select.length !=1 ){
                        XD.msg('只能选择一条记录');
                        return;
                    }
                    carDefendGridView.initGrid({carid:select[0].get('id')});
                    carDefendGridView.carid = select[0].get('id');
                    carManageView.setActiveItem(carDefendGridView);
                }
            },
            
            'carDefendGridView button[itemId=addDefend]':{ // 新增维护记录
                click:function (view) {
                    var carDefendGridView = view.findParentByType('carDefendGridView');
                    var carDefendAddView = Ext.create('CarManage.view.CarDefendAddView');
                    carDefendAddView.carDefendGridView = carDefendGridView;
                    var form = carDefendAddView.down('form');
                    form.load({
                        url:'/carManage/loadCarDefend',
                        method:'POST',
                        success:function (form,action) {
                        },
                        failure:function () {
                            XD.msg('操作失败');
                        }
                    });
                    carDefendAddView.carid = carDefendGridView.carid;
                    carDefendAddView.show();
                }
            },

            'carDefendAddView button[itemId=defendSubmit]':{ // 新增 提交
                click:function (view) {
                    var carDefendAddView = view.findParentByType('carDefendAddView');
                    var form = carDefendAddView.down('form');
                    if(!form.isValid()){
                        XD.msg('存在必填项未填写');
                        return;
                    }
                    form.submit({
                        url:'/carManage/carDefendSubmit',
                        method:'POST',
                        params:{
                            carid:carDefendAddView.carid
                        },
                        scope: this,
                        success:function (form,action) {
                            if(carDefendAddView.title=='修改维护记录'){
                                XD.msg('修改成功');
                            }else{
                                XD.msg('新增成功');
                            }
                            carDefendAddView.carDefendGridView.getStore().reload();
                            carDefendAddView.close();
                        },
                        failure:function () {
                            XD.msg('操作失败');
                        }
                    });
                }
            },

            'carDefendAddView button[itemId=defendClose]':{  // 新增 关闭
                click:function (view) {
                    view.findParentByType('carDefendAddView').close();
                }
            },


            'carDefendGridView button[itemId=editDefend]':{  //修改维护记录
                click:function (view) {
                    var carDefendGridView = view.findParentByType('carDefendGridView');
                    var select = carDefendGridView.getSelectionModel().getSelection();
                    if(select.length!=1){
                        XD.msg('只能选择一条数据');
                        return;
                    }
                    var carDefendAddView = Ext.create('CarManage.view.CarDefendAddView');
                    var form = carDefendAddView.down('form');
                    form.load({
                        url:'/carManage/getCarDefendByid',
                        method:'POST',
                        params:{
                            id:select[0].get('id')
                        },
                        success:function (form,action) {
                            var respText = Ext.decode(action.response.responseText);
                            if(!respText.success){
                                XD.msg('获取表单信息失败');
                            }
                            carDefendAddView.setTitle('修改维护记录');
                            carDefendAddView.carid = carDefendGridView.carid;
                            carDefendAddView.carDefendGridView = carDefendGridView;
                            carDefendAddView.show();
                        },
                        failure:function () {
                            XD.msg('操作失败');
                        }
                    });
                }
            },

            'carDefendGridView button[itemId=lookDefend]':{  //查看维护记录
                click:function (view) {
                    var carDefendGridView = view.findParentByType('carDefendGridView');
                    var select = carDefendGridView.getSelectionModel().getSelection();
                    if(select.length!=1){
                        XD.msg('只能选择一条数据');
                        return;
                    }
                    var carDefendAddView = Ext.create('CarManage.view.CarDefendAddView');
                    var form = carDefendAddView.down('form');
                    form.load({
                        url:'/carManage/getCarDefendByid',
                        method:'POST',
                        params:{
                            id:select[0].get('id')
                        },
                        success:function (form,action) {
                            var respText = Ext.decode(action.response.responseText);
                            if(!respText.success){
                                XD.msg('获取表单信息失败');
                            }
                            carDefendAddView.setTitle('查看维护记录');
                            carDefendAddView.down('[itemId=defendSubmit]').hide();
                            carDefendAddView.show();
                        },
                        failure:function () {
                            XD.msg('操作失败');
                        }
                    });
                }
            },

            'carDefendGridView button[itemId=deleteDefend]':{  //删除维护记录
                click:function (view) {
                    var carDefendGridView = view.findParentByType('carDefendGridView');
                    var select = carDefendGridView.getSelectionModel().getSelection();
                    if(select.length<1){
                        XD.msg('请至少选择一条数据');
                        return;
                    }
                    var ids = [];
                    for(var i=0;i<select.length;i++){
                        ids.push(select[i].get('id'));
                    }
                    XD.confirm('是否删除这'+select.length+'条数据',function () {
                        Ext.Ajax.request({
                            url:'/carManage/deleteCarDefendByid',
                            method:'POST',
                            params:{
                                ids:ids
                            },
                            success:function (rep) {
                                var respText = Ext.decode(rep.responseText);
                                if(!respText.success){
                                    XD.msg('删除失败');
                                }else{
                                    XD.msg('删除成功');
                                    carDefendGridView.getStore().reload();
                                }
                            },
                            failure:function () {
                                XD.msg('操作失败');
                            }
                        });
                    });
                }
            },

            'carDefendGridView button[itemId=back]':{  //返回
                click:function (view) {
                    var carDefendGridView = view.findParentByType('carDefendGridView');
                    var carManageView = carDefendGridView.findParentByType('carManageView');
                    var carManageGridView = carManageView.down('carManageGridView');
                    carManageView.setActiveItem(carManageGridView);
                }
            }
        });
    }
});
