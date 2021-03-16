Ext.define('PartyBuilding.controller.PartyBuildingController',{
    extend:'Ext.app.Controller',
    views:['PartyBuildingGridView','PartyBuildingAddView','StickView','LookView'],
    stores:['PartyBuildingGridStore'],
    models:['PartyBuildingGridModel'],
    init:function () {
        this.control({
            'partyBuildingGridView':{
                afterrender:function (view) {
                    view.initGrid();
                },
                celldblclick:function (thisp,row,col,model) {//单元格双击修改表单对应值
                    var view = thisp.up('partyBuildingGridView');
                    var select = view.getSelectionModel();
                    if (select.getCount() != 1){
                        XD.msg('请选择一条数据！');
                        return ;
                    }
                    // this.showAddView(btn,'查看');
                    var win = Ext.create('PartyBuilding.view.LookView',{});
                    win.show();
                    var store = select.getSelection();
                    setTimeout(function () {
                        Ext.Ajax.request({
                            method: 'POST',
                            url: '/partyBuilding/findPartyBuilding',
                            params:{
                                id:store[0].get('partybuildingID')
                            },
                            scope: this,
                            success: function (response, opts) {
                                var data = Ext.decode(response.responseText).data[0];
                                win.down('[itemId=title]').setText(data['title']);
                                if ("" == data['publishtime']){
                                    win.down('[itemId=date]').setText('暂未发布');
                                }else{
                                    win.down('[itemId=date]').setText('发布日期：'+new Date(data['publishtime']).format("yyyy-MM-dd hh:mm:ss"));
                                }
                                document.getElementById('editFrame').contentWindow.setHtml(data['content']);
                                document.getElementById('editFrame').contentWindow.hideButton();
                            }
                        });
                        Ext.Ajax.request({
                            method: 'POST',
                            url: '/partyBuilding/electronicsFile/'+ store[0].get('partybuildingID') + '/',
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
            'partyBuildingGridView button[itemId=add]':{
                click:function (btn) {
                    this.showAddView(btn,'新增');
                }
            },
            'partyBuildingGridView button[itemId=edit]':{
                click:function (btn){
                    var view = btn.findParentByType('partyBuildingGridView');
                    var select = view.getSelectionModel();
                    if (select.getCount() != 1){
                        XD.msg('请选择一条数据！');
                        return ;
                    }
                    this.showAddView(btn,'修改');
                }
            },
            'partyBuildingGridView button[itemId=delete]':{
                click:function (btn) {
                    var view = btn.findParentByType('partyBuildingGridView');
                    var select = view.getSelectionModel();
                    if (select.getCount() < 1){
                        XD.msg('请选择数据！');
                        return ;
                    }
                    var store = select.selected.items;
                    var partybuildingIDs = [];
                    for (var i = 0; i < store.length; i++) {
                        partybuildingIDs.push(store[i].get('partybuildingID'));
                    }
                    Ext.Ajax.request({
                        url:'/partyBuilding/deletePartyBuilding',
                        method:'POST',
                        params:{
                            partybuildingIDs:partybuildingIDs
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
            'partyBuildingGridView button[itemId=look]':{
                click:function (btn){
                    var view = btn.findParentByType('partyBuildingGridView');
                    var select = view.getSelectionModel();
                    if (select.getCount() != 1){
                        XD.msg('请选择一条数据！');
                        return ;
                    }
                    // this.showAddView(btn,'查看');
                    var win = Ext.create('PartyBuilding.view.LookView',{});
                    win.show();
                    var store = select.getSelection();
                    setTimeout(function () {
                        Ext.Ajax.request({
                            method: 'POST',
                            url: '/partyBuilding/findPartyBuilding',
                            params:{
                                id:store[0].get('partybuildingID')
                            },
                            scope: this,
                            success: function (response, opts) {
                                var data = Ext.decode(response.responseText).data[0];
                                win.down('[itemId=title]').setText(data['title']);
                                if ("" == data['publishtime']){
                                    win.down('[itemId=date]').setText('暂未发布');
                                }else{
                                    win.down('[itemId=date]').setText('发布日期：'+new Date(data['publishtime']).format("yyyy-MM-dd hh:mm:ss"));
                                }
                                document.getElementById('editFrame').contentWindow.setHtml(data['content']);
                                document.getElementById('editFrame').contentWindow.hideButton();
                            }
                        });
                        Ext.Ajax.request({
                            method: 'POST',
                            url: '/partyBuilding/electronicsFile/'+ store[0].get('partybuildingID') + '/',
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
            'partyBuildingGridView button[itemId=publishBtnID]':{
                click:function (btn) {
                    this.updatePublish(btn,'1');
                }
             },
            'partyBuildingGridView button[itemId=canclePublishBtnID]':{
                click:function (btn) {
                    this.updatePublish(btn,'0');
                }
            },
            'partyBuildingGridView button[itemId=stick]':{
                click:function (btn) {
                    var view = btn.findParentByType('partyBuildingGridView');
                    var select = view.getSelectionModel();
                    var partybuildings = select.getSelection();
                    if (select.getCount() < 1){
                        XD.msg('请选择数据！');
                        return ;
                    }
                    var ids = [];
                    for (var i = 0; i < partybuildings.length; i++) {
                        ids.push(partybuildings[i].get('partybuildingID'));
                    }
                    var stickWin = Ext.create('PartyBuilding.view.StickView', {
                        partybuildingIDs: ids,
                        informGrid: view
                    });
                    stickWin.show();
                }
            },
            'partyBuildingGridView button[itemId=cancelStick]':{
                click:function (btn) {
                    var view = btn.findParentByType('partyBuildingGridView');
                    var select = view.getSelectionModel();
                    var partybuildings = select.getSelection();
                    if (select.getCount() < 1){
                        XD.msg('请选择数据！');
                        return ;
                    }
                    var ids = [];
                    for (var i = 0; i < partybuildings.length; i++) {
                        ids.push(partybuildings[i].get('partybuildingID'));
                    }
                    Ext.Ajax.request({
                        url:'/partyBuilding/cancelStick',
                        method:'POST',
                        params:{
                            'ids':ids
                        },
                        success:function (resp) {
                            var respText = Ext.decode(resp.responseText);
                            XD.msg(respText.msg);
                            btn.findParentByType('partyBuildingGridView').getStore().reload();
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
                        params: {ids: btn.up('stickView').partybuildingIDs, level: form.down('combobox').value},
                        url: '/partyBuilding/setStick',
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
            'partyBuildingAddView button[itemId=submit]':{
                click:function (btn) {
                    this.submitPartyBuilding(btn);
                }
            },
            'partyBuildingAddView button[itemId=close]':{
                click:function (btn){
                    var view = btn.findParentByType('partyBuildingAddView');
                    view.close();
                }
            }
        })
    },

    updatePublish:function(btn,state){
        var view = btn.findParentByType('partyBuildingGridView');
        var select = view.getSelectionModel();
        if (select.getCount() < 1){
            XD.msg('请选择数据！');
            return ;
        }
        var store = select.selected.items;
        var partybuildingIDs = [];
        for (var i = 0; i < store.length; i++) {
            partybuildingIDs.push(store[i].get('partybuildingID'));
        }
        Ext.Ajax.request({
            url:'/partyBuilding/publish',
            method:"POST",
            params:{
                partybuildingIDs:partybuildingIDs,
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

    showAddView:function(btn,operate){
        var view = btn.findParentByType('partyBuildingGridView');
        window.cardobj = {grid: view};
        var editView = Ext.create('PartyBuilding.view.PartyBuildingAddView',{
            title:operate,
            operate: 'edit', partyBuildingGridView:view
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

            var partybuildingID = form.getForm().findField('partybuildingID');
            partybuildingID.setValue(select[0].data['partybuildingID']);

            var title = form.getForm().findField("title");
            title.setValue(select[0].data['title']);

            var organ = form.getForm().findField("organ");
            organ.setValue(select[0].data['organ']);

            var publishstate = form.getForm().findField("publishstate");
            publishstate.setValue(select[0].data['publishstate']);

            var stick = form.getForm().findField("stick");
            stick.setValue(select[0].data['stick']);

                setTimeout(function () {
                    /*Ext.Ajax.request({
                        method: 'POST',
                        url: '/partyBuilding/findPartyBuilding?id='+select[0].data['partybuildingID'],
                        success: function (response, opts) {
                            document.getElementById('editFrame').contentWindow.setHtml(select[0].data['content']);
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
                    });*/

                    editView.down('form').load({
                        url: '/partyBuilding/findPartyBuilding?id='+select[0].data['partybuildingID'],
                        /*params:{
                            partybuildingID:partybuildingID,
                        },*/
                        //waitMsg: '请稍后......',
                        success : function(form, action) {
                            //var data=action.result.data[0];
                            document.getElementById('editFrame').contentWindow.setHtml(select[0].data['content']);
                            editView.down('form').items.get(0).setValue(select[0].data['partybuildingID']);
                        },
                        failure: function() {
                            XD.msg('操作失败');
                        }
                    });
                }, 100);
            }
            // if (operate == '查看') {
            //     title.setReadOnly(true);
            //     organ.setReadOnly(true);
            //     publishstate.setReadOnly(true);
            //     stick.setReadOnly(true);
            //     form.down('[itemId=upload]').hide();
            //     form.down('label').hide();
            //     editView.down('[itemId=submit]').hide();
            //     editView.remove(editView.items.items[1]);
            //     form.down('[itemId=content]').hidden = false;
            //     form.down('[itemId=content]').setValue(select[0].data['content']);
            //     setTimeout(function () {
            //         Ext.Ajax.request({
            //             method: 'POST',
            //             url: '/partyBuilding/electronicsFile/'+select[0].get('partybuildingID')+'/',
            //             success: function (response, opts) {
            //                 var data = Ext.decode(response.responseText);
            //                 if(data.length>0){
            //                     var lable = [];
            //                     lable.push({xtype: 'label',text: '相关附件：(双击文件可以下载)',margin: '0 0 5 20'});
            //                     for(var i=0;i<data.length;i++){
            //                         lable.push({
            //                             xtype: 'label',text: data[i].filename,eleid:data[i].eleid,margin: '0 0 5 35',listeners: {
            //                                 render: function (view) {//渲染后添加双击事件
            //                                     view.addListener("dblclick", function () {
            //                                         //解决IE浏览器中文乱码
            //                                         var url = encodeURI("/inform/openFile?eleid=" + view.eleid +'&fileName=' +view.text);
            //                                         window.open(url);
            //                                     }, null, {element: 'el'});
            //                                 },
            //                                 scope: this
            //                             }
            //                         });
            //                     }
            //                 }
            //                 editView.add(lable);
            //             }
            //         });
            //     }, 100);
            // }
        editView.show();
    },

    submitPartyBuilding:function (btn) {
        var view = btn.findParentByType('partyBuildingAddView');
        var form = view.down('form');
        var container = view .down('[itemId=container]');
        var values = form.getValues();
        if ("" == values['title'] || null == values['title']) {
            XD.msg("有必填项未填写！");
            return ;
        }
        var html = document.getElementById('editFrame').contentWindow.getHtml();
        Ext.Ajax.request({
            url:'/partyBuilding/addPartyBuilding',
            method:'POST',
            params:{
                partybuildingID:values['partybuildingID'],
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
                    var partyBuildingGridView = view.partyBuildingGridView;
                    partyBuildingGridView.getStore().reload();
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
    }
});