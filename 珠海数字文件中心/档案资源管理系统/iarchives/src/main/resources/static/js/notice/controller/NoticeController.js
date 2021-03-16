Ext.define('Notice.controller.NoticeController',{
    extend: 'Ext.app.Controller',
    views:['NoticeGridView','NoticeAddView','LookView','StickView'],
    stores:['NoticeStore'],
    models:['NoticeModel'],
    init:function () {
        this.control({
            'noticeGridView':{
                afterrender:function (view) {
                    view.initGrid();
                }
            },
            'noticeGridView button[itemId=add]':{
                click:function (btn) {
                    this.showAddView(btn,'新增');
                }
            },
            'noticeGridView button[itemId=edit]':{
                click:function (btn){
                    var view = btn.findParentByType('noticeGridView');
                    var select = view.getSelectionModel();
                    if (select.getCount() != 1){
                        XD.msg('请选择一条数据！');
                        return ;
                    }
                    this.showAddView(btn,'修改');
                }
            },
            'noticeGridView button[itemId=look]':{
                click:function (btn){
                    var view = btn.findParentByType('noticeGridView');
                    var select = view.getSelectionModel();
                    if (select.getCount() != 1){
                        XD.msg('请选择一条数据！');
                        return ;
                    }
                    var win = Ext.create('Notice.view.LookView',{});
                    win.show();
                    var store = select.getSelection();
                    setTimeout(function () {
                        Ext.Ajax.request({
                            method: 'POST',
                            url: '/notice/findNotice',
                            params:{
                                noticeID:store[0].get('noticeID')
                            },
                            scope: this,
                            success: function (response, opts) {
                                var data = Ext.decode(response.responseText).data;
                                win.down('[itemId=title]').setText(data.title);
                                if ("" == data.publishtime||data.publishtime==undefined){
                                    win.down('[itemId=date]').setText('暂未发布');
                                }else{
                                    var getTime = data.publishtime.replace(/-/g,'/');
                                    var pudate = new Date(getTime);
                                    var pbtime = format(pudate);
                                    win.down('[itemId=date]').setText('发布日期：'+ pbtime);
                                }
                                document.getElementById('editFrame').contentWindow.setHtml(data.content);
                                document.getElementById('editFrame').contentWindow.hideButton();
                            }
                        });
                        Ext.Ajax.request({
                            method: 'POST',
                            url: '/notice/electronicsFile/'+ store[0].get('noticeID') + '/',
                            success: function (response, opts) {
                                var data = Ext.decode(response.responseText);
                                if(data.length>0){
                                    var lable = [];
                                    lable.push({xtype: 'label',text: '相关附件：(双击文件可以下载)',margin: '0 0 5 20'});
                                    for(var i=0;i<data.length;i++){
                                        lable.push({
                                            xtype: 'label',text: data[i].filename,eleid:data[i].eleid,margin: '0 0 5 35',listeners: {
                                                render: function (view) {//渲染后添加双击事件
                                                    view.addListener("dblclick", function () {
                                                        window.open("/inform/openFile?eleid=" + view.eleid +'&fileName=' +view.text);
                                                    }, null, {element: 'el'});
                                                },
                                                scope: this
                                            }
                                        });
                                    }
                                }
                                win.add(lable);
                            }
                        });
                    }, 100);

                }
            },
            'noticeGridView button[itemId=delete]':{
                click:function (btn) {
                    var view = btn.findParentByType('noticeGridView');
                    var select = view.getSelectionModel();
                    if (select.getCount() < 1){
                        XD.msg('请选择数据！');
                        return ;
                    }
                    var store = select.selected.items;
                    var noticeIDs = [];
                    for (var i = 0; i < store.length; i++) {
                        noticeIDs.push(store[i].get('noticeID'));
                    }
                    Ext.Ajax.request({
                        url:'/notice/deleteNotice',
                        method:'POST',
                        params:{
                            noticeIDs:noticeIDs
                        },
                        success:function (resp) {
                            var responseText = Ext.decode(resp.responseText);
                            XD.msg(responseText.msg);
                            if (responseText.msg == '删除成功！'){
                                view.delReload(select.getCount());
                            }else{
                                return;
                            }
                        },
                        failure:function () {
                            XD.msg('操作失败！');
                            return ;
                        }
                    });
                }
            },
            'noticeGridView button[itemId=publishBtnID]':{
                click:function (btn) {
                    this.updatePublish(btn,'1');
                }
            },
            'noticeGridView button[itemId=canclePublishBtnID]':{
                click:function (btn) {
                    this.updatePublish(btn,'0');
                }
            },
            'noticeGridView button[itemId=stick]':{
                click:function (btn) {
                    var view = btn.findParentByType('noticeGridView');
                    var select = view.getSelectionModel();
                    var notices = select.getSelection();
                    if (select.getCount() < 1){
                        XD.msg('请选择数据！');
                        return ;
                    }
                    var ids = [];
                    for (var i = 0; i < notices.length; i++) {
                        ids.push(notices[i].get('noticeID'));
                    }
                    var stickWin = Ext.create('Notice.view.StickView', {
                        notices: ids,
                        informGrid: view
                    });
                    stickWin.show();
                }
            },
            'noticeGridView button[itemId=cancelStick]':{
                click:function (btn) {
                    var view = btn.findParentByType('noticeGridView');
                    var select = view.getSelectionModel();
                    var notices = select.getSelection();
                    if (select.getCount() < 1){
                        XD.msg('请选择数据！');
                        return ;
                    }
                    var ids = [];
                    for (var i = 0; i < notices.length; i++) {
                        ids.push(notices[i].get('noticeID'));
                    }
                    Ext.Ajax.request({
                        url:'/notice/cancelStick',
                        method:'POST',
                        params:{
                            'ids':ids
                        },
                        success:function (resp) {
                            var respText = Ext.decode(resp.responseText);
                            XD.msg(respText.msg);
                            btn.findParentByType('noticeGridView').getStore().reload();
                        },
                        failure:function () {
                            XD.msg('操作失败');
                            return ;
                        }
                    });
                }
            },
            'stickView button[itemId=stickSubmit]':{
                click:function (btn) {
                    var form = btn.findParentByType('stickView').down('form');
                    Ext.Ajax.request({
                        params: {ids: btn.up('stickView').notices, level: form.down('combobox').value},
                        url: '/notice/setStick',
                        method: 'POST',
                        success: function (resp) {
                            var respText = Ext.decode(resp.responseText);
                            XD.msg(respText.msg);
                            btn.up('stickView').informGrid.notResetInitGrid();
                            btn.up('stickView').close();
                        },
                        failure: function () {
                            XD.msg('操作失败');
                        }
                    });
                }
            },
            'stickView button[itemId=stickClose]':{
                click:function (btn) {
                    btn.up('stickView').close();
                }
            },
            'noticeAddView button[itemId=submit]':{
                click:function (btn) {
                    this.submitNotice(btn);
                }
            },
            'noticeAddView button[itemId=close]':{
                click:function (btn){
                    var view = btn.findParentByType('noticeAddView');
                    view.close();
                }
            }
        })
    },

    updatePublish:function(btn,state){
        var view = btn.findParentByType('noticeGridView');
        var select = view.getSelectionModel();
        if (select.getCount() < 1){
            XD.msg('请选择数据！');
            return ;
        }
        var store = select.selected.items;
        var noticeIDs = [];
        for (var i = 0; i < store.length; i++) {
            noticeIDs.push(store[i].get('noticeID'));
        }
        Ext.Ajax.request({
            url:'/notice/publish',
            method:"POST",
            params:{
                noticeIDs:noticeIDs,
                state: state
            },
            success:function (resp) {
                var responseText = Ext.decode(resp.responseText);
                if (responseText.success == true) {
                    XD.msg(responseText.msg);
                    view.delReload(select.getCount());
                }
            }
        });
    },

    submitNotice:function(btn){
        var view = btn.findParentByType('noticeAddView');
        var form = view.down('form');
        var container = view .down('[itemId=container]');
        var values = form.getValues();
        if (!form.isValid()) {
            XD.msg("有必填项未填写！");
            return ;
        }
        // if(values['title'].length>=100){
        //     XD.msg("标题字符过长，请修改！");
        //     return;
        // }
        // console.info(values['title'].length);
        var html = document.getElementById('editFrame').contentWindow.getHtml();
        Ext.Ajax.request({
            url:'/notice/addNotice',
            method:'POST',
            params:{
                noticeID:values['noticeID'],
                title: values['title'],
                organ:values['organ'],
                publishstate:values['publishstate'],
                stick:values['stick'],
                html:html,
                eleids:container.eleids
            },
            success:function (resp) {
                var respText = Ext.decode(resp.responseText);
                XD.msg(respText.msg);
                if ("操作成功" == respText.msg){
                    var noticeGridView = view.noticeGridView;
                    noticeGridView.getStore().reload();
                    view.close();
                }else{
                    return;
                }
            },
            failure:function () {
                XD.msg('添加失败！');
                return;
            }
        });
    },

    showAddView:function (btn,operate) {
        var view = btn.findParentByType('noticeGridView');
        window.cardobj = {grid: view};
        var editView = Ext.create('Notice.view.NoticeAddView',{
            title:operate,
            noticeGridView:view
        });
        var container=editView.down('[itemId=container]');
        container.eleids=[];
        container.fileName=[];
        window.wpostedview = editView;
        window.wpostedview.postedUserData = undefined;
        window.wpostedview.postedUsergroupData = undefined;
        var form = editView.down('form');
        if (operate != '新增') {
            var select = view.getSelectionModel().getSelection();

            var noticeID = form.getForm().findField('noticeID');
            noticeID.setValue(select[0].data['noticeID']);

            var title = form.getForm().findField("title");
            title.setValue(select[0].data['title']);

            var organ = form.getForm().findField("organ");
            organ.setValue(select[0].data['organ']);

            var publishstate = form.getForm().findField("publishstate");
            publishstate.setValue(select[0].data['publishstate']);

            var stick = form.getForm().findField("stick");
            stick.setValue(select[0].data['stick']);

            if (operate == '修改') {
                setTimeout(function () {
                    editView.down('form').load({
                        url: '/notice/findNotice',
                        params:{
                            noticeID:select[0].data['noticeID'],
                        },
                        success : function(form, action) {
                            var data=action.result.data;
                            document.getElementById('editFrame').contentWindow.setHtml(select[0].data['content']);
                        },
                        failure: function() {
                            //XD.msg('操作失败');
                        }
                    });

                }, 100);
            }
             if (operate == '查看') {
                 title.setReadOnly(true);
                 organ.setReadOnly(true);
                 publishstate.setReadOnly(true);
                 stick.setReadOnly(true);
                 form.down('[itemId=upload]').hide();
                 form.down('label').hide();
                 editView.down('[itemId=submit]').hide();
                 editView.remove(editView.items.items[1]);
                 form.down('[itemId=content]').hidden = false;
                 form.down('[itemId=content]').setValue(select[0].data['content']);
                 setTimeout(function () {
                     Ext.Ajax.request({
                         method: 'POST',
                         url: '/notice/findNotice/'+select[0].get('noticeID')+'/',
                         success: function (response, opts) {
                             var data = Ext.decode(response.responseText);
                             if(data.length>0){
                                 var lable = [];
                                 lable.push({xtype: 'label',text: '相关附件：(双击文件可以下载)',margin: '0 0 5 20'});
                                 for(var i=0;i<data.length;i++){
                                     lable.push({
                                        xtype: 'label',text: data[i].filename,eleid:data[i].eleid,margin: '0 0 5 35',listeners: {
                                             render: function (view) {//渲染后添加双击事件
                                                 view.addListener("dblclick", function () {
                                                     //解决IE浏览器中文乱码
                                                     var url = encodeURI("/inform/openFile?eleid=" + view.eleid +'&fileName=' +view.text);
                                                     window.open(url);
                                                 }, null, {element: 'el'});
                                             },
                                             scope: this
                                         }
                                     });
                                 }
                             }
                             editView.add(lable);
                         }
                     });
                 }, 100);
             }
        }
        editView.show();
    }
});

function format(date) {
    var year = date.getFullYear();
    var mon = (date.getMonth()+1 < 10 ? '0'+(date.getMonth()+1) : date.getMonth()+1);
    var day = (date.getDate() < 10 ? '0'+(date.getDate()) : date.getDate());
    var hou = (date.getHours() < 10 ? '0'+(date.getHours()) : date.getHours());
    var min = (date.getMinutes() < 10 ? '0'+(date.getMinutes()) : date.getMinutes());
    return  year + "-" + mon + "-" + day +" "+ hou +":"+min;
}