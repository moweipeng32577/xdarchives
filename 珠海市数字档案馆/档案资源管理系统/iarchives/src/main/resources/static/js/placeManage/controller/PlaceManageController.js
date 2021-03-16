/**
 * Created by Administrator on 2020/4/20.
 */


Ext.define('PlaceManage.controller.PlaceManageController', {
    extend: 'Ext.app.Controller',

    views: ['PlaceManageGridView','PlaceManageAddView','PlaceDefendAddView','PlaceDefendGridView',
    'PlaceManageView','PlaceManageEditView'],//加载view
    stores: ['PlaceManageGridStore','PlaceDefendGridStore'],//加载store
    models: ['PlaceManageGridModel','PlaceDefendGridModel'],//加载model
    init: function () {
        this.control({
            'placeManageView': {
                afterrender: function (view) {
                    var placeManageGridView = view.down('placeManageGridView');
                    placeManageGridView.initGrid();
                }
            },
            'placeManageGridView button[itemId=addplace]':{  //新增场地
                click:function (view) {
                    var placeManageGridView = view.findParentByType('placeManageGridView');
                    var placeManageAddView = Ext.create('PlaceManage.view.PlaceManageAddView');
                    placeManageAddView.placeManageGridView = placeManageGridView;
                    placeManageAddView.show();
                }
            },
            'placeManageAddView button[itemId=placeSubmit]':{  //新增提交
                click:function (view) {
                    var placeManageAddView = view.findParentByType('placeManageAddView');
                    var form = placeManageAddView.down('form');
                    if(!form.isValid()){
                        XD.msg('存在必填项未填写');
                        return;
                    }
                    form.submit({
                        url:'/placeManage/placeManageSubmit',
                        method:'POST',
                        scope: this,
                        success:function (form,action) {
                            XD.msg('新增成功');
                            placeManageAddView.placeManageGridView.getStore().reload();
                            placeManageAddView.close();
                        },
                        failure:function () {
                            XD.msg('操作失败');
                        }
                    });
                }
            },
            'placeManageAddView button[itemId=placeClose]':{  //关闭
                click:function (view) {
                    view.findParentByType('placeManageAddView').close();
                }
            },
            'placeManageGridView button[itemId=editplace]':{  //修改场地
                click:function (view) {
                    var placeManageGridView = view.findParentByType('placeManageGridView');
                    var select = placeManageGridView.getSelectionModel().getSelection();
                    if(select.length!=1){
                        XD.msg('只能选择一条数据');
                        return;
                    }
                    if(select[0].get('state').indexOf('使用中')!=-1){
                        XD.msg('场地 '+select[0].get('floor')+' '+select[0].get('placedesc')+ ' 正使用中，不可修改。');
                        return;
                    }
                    var placeManageEditView = Ext.create('PlaceManage.view.PlaceManageEditView');
                    var form = placeManageEditView.down('form');
                    form.load({
                        url:'/placeManage/getPlaceManageByid',
                        method:'POST',
                        params:{
                            id:select[0].get('id')
                        },
                        success:function (form,action) {
                            var respText = Ext.decode(action.response.responseText);
                            if(!respText.success){
                                XD.msg('获取表单信息失败');
                            }
                            placeManageEditView.placeManageGridView = placeManageGridView;
                            placeManageEditView.show();
                        },
                        failure:function () {
                            XD.msg('操作失败');
                        }
                    });
                }
            },
            'placeManageEditView button[itemId=placeSubmit]':{  //修改提交
                click:function (view) {
                    var placeManageEditView = view.findParentByType('placeManageEditView');
                    var form = placeManageEditView.down('form');
                    if(!form.isValid()){
                        XD.msg('存在必填项未填写');
                        return;
                    }
                    form.submit({
                        url:'/placeManage/placeManageSubmit',
                        method:'POST',
                        scope: this,
                        success:function (form,action) {
                            XD.msg('修改成功');
                            placeManageEditView.placeManageGridView.getStore().reload();
                            placeManageEditView.close();
                        },
                        failure:function () {
                            XD.msg('操作失败');
                        }
                    });
                }
            },
            'placeManageEditView button[itemId=placeClose]':{  //关闭
                click:function (view) {
                    view.findParentByType('placeManageEditView').close();
                }
            },
            'placeManageGridView button[itemId=lookplace]':{  //查看场地
                click:function (view) {
                    var placeManageGridView = view.findParentByType('placeManageGridView');
                    var select = placeManageGridView.getSelectionModel().getSelection();
                    if(select.length!=1){
                        XD.msg('只能选择一条数据');
                        return;
                    }
                    var placeManageAddView = Ext.create('PlaceManage.view.PlaceManageAddView');
                    var form = placeManageAddView.down('form');
                    form.load({
                        url:'/placeManage/getPlaceManageByid',
                        method:'POST',
                        params:{
                            id:select[0].get('id')
                        },
                        success:function (form,action) {
                            var respText = Ext.decode(action.response.responseText);
                            if(!respText.success){
                                XD.msg('获取表单信息失败');
                            }
                            placeManageAddView.setTitle('查看场地');
                            placeManageAddView.down('[itemId=placeSubmit]').hide();
                            placeManageAddView.show();
                        },
                        failure:function () {
                            XD.msg('操作失败');
                        }
                    });
                }
            },

            'placeManageGridView button[itemId=deleteplace]':{  //删除场地
                click:function (view) {
                    var placeManageGridView = view.findParentByType('placeManageGridView');
                    var select = placeManageGridView.getSelectionModel().getSelection();
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
                            url:'/placeManage/deletePlaceManageByid',
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
                                    placeManageGridView.getStore().reload();
                                }
                            },
                            failure:function () {
                                XD.msg('操作失败');
                            }
                        });
                    });
                }
            },

            'placeManageGridView button[itemId=defendcar]': {  //维护记录
                click: function (view) {
                    var placeManageGridView = view.findParentByType('placeManageGridView');
                    var placeManageView = placeManageGridView.findParentByType('placeManageView');
                    var placeDefendGridView = placeManageView.down('placeDefendGridView');
                    var select = placeManageGridView.getSelectionModel().getSelection();
                    if(select.length !=1 ){
                        XD.msg('只能选择一条记录');
                        return;
                    }
                    placeDefendGridView.initGrid({placeid:select[0].get('id')});
                    placeDefendGridView.placeid = select[0].get('id');
                    placeManageView.setActiveItem(placeDefendGridView);
                }
            },

            'placeDefendGridView button[itemId=addDefend]':{ // 新增维护记录
                click:function (view) {
                    var placeDefendGridView = view.findParentByType('placeDefendGridView');
                    var placeDefendAddView = Ext.create('PlaceManage.view.PlaceDefendAddView');
                    placeDefendAddView.placeDefendGridView = placeDefendGridView;
                    var form = placeDefendAddView.down('form');
                    form.load({
                        url:'/placeManage/loadPlaceDefend',
                        method:'POST',
                        success:function (form,action) {
                        },
                        failure:function () {
                            XD.msg('操作失败');
                        }
                    });
                    placeDefendAddView.placeid = placeDefendGridView.placeid;
                    placeDefendAddView.show();
                }
            },

            'placeDefendAddView button[itemId=defendSubmit]':{ // 新增 提交
                click:function (view) {
                    var placeDefendAddView = view.findParentByType('placeDefendAddView');
                    var form = placeDefendAddView.down('form');
                    if(!form.isValid()){
                        XD.msg('存在必填项未填写');
                        return;
                    }
                    form.submit({
                        url:'/placeManage/placeDefendSubmit',
                        method:'POST',
                        params:{
                            placeid:placeDefendAddView.placeid
                        },
                        scope: this,
                        success:function (form,action) {
                            if(placeDefendAddView.title=='修改维修记录'){
                                XD.msg('修改成功');
                            }else{
                                XD.msg('新增成功');
                            }
                            placeDefendAddView.placeDefendGridView.getStore().reload();
                            placeDefendAddView.close();
                        },
                        failure:function () {
                            XD.msg('操作失败');
                        }
                    });
                }
            },

            'placeDefendAddView button[itemId=defendClose]':{  // 新增 关闭
                click:function (view) {
                    view.findParentByType('placeDefendAddView').close();
                }
            },


            'placeDefendGridView button[itemId=editDefend]':{  //修改维护记录
                click:function (view) {
                    var placeDefendGridView = view.findParentByType('placeDefendGridView');
                    var select = placeDefendGridView.getSelectionModel().getSelection();
                    if(select.length!=1){
                        XD.msg('只能选择一条数据');
                        return;
                    }
                    var placeDefendAddView = Ext.create('PlaceManage.view.PlaceDefendAddView');
                    var form = placeDefendAddView.down('form');
                    form.load({
                        url:'/placeManage/getPlaceDefendByid',
                        method:'POST',
                        params:{
                            id:select[0].get('id')
                        },
                        success:function (form,action) {
                            var respText = Ext.decode(action.response.responseText);
                            if(!respText.success){
                                XD.msg('获取表单信息失败');
                            }
                            placeDefendAddView.setTitle('修改维护记录');
                            placeDefendAddView.placeid = placeDefendGridView.placeid;
                            placeDefendAddView.placeDefendGridView = placeDefendGridView;
                            placeDefendAddView.show();
                        },
                        failure:function () {
                            XD.msg('操作失败');
                        }
                    });
                }
            },

            'placeDefendGridView button[itemId=lookDefend]':{  //查看维护记录
                click:function (view) {
                    var placeDefendGridView = view.findParentByType('placeDefendGridView');
                    var select = placeDefendGridView.getSelectionModel().getSelection();
                    if(select.length!=1){
                        XD.msg('只能选择一条数据');
                        return;
                    }
                    var placeDefendAddView = Ext.create('PlaceManage.view.PlaceDefendAddView');
                    var form = placeDefendAddView.down('form');
                    form.load({
                        url:'/placeManage/getPlaceDefendByid',
                        method:'POST',
                        params:{
                            id:select[0].get('id')
                        },
                        success:function (form,action) {
                            var respText = Ext.decode(action.response.responseText);
                            if(!respText.success){
                                XD.msg('获取表单信息失败');
                            }
                            placeDefendAddView.setTitle('查看维护记录');
                            placeDefendAddView.down('[itemId=defendSubmit]').hide();
                            placeDefendAddView.show();
                        },
                        failure:function () {
                            XD.msg('操作失败');
                        }
                    });
                }
            },

            'placeDefendGridView button[itemId=deleteDefend]':{  //删除维护记录
                click:function (view) {
                    var placeDefendGridView = view.findParentByType('placeDefendGridView');
                    var select = placeDefendGridView.getSelectionModel().getSelection();
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
                            url:'/placeManage/deletePlaceDefendByid',
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
                                    placeDefendGridView.getStore().reload();
                                }
                            },
                            failure:function () {
                                XD.msg('操作失败');
                            }
                        });
                    });
                }
            },

            'placeDefendGridView button[itemId=back]':{  //返回
                click:function (view) {
                    var placeDefendGridView = view.findParentByType('placeDefendGridView');
                    var placeManageView = placeDefendGridView.findParentByType('placeManageView');
                    var placeManageGridView = placeManageView.down('placeManageGridView');
                    placeManageView.setActiveItem(placeManageGridView);
                }
            }
        });
    }
});
