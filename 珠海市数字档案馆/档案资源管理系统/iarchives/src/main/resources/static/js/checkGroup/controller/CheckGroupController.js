/**
 * Created by Administrator on 2018/11/30.
 */

Ext.define('CheckGroup.controller.CheckGroupController', {
    extend: 'Ext.app.Controller',

    views: ['CheckGroupGridView','CheckGroupAddFromView','CheckGroupUserGridView','CheckGroupSetUserView',
    'CheckGroupAddUserView','CheckGroupOrganTreeView'],//加载view
    stores: ['CheckGroupGridStore','CheckGroupUserGridStore','CheckGroupOrganTreeStore','CheckGroupUserSelectStore'],//加载store
    models: ['CheckGroupGridModel','CheckGroupUserGridModel','CheckGroupUserSelectModel','CheckGroupOrganTreeModel'],//加载model
    init: function () {
        var checkGroupSetUserView;
        this.control({
            'checkGroupGridView': {
                afterrender: function (view) {
                    view.initGrid();
                    window.wcheckGroupGrid = view;
                }
            },

            //新增质检组
            'checkGroupGridView button[itemId="checksave"]':{
                click:function(view){
                    Ext.create("CheckGroup.view.CheckGroupAddFromView").show();
                }
            },
            //增加质检组提交表单
            'checkGroupAddFormView button[itemId="checkGroupAddSubmit"]':{
                click:function(view){
                    var form = view.findParentByType('checkGroupAddFormView').down('form');
                    var formdata = form.getValues();
                    var name = formdata['groupname'];
                    if (name === '') {
                        XD.msg('质检组名不能为空');
                        return;
                    }
                    form.submit({
                        url : '/checkGroup/checkGroupAddSubmit',
                        method : 'POST',
                        params : { // 此处可以添加额外参数

                        },
                        success : function(form, action) {
                            var respText = Ext.decode(action.response.responseText);
                            if (respText.success == true) {
                                XD.msg(respText.msg);
                                view.findParentByType('checkGroupAddFormView').close();//添加成功后关闭窗口
                                window.wcheckGroupGrid.initGrid();
                            } else {
                                XD.msg(respText.msg);
                            }
                        },
                        failure : function() {
                            XD.msg('操作失败');
                        }
                    });
                }
            },
            //增加质检组窗口关闭
            'checkGroupAddFormView button[itemId="checkGroupAddClose"]':{
                click:function(view){
                    view.findParentByType('checkGroupAddFormView').close();
                }
            },

            'checkGroupGridView button[itemId="checkmodify"]':{
                click:function(view){
                    var grid= view.findParentByType('checkGroupGridView');
                    var select = grid.getSelectionModel().getSelection();
                    if(select.length!=1){
                        XD.msg('只能选择一条数据');
                        return;
                    }
                    var checkgroupid = select[0].get('id');
                    var checkGroupAddFromView = Ext.create("CheckGroup.view.CheckGroupAddFromView");
                    var form = checkGroupAddFromView.down('form');
                    form.load({
                        url: '/checkGroup/getCheckGroupform',
                        params: {checkgroupid: checkgroupid},
                        success: function (form, action) {
                        },
                        failure: function () {
                            XD.msg('操作失败');
                        }
                    });
                    checkGroupAddFromView.show();
                }
            },
            'checkGroupGridView button[itemId="checkdelete"]':{
                click:function(view){
                    var grid= view.findParentByType('checkGroupGridView');
                    var select = grid.getSelectionModel().getSelection();
                    if(select.length==0){
                        XD.msg('请至少选择一条数据');
                        return;
                    }
                    var checkgroupids = [];
                    for(var i=0;i<select.length;i++){
                        checkgroupids.push(select[i].get('id'));
                    }
              XD.confirm('确定要删除这'+select.length+'数据吗',function () {
                  Ext.Ajax.request({
                      url: '/checkGroup/delCheckGroup',
                      async: false,
                      params: {
                          checkgroupids: checkgroupids
                      },
                      success: function (response) {
                          if(Ext.decode(response.responseText).success){
                              XD.msg('成功删除'+Ext.decode(response.responseText).data+'条数据');
                          }else{
                              XD.msg('删除失败');
                          }
                      },
                      failure: function () {
                          XD.msg('操作失败');
                      }
                  });
                  grid.getStore().reload();
              },this);
                }
            },

            'checkGroupGridView button[itemId="checkuser"]':{
                click:function(view){
                    var grid= view.findParentByType('checkGroupGridView');
                    var select = grid.getSelectionModel().getSelection();
                    if(select.length!=1){
                        XD.msg('只能选择一条数据');
                        return;
                    }
                    var checkgroupid = select[0].get('id');
                    checkGroupSetUserView = Ext.create("CheckGroup.view.CheckGroupSetUserView");
                    var checkGroupUserGridView =checkGroupSetUserView.down('checkGroupUserGridView');
                    var checkuserstore = checkGroupUserGridView.getStore();
                    checkuserstore.proxy.extraParams.checkgroupid = checkgroupid;
                    checkuserstore.reload();
                    window.checkgroupid = checkgroupid;
                    checkGroupSetUserView.show();
                }
            },

            'checkGroupUserGridView button[itemId="checkusersave"]':{
                click:function(view){
                    Ext.Ajax.request({
                        params: {checkgroupid:window.checkgroupid},
                        url: '/checkGroup/getUser',
                        method: 'POST',
                        sync: true,
                        success: function (resp) {
                            var respText = Ext.decode(resp.responseText);
                            if (respText.success == true) {
                                var checkGroupAddUserView = Ext.create("CheckGroup.view.CheckGroupAddUserView");
                                checkGroupAddUserView.down('itemselector').getStore().load({
                                    callback:function(){
                                        checkGroupAddUserView.down('itemselector').setValue(respText.data);
                                    }
                                });
                                checkGroupAddUserView.show();
                            }else{
                                XD.msg(respText.msg);
                            }
                        },
                        failure: function() {
                            XD.msg('操作失败');
                        }
                    });
                }
            },

            'checkGroupOrganTreeView':{
                select: this.getUserByOrganid
            },

            'checkGroupAddUserView button[itemId="userSelectSubmit"]':{
                click:function(view){
                    var selectView = view.findParentByType('checkGroupAddUserView');
                    if(selectView.down('itemselector').getValue().length==0){
                        XD.msg('至少选择一个用户');
                        return;
                    }

                    Ext.Ajax.request({
                        params: {
                            checkgroupid: window.checkgroupid,
                            userids:selectView.down('itemselector').getValue()},
                        url: '/checkGroup/setCheckUser',
                        method: 'POST',
                        sync: true,
                        success: function (resp) {
                            var respText = Ext.decode(resp.responseText);
                            if (respText.success == true) {
                                XD.msg(respText.msg);
                                checkGroupSetUserView.down('checkGroupUserGridView').getStore().reload();
                                view.findParentByType('checkGroupAddUserView').close();
                            }else{
                                XD.msg(respText.msg);
                            }
                        },
                        failure: function() {
                            XD.msg('操作失败');
                        }
                    });

                }
            },

            'checkGroupAddUserView button[itemId="userSelectClose"]':{
                click:function(view){
                    var selectView = view.findParentByType('checkGroupAddUserView');
                    selectView.close();
                }
            },


            'checkGroupUserGridView button[itemId="checkuserdelete"]':{
                click:function(view){
                    var grid= view.findParentByType('checkGroupUserGridView');
                    var select = grid.getSelectionModel().getSelection();
                    if(select.length==0){
                        XD.msg('请至少选择一条数据');
                        return;
                    }
                    var checkuserids = [];
                    for(var i=0;i<select.length;i++){
                        checkuserids.push(select[i].get('id'))
                    }
                    XD.confirm('确定要删除这'+select.length+'数据吗',function () {
                    Ext.Ajax.request({
                        params: {
                            checkuserids:checkuserids
                        },
                        url: '/checkGroup/delCheckUser',
                        method: 'POST',
                        async: false,
                        success: function (response) {
                            if(Ext.decode(response.responseText).success){
                                XD.msg('成功删除'+Ext.decode(response.responseText).data+'条数据');
                            }else{
                                XD.msg('删除失败');
                            }
                        },
                        failure: function () {
                            XD.msg('操作失败');
                        }
                    });
                        grid.getStore().reload();
                    },this);
                }
            },

            'checkGroupUserGridView button[itemId="checkuserback"]':{
                click:function(view){
                    checkGroupSetUserView.close();
                }
            }

        });
    },
    getUserByOrganid:function(treemodel, record){
        var organid = record.data.fnid;
        var userstore = treemodel.view.findParentByType('checkGroupAddUserView').down('itemselector').getStore();
        userstore.reload({params:{organid:organid}});
    }
});
