/**
 * Created by zdw on 2020/03/20
 */
Ext.define('Showroom.controller.ShowroomController', {
    extend: 'Ext.app.Controller',

    views: [
        'ShowroomView','ShowroomGridView',
        'ShowroomAddForm'
    ],//加载view
    stores: ['ShowroomGridStore'],//加载store
    models: ['ShowroomGridModel'],//加载model

    init: function () {
        this.control({
            'showroomGridView':{
                afterrender:function (view) {
                    var buttons = view.down("toolbar").query('button');
                    var tbseparator = view.down("toolbar").query('tbseparator');
                    var type;
                    if(buttonflag=='1'){//利用平台

                        type = 'self';
                    }else{//管理平台

                        type = 'all';
                    }
                    view.initGrid({type:type});
                    window.showroomGrid=view;
                }
            },
            'showroomGridView button[itemId=showroomAdd]':{//增加
                click:this.addHandler
            },
            'showroomGridView button[itemId=showroomDel]':{//删除
                click:this.delHandler
            },
            'showroomGridView button[itemId=showroomEdit]':{//修改
                click:this.editHandler
            },
            'showroomAddForm':{//添加键盘监控
                afterrender:this.addKeyAction
            },

            'showroomAddForm button[itemId=save]':{//增加展厅　保存
                click:this.addSubmit
            },
            'showroomAddForm button[itemId=back]':{//增加展厅　返回
                click:function (btn) {
                    var showroomAddFormView = btn.up('showroomAddForm');
                    showroomAddFormView.close();
                    window.showroomGrid.getStore().reload();
                }
            }
        });
    },

    //获取展厅管理应用视图
    findView: function (btn) {
        return btn.up('showroom');
    },

    //获取增加展厅表单界面视图
    findAddformView: function (btn) {
        return this.findView(btn).down('showroomAddForm');
    },

    //获取列表界面视图
    findGridView: function (btn) {
        return this.findView(btn).getComponent('gridview');
    },


    //切换到增加展厅表单界面视图
    activeAddform: function (btn) {
        var view = this.findView(btn);
        var addformview = this.findAddformView(btn);
        view.setActiveItem(addformview);
        return addformview;
    },

    //切换到修改展厅表单界面视图
    activeLookform: function (btn) {
        var view = this.findView(btn);
        var lookformview = this.findLookformView(btn);
        view.setActiveItem(lookformview);
        return lookformview;
    },

    //切换到列表界面视图
    activeGrid: function (btn,flag) {
        var view = this.findView(btn);
        var addform = this.findAddformView(btn);
        var grid = this.findGridView(btn);
        view.setActiveItem(grid);
        addform.saveBtn = undefined;
        if(flag){//根据参数确定是否需要刷新数据
            grid.notResetInitGrid();
        }
    },

    addHandler:function (btn) {//打开增加展厅表
        //var addform = this.findAddformView(btn);
        var addform = Ext.create('Showroom.view.ShowroomAddForm',{title: '新增展厅',operate:'add'});
        window.addType='add';//标记增加类型
        addform.saveBtn = addform.down('[itemId=save]');
        addform.operateFlag = 'add';
        addform.show();
        //this.initAddformData(addform);
    },


    delHandler:function (btn) {//删除展厅信息
        var grid = this.findGridView(btn);
        var record = grid.getSelectionModel().getSelection();
        if (record.length < 1) {
            XD.msg('请选择需要删除的展厅信息');
            return;
        }
        XD.confirm('确定要删除这' + record.length + '条数据吗',function(){
            var tmp = [];
            for (var i = 0; i < record.length; i++) {
                tmp.push(record[i].get('showroomid'));
            }
            var showroomids = tmp.join(',');
            Ext.Ajax.request({
                method: 'DELETE',
                url: '/showroom/showrooms/' + showroomids,
                success: function (response) {
                    XD.msg(Ext.decode(response.responseText).msg);
                    grid.delReload(record.length);
                }
            })
        },this);
    },

    lookHandler:function (btn) {//查看展厅信息
        var grid = this.findGridView(btn);
        var record = grid.getSelectionModel().getSelection();
        if (record.length != 1) {
            XD.msg('请选择一条需要查看的展厅信息');
            return;
        }
        var showroomid = record[0].get('showroomid');
        var lookform = this.findLookformView(btn);
        this.initLookformData(lookform,showroomid);
    },

    editHandler:function (btn) {//查看展厅信息
        var grid = this.findGridView(btn);
        var record = grid.getSelectionModel().getSelection();
        if (record.length != 1) {
            XD.msg('请选择一条需要修改的展厅信息');
            return;
        }
        window.addType='edit';//标记增加类型
        /*var showroomid = record[0].get('showroomid');
        var lookform = this.findLookformView(btn);
        this.initLookformData(lookform,showroomid);*/

        var editform = Ext.create('Showroom.view.ShowroomAddForm',{title: '修改展厅',operate:'edit'});
        //window.wpostedview = postedview;
        editform.show();
        var container=editform.down('[itemId=container]');
        container.eleids=[];
        container.fileName=[];

        var ids = [];
        for(var i=0;i<record.length;i++){
            ids.push(record[i].get('id'));
        }
        setTimeout(function () {
            Ext.Ajax.request({
                method: 'POST',
                url: '/showroom/electronicsFile/'+record[0].get('id') + '/',
                success: function (response, opts) {
                    var data = Ext.decode(response.responseText);
                    var lable = [];
                    for(var i=0;i<data.length;i++){
                        container.eleids.push(data[i].eleid);
                        container.fileName.push(data[i].filename);
                        lable.push({xtype: 'label',text: data[i].filename
                            ,eleid:data[i].eleid});
                    }
                    container.add(lable);
                }
            });

            editform.down('form').load({
                method: 'GET',
                url: '/showroom/showrooms/' + record[0].get('id'),
                //waitMsg: '请稍后......',
                success : function(form, action) {
                    var data=action.result.data;
                    /*editform.down('form').down('datefield').setValue(new Date(data['limitdate']));
                    editform.down('form').down('datefield').setMinValue(new Date(data['informdate']));*/
                    document.getElementById('editFrame').contentWindow.setHtml(data['content']);
                },
                failure: function() {
                    XD.msg('操作失败');
                }
            });
        }, 100);
    },

    addSubmit:function (btn) {//增加展厅信息保存
        var showroomAddFormView = btn.up('showroomAddForm');
        var container=showroomAddFormView .down('[itemId=container]');
        var form = showroomAddFormView.down('form');
        var data = form.getValues();
        var id = data['showroomid'];
        var title = data['title'];
        var audiences = data['audiences'];
        var sequence = data['sequence'];
        var flag = data['flag'];
        if(title==''||audiences==''||sequence==''){
            XD.msg('有必填项未填写');
            return;
        }
        var html = document.getElementById('editFrame').contentWindow.getHtml();

        var url = '/showroom/showrooms';
        var params = {
            showroomid:id,
            title:title,
            content:html,
            audiences:audiences,
            sequence:sequence,
            flag:flag,
            eleids:container.eleids
        };

        if(showroomAddFormView.operate=='edit'){
            url = '/showroom/editShowroom';
            params.id = id;
        }
        Ext.Ajax.request({
            params: params,
            url: url,
            method: 'POST',
            sync: true,
            success: function (resp) {
                var respText = Ext.decode(resp.responseText);
                XD.msg(respText.msg);
                showroomAddFormView.close();
                window.showroomGrid.getStore().reload();
            },
            failure : function() {
                XD.msg('操作失败');
            }
        });
    },

    initAddformData:function(form){
        // form.reset();
        this.activeAddform(form);
        var askmanField = form.getForm().findField('askman');
        var asktimeField = form.getForm().findField('asktime');
        if(asktimeField){
            asktimeField.setValue(Ext.util.Format.date(new Date(),'Y-m-d H:i:s'));//自动设置展厅增加日期时间
        }
        if(askmanField){
            Ext.Ajax.request({//自动设置投件人真实姓名
                async:false,
                url: '/user/getUserRealname',
                success:function (response) {
                    askmanField.setValue(Ext.decode(response.responseText).data);
                }
            });
        }
    },

    initLookformData:function(form,showroomid){
        form.reset();
        this.activeLookform(form);
        Ext.Ajax.request({
            method: 'GET',
            scope: this,
            url: '/showroom/showrooms/' + showroomid,
            success: function (response) {
                var showroom = Ext.decode(response.responseText);
                form.loadRecord({getData: function () {return showroom;}});
            }
        });
    },

    //监听键盘按下事件
    addKeyAction:function (view) {
        var controller = this;
        view.saveBtn = view.down('[itemId=save]');
        document.onkeydown = function () {
            var oEvent = window.event;
            if (oEvent.ctrlKey && !oEvent.shiftKey && !oEvent.altKey && oEvent.keyCode == 83) { //这里只能用alt，shift，ctrl等去组合其他键event.altKey、event.ctrlKey、event.shiftKey 属性
                // XD.msg('Ctrl+S');
                Ext.defer(function () {
                    if(view.saveBtn && view.operateFlag=='reply'){//此处若不增加operateFlag判断，点击树节点后初次渲染showroom表单时，按下ctrl+s会调用此方法
                        controller.replySubmit(view.saveBtn);//回复
                    }
                    if(view.saveBtn && view.operateFlag=='add'){//此处若不增加operateFlag判断，点击树节点后初次渲染showroom表单时，按下ctrl+s会调用此方法
                        controller.addSubmit(view.saveBtn);//增加
                    }
                },1);
                event.returnValue = false;//阻止event的默认行为
                // return false;//阻止event的默认行为
            }
        }
    }
});