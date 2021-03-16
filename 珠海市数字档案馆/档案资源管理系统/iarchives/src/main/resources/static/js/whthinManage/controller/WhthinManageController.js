/**
 * Created by xd on 2017/10/21.
 */
Ext.define('WhthinManage.controller.WhthinManageController', {
    extend: 'Ext.app.Controller',

    views: ['JyAdminsView', 'DzJyTreeView', 'DzJyGridView', 'LookBorrowdocMxGridView', 'LookBorrowdocMxView',
        'StJyGridView', 'StJyTreeView', 'XjDescAddView','DealDetailsGridView','DzPrintTreeView','LookBorrowDetailFormView',
    'DzPrintGridView','PrintEleDetailView','PrintEleView','FindNotFileFormView','AppraiseView'],//加载view
    stores: ['DzJyTreeStore', 'DzJyGridStore', 'LookBorrowdocMxGridStore', 'StJyGridStore', 'StJyTreeStore',
        'DealDetailsGridStore','DzPrintGridStore','PrintEleDetailGridStore'],//加载store
    models: ['DzJyTreeModel', 'DzJyGridModel', 'LookBorrowdocMxGridModel', 'StJyTreeModel', 'StJyGridModel',
        'DealDetailsGridModel','PrintEleDetailGridModel'],//加载model
    init: function (view) {
        var borrowcodeid;
        this.control({
            'dzJyTreeView': {
                render: function (view) {
                    view.getRootNode().on('expand', function (node) {
                        if (node.getOwnerTree().getSelectionModel().selected.length == 0) {
                            view.getSelectionModel().select(view.getRootNode().childNodes[0]);
                        }
                    })
                },
                select: function (treemodel, record) {
                    if (record.get('leaf')) {
                        var jyAdminsView = treemodel.view.findParentByType('jyAdminsView');
                        var dzJyGridView = jyAdminsView.down('[itemId=dzJyGridViewID]');
                        window.wdzJyGridView = dzJyGridView;
                        var treeview = jyAdminsView.down('[itemId=dzJyTreeViewID]');
                        var treetext = treeview.getSelectionModel().getSelected().items[0].get('text');
                        var buttons = dzJyGridView.down('toolbar').query('button');
                        var tbseparators = dzJyGridView.down('toolbar').query('tbseparator');
                        buttons[4].hide();
                        tbseparators[3].hide();
                        if ('已通过' == treetext) {
                            buttons[0].show();
                            tbseparators[0].show();
                            buttons[1].show();
                            tbseparators[1].show();
                            buttons[2].hide();
                            tbseparators[2].hide();
                            buttons[3].show();
                            buttons[4].show();
                            tbseparators[3].show();
                        }
                        else if('查无此档' == treetext){
                            buttons[0].show();
                            tbseparators[0].show();
                            buttons[1].hide();
                            tbseparators[1].hide();
                            buttons[2].show();
                            tbseparators[2].show();
                            buttons[3].show();
                        }
                        else {
                            buttons[0].show();
                            tbseparators[0].show();
                            buttons[2].hide();
                            tbseparators[2].hide();
                            buttons[3].show();
                            buttons[1].show();
                            tbseparators[1].show();

                        }
                        if(iflag==1){
                            dzJyGridView.down("[itemId=dzDealDetailsId]").hide();
                            dzJyGridView.down("[itemId=print]").hide();
                            tbseparators[1].hide();
                            tbseparators[0].hide();
                        }
                        window.wdzJyGridView.treeItem = treetext;
                        dzJyGridView.initGrid({state: treetext, type: '查档', flag: iflag});
                    }
                }
            },

            'stJyTreeView': {
                select: function (treemodel, record) {
                    if (record.get('leaf')) {
                        var jyAdminsView = treemodel.view.findParentByType('jyAdminsView');
                        var stJyGridView = jyAdminsView.down('[itemId=stJyGridViewID]');
                        window.wstJyGridView = stJyGridView;
                        var treeview = jyAdminsView.down('[itemId=stJyTreeViewID]');
                        var treetext = treeview.getSelectionModel().getSelected().items[0].get('text');
                        var buttons = stJyGridView.down('toolbar').query('button');
                        var tbseparators = stJyGridView.down('toolbar').query('tbseparator');
                        buttons[4].hide();
                        tbseparators[3].hide();
                        if ('已通过' == treetext) {
                            buttons[0].show();
                            tbseparators[0].show();
                            buttons[1].show();
                            tbseparators[1].show();
                            buttons[2].hide();
                            tbseparators[2].hide();
                            buttons[3].show();
                            buttons[4].show();
                            tbseparators[3].show();
                        }
                        else if('查无此档' == treetext){
                            buttons[0].show();
                            tbseparators[0].show();
                            buttons[1].hide();
                            tbseparators[1].hide();
                            buttons[2].show();
                            tbseparators[2].show();
                            buttons[3].show();
                        }
                        else {
                            buttons[0].show();
                            tbseparators[0].show();
                            buttons[1].hide();
                            tbseparators[1].hide();
                            buttons[2].hide();
                            tbseparators[2].hide();
                            buttons[3].show();

                        }
                        if(iflag==1){
                            stJyGridView.down('[itemId=stDealDetailsId]').hide();
                            stJyGridView.down('[itemId=print]').hide();
                            tbseparators[1].hide();
                            tbseparators[0].hide();
                        }
                        window.wstJyGridView.treeItem = treetext;
                        stJyGridView.initGrid({state: treetext, type: '实体查档', flag: iflag});
                    }
                }
            },

            'dzPrintTreeView': {
                render: function (view) {
                    // view.getRootNode().on('expand', function (node) {
                    //     if (node.getOwnerTree().getSelectionModel().selected.length == 0) {
                    //         node.getOwnerTree().getSelectionModel().select(node.firstChild);
                    //     }
                    // })
                    view.getSelectionModel().select(view.getRootNode().childNodes[0]);
                },
                select: function (treemodel, record) {
                    if (record.get('leaf')) {
                        var jyAdminsView = treemodel.view.findParentByType('jyAdminsView');
                        var dzPrintGridView = jyAdminsView.down('dzPrintGridView');
                        window.wdzPrintGridView = dzPrintGridView;
                        var treeview = jyAdminsView.down('dzPrintTreeView');
                        var treetext = treeview.getSelectionModel().getSelected().items[0].get('text');
                        var buttons = dzPrintGridView.down('toolbar').query('button');
                        var tbseparators = dzPrintGridView.down('toolbar').query('tbseparator');
                        buttons[4].hide();
                        tbseparators[3].hide();
                        if ('已通过' == treetext) {
                            buttons[0].show();
                            tbseparators[0].show();
                            buttons[1].show();
                            tbseparators[1].show();
                            buttons[2].hide();
                            tbseparators[2].hide();
                            buttons[3].show();
                            buttons[4].show();
                            tbseparators[3].show();
                        }
                        else if('查无此档' == treetext){
                            buttons[0].show();
                            tbseparators[0].show();
                            buttons[1].hide();
                            tbseparators[1].hide();
                            buttons[2].show();
                            tbseparators[2].show();
                            buttons[3].show();
                        }
                        else {
                            buttons[0].show();
                            tbseparators[0].show();
                            buttons[1].hide();
                            tbseparators[1].hide();
                            buttons[2].hide();
                            tbseparators[2].hide();
                            buttons[3].show();
                        }
                        if(iflag==1){
                            dzPrintGridView.down("[itemId=dzDealDetailsId]").hide();
                            dzPrintGridView.down("[itemId=print]").hide();
                            tbseparators[1].hide();
                            tbseparators[0].hide();
                        }
                        window.wdzPrintGridView.treeItem = treetext;
                        dzPrintGridView.initGrid({state: treetext, type: '电子打印', flag: iflag});
                    }
                }
            },

            "printEleDetailView": {
                render: function (view) {
                    if (iflag != 1) {
                        view.down("[itemId=print]").show();
                    }
                }
            },

            'DealDetailsGridView button[itemId=lookApproveId]': {//实体查档管理　查看单据批示
                click: function () {
                    var borrowdocid = window.borrowdocid;
                    var approve = Ext.create("Ext.window.Window", {
                        width: 370,
                        height: 210,
                        title: '查看批示',
                        modal: true,
                        closeToolText: '关闭',
                        items: [{
                            xtype: 'form',
                            defaults: {
                                layout: 'form',
                                xtype: 'container',
                                defaultType: 'textarea'
                            },
                            items: [{
                                itemId: 'approveId',
                                xtype: 'textarea',
                                name: 'approve',
                                margin: '15',
                                flex: 2,
                                readOnly: true,
                                width: 340,
                                height: 130
                            }]
                        }]
                    }).show();

                    approve.down('form').load({
                        url: '/jyAdmins/getApprove',
                        params: {borrowdocid: borrowdocid},
                        success: function (form, action) {
                        },
                        failure: function () {
                            XD.msg('操作失败');
                        }
                    });
                }
            },
            'stJyGridView button[itemId=stDealDetailsId]': {//实体查档管理,办理详情
                click: function () {
                    var select = window.wstJyGridView.getSelectionModel();
                    if (select.getSelected().length != 1) {
                        XD.msg('请选择一条数据');
                        return;
                    }
                    var borrowdocid = select.getSelected().items[0].get("id");
                    this.showDealDetailsWin(borrowdocid);
                }
            },

            'DealDetailsGridView button[itemId=lookApproveId]': {//电子查档管理　查看单据批示
                click: function (view) {
                    var borrowdocid = window.borrowdocid;
                    var approve = Ext.create('Ext.window.Window', {
                        width: 370,
                        height: 210,
                        title: '查看批示',
                        modal: true,
                        closeToolText: '关闭',
                        items: [{
                            xtype: 'form',
                            defaults: {
                                layout: 'form',
                                xtype: 'container',
                                defaultType: 'textarea'
                            },
                            items: [{
                                itemId: 'approveId',
                                xtype: 'textarea',
                                name: 'approve',
                                margin: '15',
                                flex: 2,
                                readOnly: true,
                                width: 340,
                                height: 130
                            }]
                        }]
                    }).show();

                    approve.down('form').load({
                        url: '/jyAdmins/getApprove',
                        params: {borrowdocid: borrowdocid},
                        success: function (form, action) {
                        },
                        failure: function () {
                            XD.msg('操作失败');
                        }
                    });
                }
            },
            'dzJyGridView button[itemId=dzDealDetailsId]': {//电子查档管理,办理详情
                click: function () {
                    var select = window.wdzJyGridView.getSelectionModel();
                    if (select.getSelected().length != 1) {
                        XD.msg('请选择一条数据');
                        return;
                    }
                    var borrowdocid = select.getSelected().items[0].get("id");
                    this.showDealDetailsWin(borrowdocid);
                }
            },

            'dzJyGridView button[itemId=lookBorrowMsgId]': {//电子查档管理　查看单据信息
                click: function () {
                    var select = window.wdzJyGridView.getSelectionModel();
                    if (select.getSelected().length != 1) {
                        XD.msg('请选择一条数据');
                        return;
                    }
                    var borrowdocid = select.getSelected().items[0].get("id");
                    borrowcodeid = select.getSelected().items[0].get("id");
                    var borrowcode = select.getSelected().items[0].get("borrowcode");
                    var mxView = Ext.create('WhthinManage.view.LookBorrowdocMxView');
                    var lookBorrowDetailFormView = mxView.down('lookBorrowDetailFormView');
                    lookBorrowDetailFormView.borrowcode = borrowcode;
                    lookBorrowDetailFormView.load({
                        url:'/electron/getBorrowdocById',
                        method:'GET',
                        params:{
                            borrowdocid: borrowdocid
                        },
                        success:function () {
                        },
                        failure:function () {
                            XD.msg('获取表单信息失败');
                        }
                    });
                    mxView.show();

                    window.mxView = mxView;
                    Ext.on('resize', function () {
                        window.mxView.setPosition(0, 0);
                        window.mxView.fitContainer();
                    });
                    var gridView = mxView.getComponent("lookBorrowdocMxGridViewId");
                    gridView.borrowdocid = borrowdocid;
                    gridView.down('[itemId=reItem]').hide();//隐藏未归还状态列
                    var buttons = mxView.down('toolbar').query('button');
                    buttons[1].hide();
                    gridView.initGrid({borrowdocid: borrowdocid});
                }
            },

            'dzJyGridView button[itemId=showFileId]': {//电子查档管理　出具查无此档证明
                click: function (btn) {
                    var select = btn.findParentByType('dzJyGridView').getSelectionModel();
                    if (select.getSelected().length != 1) {
                        XD.msg('请选择一条数据');
                        return;
                    }
                    var borrowId =select.getSelected().items[0].data.id;


                    var entryAddForm = Ext.create('WhthinManage.view.FindNotFileFormView',{
                        borrowId :borrowId
                    });
                    var form = entryAddForm.down('form');
                    form.load({
                        url: '/electron/getfileNoneData',
                        waitMsg: '正在加载数据',
                        params: {
                            docid:borrowId
                        },
                        success: function (form, action) {
                            var msg  =action.result.msg;
                            var data = action.result.data;
                            var formView =entryAddForm;
                            formView.setTitle("登记查无此档证明"+"( "+msg+" )");
                            if(msg == '已登记'){
                                var saveBtn = formView.down('[itemId = findNoneAddSubmit]');
                                var saveAndprintBtn = formView.down('[itemId = findNoneAddSubmitAndPrint]');
                                saveBtn.setText("修改");
                                saveAndprintBtn.setText("修改并打印");
                                if(data.mandie == 1){
                                    formView.down('[itemId = manradioId]')
                                }
                                if(data.womandie == 1){
                                    var womanradio =  formView.down('[itemId = womanradioId]');
                                    womanradio.setValue('1');
                                }
                            }
                        },
                        failure: function (form, action) {
                            XD.msg('操作失败');
                        }
                    });
                    var fields = form.getForm().getFields().items;
                    entryAddForm.show();
                }
            },


            'stJyGridView button[itemId=showFileId]': {//实体查档管理　出具查无此档证明
                click: function (btn) {
                    var select = btn.findParentByType('stJyGridView').getSelectionModel();
                    if (select.getSelected().length != 1) {
                        XD.msg('请选择一条数据');
                        return;
                    }
                    var borrowId =select.getSelected().items[0].data.id;


                    var entryAddForm = Ext.create('WhthinManage.view.FindNotFileFormView',{
                        borrowId :borrowId
                    });
                    var form = entryAddForm.down('form');
                    form.load({
                        url: '/electron/getfileNoneData',
                        waitMsg: '正在加载数据',
                        params: {
                            docid:borrowId
                        },
                        success: function (form, action) {
                            var msg  =action.result.msg;
                            var data = action.result.data;
                            var formView =entryAddForm;
                            formView.setTitle("登记查无此档证明"+"( "+msg+" )");
                            if(msg == '已登记'){
                                var saveBtn = formView.down('[itemId = findNoneAddSubmit]');
                                var saveAndprintBtn = formView.down('[itemId = findNoneAddSubmitAndPrint]');
                                saveBtn.setText("修改");
                                saveAndprintBtn.setText("修改并打印");
                                if(data.mandie == 1){
                                    formView.down('[itemId = manradioId]')
                                }
                                if(data.womandie == 1){
                                    var womanradio =  formView.down('[itemId = womanradioId]')
                                }
                            }
                        },
                        failure: function (form, action) {
                            XD.msg('操作失败');
                        }
                    });
                    var fields = form.getForm().getFields().items;
                    entryAddForm.show();
                }
            },


            'FindNotFileFormView button[itemId = findNoneAddSubmit]':{ //查无此档表单保存/修改
              click:function (btn) {
                  var formView = btn.findParentByType('FindNotFileFormView');
                  var form = formView.down('form');
                  var data = form.getValues();

                  // if(data['text']==''||data['desci']==''){
                  //     XD.msg('有必填项未填写');
                  //     return;
                  // }
                  var man=form.down('[name = manname]').value;
                  var woman=form.down('[name = womanname]').value;
                  if(!man&&!woman){
                      XD.msg('单人证明至少填写一方信息！');
                      return;
                  }
                  if(man&&form.down('[itemId=manradioId]').getChecked()[0].inputValue==0&&!form.down('[name = mancard]').value){
                      XD.msg('男方身份证未填写！');
                      return;
                  }
                  if(woman&&form.down('[itemId=womanradioId]').getChecked()[0].inputValue==0&&!form.down('[name = womancard]').value){
                      XD.msg('女方身份证未填写！');
                      return;
                  }
                  if (!form.isValid()) {
                      XD.msg('填写存在错误项，请修改后再提交');
                      return;
                  }
                  form.submit({
                      url: '/electron/savafileNoneData',
                      params:{
                          mandie:data.manradio,
                          womandie:data.womanradio
                      },
                      method: 'POST',
                      success: function () {
                          XD.msg('操作成功');
                          formView.close();
                      }, failure: function () {
                          XD.msg('操作失败');
                      }
                  });
              }
            },

            'FindNotFileFormView button[itemId = findNoneAddSubmitAndPrint]':{//查无此档表单保存并打印
              click:function (btn) {
                  var formView = btn.findParentByType('FindNotFileFormView');
                  var form = formView.down('form');
                  var data = form.getValues();
                  var filenoneid = [];
                  var params = {};
                  // if(data['text']==''||data['desci']==''){
                  //     XD.msg('有必填项未填写');
                  //     return;
                  // }
                  var man=form.down('[name = manname]').value;
                  var woman=form.down('[name = womanname]').value;
                  if(!man&&!woman){
                      XD.msg('单人证明至少填写一方信息！');
                      return;
                  }
                  if(man&&form.down('[itemId=manradioId]').getChecked()[0].inputValue==0&&!form.down('[name = mancard]').value){
                      XD.msg('男方身份证未填写！');
                      return;
                  }
                  if(woman&&form.down('[itemId=womanradioId]').getChecked()[0].inputValue==0&&!form.down('[name = womancard]').value){
                      XD.msg('女方身份证未填写！');
                      return;
                  }
                  if (!form.isValid()) {
                      XD.msg('填写存在错误项，请修改后再提交');
                      return;
                  }
                  form.submit({
                      url: '/electron/savafileNoneData',
                      params:{
                          mandie:data.manradio,
                          womandie:data.womanradio
                      },
                      method: 'POST',
                      success: function () {
                          XD.msg('操作成功,开始打印！');
                          formView.close();
                          //（判断需要打印那一份）打印开始
                          if(data.organname == null || data.organname == ""){
                              if(data.manname != null && data.manname !='' && data.womanname != null && data.womanname != '') {
                                  filenoneid.push(data.filenoneid)
                                  params['filenoneid'] = filenoneid.join(",");
                                  XD.UReportPrint("婚姻登记证明模板","婚姻登记证明模板（双人)",params);
                              } else{
                                  filenoneid.push(data.filenoneid)
                                  params['filenoneid'] = filenoneid.join(",");
                                  XD.UReportPrint("婚姻登记证明模板","婚姻登记证明模板（单人)",params);
                              }
                          }
                          else{
                              if(data.manname != null && data.manname !='' && data.womanname != null && data.womanname != '') {
                                  filenoneid.push(data.filenoneid)
                                  params['filenoneid'] = filenoneid.join(",");
                                  XD.UReportPrint("婚姻登记证明模板","婚姻登记证明模板（单位双人)",params);

                              } else{
                                  filenoneid.push(data.filenoneid)
                                  params['filenoneid'] = filenoneid.join(",");
                                  XD.UReportPrint("婚姻登记证明模板","婚姻登记证明模板（单位单人)",params);
                              }
                          }
                      }, failure: function () {
                          XD.msg('操作失败');
                      }
                  });
              }
            },

            'FindNotFileFormView button[itemId = print]':{//查无此档打印
                click:function (btn) {
                    var formView = btn.findParentByType('FindNotFileFormView');
                    var form = formView.down('form');
                    var data = form.getValues();
                    var filenoneid = [];
                    var params = {};

                    //判断表单是否修改过或者表单或者初始创建
                    if(data.filenoneid == null || data.filenoneid == ''||form.getForm().isDirty() == true)
                    {
                        XD.msg("请先保存表单在进行打印操作！")
                    }
                    else{
                        if(data.organname == null || data.organname == ""){
                            if(data.manname != null && data.manname !='' && data.womanname != null && data.womanname != '') {
                                filenoneid.push(data.filenoneid)
                                params['filenoneid'] = filenoneid.join(",");
                                XD.UReportPrint("婚姻登记证明模板","婚姻登记证明模板（双人)",params);
                            }
                            else{
                                filenoneid.push(data.filenoneid)
                                params['filenoneid'] = filenoneid.join(",");
                                XD.UReportPrint("婚姻登记证明模板","婚姻登记证明模板（单人)",params);
                            }
                        } else{
                            if((data.manname != null && data.manname !='' && data.womanname != null && data.womanname != '')) {
                                filenoneid.push(data.filenoneid)
                                params['filenoneid'] = filenoneid.join(",");
                                XD.UReportPrint("婚姻登记证明模板","婚姻登记证明模板（单位双人)",params);

                            } else{
                                filenoneid.push(data.filenoneid)
                                params['filenoneid'] = filenoneid.join(",");
                                XD.UReportPrint("婚姻登记证明模板","婚姻登记证明模板（单位单人)",params);
                            }
                        }
                    }
                }
            },


            'stJyGridView button[itemId=lookBorrowMsgId]': {//实体查档管理　查看单据信息
                click: function () {
                    var select = window.wstJyGridView.getSelectionModel();
                    if (select.getSelected().length != 1) {
                        XD.msg('请选择一条数据');
                        return;
                    }
                    var borrowdocid = select.getSelected().items[0].get("id");
                    var mxView = Ext.create('WhthinManage.view.LookBorrowdocMxView');
                    window.mxView = mxView;
                    Ext.on('resize', function () {
                        window.mxView.setPosition(0, 0);
                        window.mxView.fitContainer();
                    });
                    mxView.show();
                    var gridView = mxView.getComponent('lookBorrowdocMxGridViewId');
                    gridView.borrowdocid = borrowdocid;
                    var buttons = mxView.down('toolbar').query('button');
                    buttons[0].hide();
                    if (iflag === '1') {
                        buttons[1].hide();
                    }
                    window.wstJyGridView.borrowdocid = borrowdocid;
                    gridView.initGrid({borrowdocid: borrowdocid});
                }
            },

            'dzPrintGridView button[itemId=lookBorrowMsgId]': {//电子打印管理　查看单据信息
                click: function (btn) {
                    var select = btn.findParentByType('dzPrintGridView').getSelectionModel();
                    if (select.getSelected().length != 1) {
                        XD.msg('请选择一条数据');
                        return;
                    }
                    var borrowdocid = select.getSelected().items[0].get("id");
                    borrowcodeid = select.getSelected().items[0].get("id");
                    var borrowcode = select.getSelected().items[0].get("borrowcode");
                    var mxView = Ext.create('WhthinManage.view.LookBorrowdocMxView');
                    var lookBorrowdocMxGridView = mxView.down('lookBorrowdocMxGridView');
                    lookBorrowdocMxGridView.borrowcode = borrowcode;
                    mxView.show();

                    window.mxView = mxView;
                    Ext.on('resize', function () {
                        window.mxView.setPosition(0, 0);
                        window.mxView.fitContainer();
                    });
                    var gridView = mxView.getComponent("lookBorrowdocMxGridViewId");
                    gridView.borrowdocid = borrowdocid;
                    gridView.down('[itemId=reItem]').hide();//隐藏未归还状态列
                    var buttons = mxView.down('toolbar').query('button');
                    buttons[0].hide();
                    buttons[1].hide();
                    buttons[2].show();
                    if(iflag!=1){  //利用平台不显示打印按钮
                        buttons[3].show();
                    }
                    gridView.initGrid({borrowdocid: borrowdocid});
                }
            },
            'dzJyGridView button[itemId=print]': {//电子查档管理 打印单据清册
                click:function () {
                    var ids = [];
                    var params = {};
                    Ext.each(window.wdzJyGridView.getSelectionModel().getSelection(),function(){
                        ids.push(this.get('id').trim());
                    });
                    if(reportServer == 'UReport') {
                        params['docid'] = ids.join(",");
                        XD.UReportPrint(null, '申请管理', params);
                    }
                    else if(reportServer == 'FReport') {
                        XD.FRprint(null, '申请管理', ids.length > 0 ? "'id':'" + ids.join(",") + "'" : '');
                    }
                }
            },
            'stJyGridView button[itemId=print]': {//实体查档管理 打印单据清册
                click:function () {
                    var ids = [];
                    var params = {};
                    Ext.each(window.wstJyGridView.getSelectionModel().getSelection(),function(){
                        ids.push(this.get('id').trim());
                    });
                    if(reportServer == 'UReport') {
                        params['docid'] = ids.join(",");
                        XD.UReportPrint(null, '申请管理', params);
                    }
                    else if(reportServer == 'FReport') {
                        XD.FRprint(null, '申请管理', ids.length > 0 ? "'id':'" + ids.join(",") + "'" : '');
                    }
                }
            },

            'dzPrintGridView button[itemId=print]': {//电子打印管理 打印单据清册
                click:function (btn) {
                    var ids = [];
                    var params = {};
                    Ext.each(btn.findParentByType('dzPrintGridView').getSelectionModel().getSelection(),function(){
                        ids.push(this.get('id').trim());
                    });
                    if(reportServer == 'UReport') {
                        params['docid'] = ids.join(",");
                        XD.UReportPrint(null, '申请管理', params);
                    }
                    else if(reportServer == 'FReport') {
                        XD.FRprint(null, '申请管理', ids.length > 0 ? "'id':'" + ids.join(",") + "'" : '');
                    }
                }
            },

            'lookBorrowdocMxGridView button[itemId=lookMedia]': {
                click: function (btn) {
                    var select = btn.findParentByType('lookBorrowdocMxGridView').getSelectionModel();
                    if (select.getSelected().length != 1) {
                        XD.msg('请选择一条数据');
                        return;
                    }
                    //判断是否已到期
                    if(select.getSelected().items[0].get('responsible') < getDateStr(0)){
                        XD.msg('该查档已到期，不允许查看原文！');
                        return;
                    }
                    if(select.getSelected().items[0].get('lyqx')==='拒绝'){
                        XD.msg('该数据未审批通过，不允许查看原文！');
                        return;
                    }
                    if(select.getSelected().items[0].get('type')!='电子查档'&&select.getSelected().items[0].get('type')!='电子、实体查档'){
                        XD.msg('请选择电子查档进行查看原文！');
                        return;
                    }
                    var entryid = select.getSelected().items[0].get("entryid");
                    var eleids = [];
                    eleids = this.getEleids(entryid,borrowcodeid);
                    if(eleids.length==0){
                        XD.msg('没有可以查看的原文');
                        return;
                    }

                    var media = Ext.create("Ext.window.Window", {
                        width: '100%',
                        height: '100%',
                        title: '查看原文',
                        modal: true,
                        closeToolText: '关闭',
                        layout: 'fit',
                        items: [{
                            xtype: 'solid',
                            entrytype: 'solid'
                        }]
                    });
                    //初始化原文数据
                    var solidview = media.down('solid');
                    solidview.isJy = false;
                    // solidview.initData(entryid);
                    solidview.entryid = entryid;
                    if(iflag==1){
                        solidview.winType='lyjylook';
                    }else{
                        solidview.winType='gljylook';
                    }
                    window.remainEleids = eleids;
                    var treeStore = solidview.down('treepanel').getStore();
                    if(typeof(entryid) == 'undefined'){
                        solidview.down('treepanel').getRootNode().removeAll();
                        return;
                    }
                    solidview.unionAll = true;
                    treeStore.reload();

                    for (var i = 0; i < solidview.down('toolbar').query('button').length; i++) {
                        if(solidview.down('toolbar').query('button')[i].itemId == 'print'&&iflag!=1){
                            continue;
                        }
                        solidview.down('toolbar').query('button')[i].hide();
                    }
                    this.getELetopBtn(solidview.down('toolbar').query('button'),'look');
                    media.show();
                    window.media = media;
                    Ext.on('resize', function () {
                        window.media.setPosition(0, 0);
                        window.media.fitContainer();
                    });
                }
            },
            'lookBorrowdocMxGridView button[itemId=hide]': {
                click: function (btn) {
                    var lookBorrowdocMxView = btn.findParentByType('lookBorrowdocMxView');
                    var lookBorrowdocMxGridView = btn.findParentByType("lookBorrowdocMxGridView");
                    var borrowdocid = lookBorrowdocMxGridView.borrowdocid;
                    lookBorrowdocMxView.close();
                    Ext.Ajax.request({
                        params: {
                            borrowdocid:borrowdocid
                        },
                        url: '/jyAdmins/getAppraise',
                        method: 'POST',
                        sync:false,
                        success: function (resp) {
                            var resptext = Ext.decode(resp.responseText);
                            var html;
                            if(!resptext.success){
                                html =
                                    "<span>"
                                    +"<img id='starone' src='../img/star2.png' height='40' width='40'  alt='yes'> "
                                    +"<img id='startwo' src='../img/star2.png' height='40' width='40'  alt='yes'> "
                                    +"<img id='starthree' src='../img/star2.png' height='40' width='40'  alt='yes'> "
                                    +"<img id='starfour' src='../img/star2.png' height='40' width='40'  alt='yes'> "
                                    +"<img id='starfive' src='../img/star2.png' height='40' width='40'  alt='yes'> "
                                    +"</span>";
                                var appraiseView = Ext.create("WhthinManage.view.AppraiseView");
                                var setAppraise = appraiseView.down('[itemId=setAppraiseId]');
                                setAppraise.setHtml(html);
                                appraiseView.borrowdocid = borrowdocid;
                                appraiseView.show();
                            }
                        },
                        failure: function () {
                            XD.msg('操作失败');
                        }
                    });
                }
            },

            'lookBorrowdocMxGridView button[itemId=renew]': {
                click: function (btn) {
                    var select = btn.findParentByType('lookBorrowdocMxGridView').getSelectionModel();
                    var entrys = select.selected.items;
                    var selectCount = entrys.length;
                    if (selectCount == 0) {
                        XD.msg('请选择数据');
                        return;
                    }
                    if (selectCount > 1) {
                        XD.msg('只能选择一条数据');
                        return;
                    }
                    var entryIds = [];
                    var state = false;
                    for (var i = 0; i < entrys.length; i++) {
                        entryIds.push(entrys[i].get('entrystorage'));
                        if(entrys[i].get('pages')=='已归还'||!entrys[i].get('pages')||entrys[i].get('pages')==''){
                            state = true;
                        }
                    }

                    if(state){
                        XD.msg('请选择“未归还”的记录进行续借');
                        return;
                    }

                    window.wstJyGridView.entryids = entryIds;
                    window.wstJyGridView.stlookgrid = btn.findParentByType('lookBorrowdocMxGridView');
                    Ext.create("WhthinManage.view.XjDescAddView").show();
                }
            },

            'xjDescAddView button[itemId=xjAddSubmit]': {
                click: function (btn) {
                    var form = btn.findParentByType('xjDescAddView').down('form');
                    var ts = form.down('textfield').value;
                    var desci = form.down('textarea').value;

                    if (isNaN(ts) || parseInt(ts) < 1) {
                        XD.msg('请输入正确格式');
                        return;
                    }

                    Ext.Ajax.request({
                        params: {
                            borrowid: window.wstJyGridView.borrowdocid,
                            ids: window.wstJyGridView.entryids,
                            ts: ts,
                            xjapprove: desci+'\n'+'续借天数：'+ts+'\t'+'续借日期：'+getNowFormatDate(),
                            flag:true
                        },
                        url: '/electron/stXjAddFormBill',
                        method: 'POST',
                        sync: true,
                        success: function () {
                            XD.msg('续借成功');
                            window.wstJyGridView.stlookgrid.initGrid();
                            btn.findParentByType('xjDescAddView').close();
                            //parent.parent.closeObj.close();//刷新通知栏
                        },
                        failure: function () {
                            XD.msg('操作失败');
                        }
                    });
                }
            },

            'xjDescAddView button[itemId=xjAddClose]': {
                click: function (btn) {
                    btn.findParentByType('xjDescAddView').close();
                }
            },
            'jyAdminsView': {
                render: function (view) {
                    view.down('[itemId=stJyTreeViewID]').on('render', function (view) {
                        view.getSelectionModel().select(view.getRootNode().childNodes[0]);//默认选中第一个
                    });
                }
            },

            'dzPrintGridView button[itemId=dzDealDetailsId]': {//电子打印管理,办理详情
                click: function (btn) {
                    var select = btn.findParentByType('dzPrintGridView').getSelectionModel();
                    if (select.getSelected().length != 1) {
                        XD.msg('请选择一条数据');
                        return;
                    }
                    var borrowdocid = select.getSelected().items[0].get("id");
                    this.showDealDetailsWin(borrowdocid);
                }
            },

            'lookBorrowdocMxGridView button[itemId=lookEle]': {
                click: function (btn) {
                    var lookBorrowdocMxGridView = btn.findParentByType('lookBorrowdocMxGridView');
                    var select = lookBorrowdocMxGridView.getSelectionModel().getSelection();
                    if (select.length < 1) {
                        XD.msg('请至少选择一条数据');
                        return;
                    }
                    var entryid = select[0].get("entryid");
                    var entryids = [];
                    var borrowcode = lookBorrowdocMxGridView.borrowcode;
                    for(var i=0;i<select.length;i++){
                        //判断是否已到期
                        if(select[i].get('responsible') < getDateStr(0)){
                            XD.msg('存在打印申请已到期，不允许打印原文！');
                            return;
                        }
                        if(select[i].get('lyqx')==='拒绝'){
                            XD.msg('存在数据未审批通过，不允许打印原文！');
                            return;
                        }
                        entryids.push(select[i].get("entryid"));
                    }
                    var iframe = document.getElementById("mediaFramePrint");
                    if(iframe){
                        iframe.parentNode.removeChild(iframe);
                    }
                    var printEleView = Ext.create("WhthinManage.view.PrintEleView");
                    var form = printEleView.down('printEleDetailView');
                    form.entryid = entryid;
                    form.entryids = entryids;
                    form.borrowcode = borrowcode;
                    form.type = "allPass";
                    printEleView.show();
                    this.getApplySetPrint(form);
                }
            },

            'lookBorrowdocMxGridView button[itemId=printEle]': {
                click: function (btn) {
                    var lookBorrowdocMxGridView = btn.findParentByType('lookBorrowdocMxGridView');
                    var select = lookBorrowdocMxGridView.getSelectionModel().getSelection();
                    if (select.length < 1) {
                        XD.msg('请至少选择一条数据');
                        return;
                    }
                    var entryid = select[0].get("id");
                    var entryids = [];
                    var borrowcode = lookBorrowdocMxGridView.borrowcode;
                    for(var i=0;i<select.length;i++){
                        //判断是否已到期
                        if(select[i].get('responsible') < getDateStr(0)){
                            XD.msg('存在打印申请已到期，不允许打印原文！');
                            return;
                        }
                        if(select[i].get('lyqx')==='拒绝'){
                            XD.msg('存在数据未审批通过，不允许打印原文！');
                            return;
                        }
                        entryids.push(select[i].get("id"));
                    }
                    var iframe = document.getElementById("mediaFramePrint");
                    if(iframe){
                        iframe.parentNode.removeChild(iframe);
                    }
                    var printEleView = Ext.create("WhthinManage.view.PrintEleView");
                    var form = printEleView.down('printEleDetailView');
                    form.entryid = entryid;
                    form.entryids = entryids;
                    form.borrowcode = borrowcode;
                    form.type = "nolyPass";
                    printEleView.show();
                    this.getApplySetPrint(form);
                }
            },

            'printEleView [itemId=preBtn]':{
                click:this.preApplySetPrint
            },

            'printEleView [itemId=nextBtn]':{
                click:this.nextApplySetPrint
            },

            'printEleView [itemId=back]':{
                click:function (view) {
                   view.findParentByType('window').close();
                }
            },

            'printEleDetailView [itemId=print]':{
                click:function (view) {
                    var printEleDetailView = view.findParentByType('printEleDetailView');
                    var eleGrid = printEleDetailView.down('[itemId=eleGrid]');
                    var select = eleGrid.getSelectionModel().getSelection();
                    if(select.length!=1){
                        XD.msg('只能选择一条数据');
                        return;
                    }
                    if(select[0].get('pass')!="通过"){
                        XD.msg('当前电子文件不通过审批或者未申请');
                        return;
                    }
                    var eleid = select[0].get('eleid');
                    var filename = select[0].get('filename');
                    //判断iframe是否存在，不存在则创建iframe
                    iframe = document.getElementById("print-iframe");
                    if (!iframe) {
                        var iframe = document.createElement('IFRAME');
                        var doc = null;
                        iframe.setAttribute("id", "print-iframe");
                        iframe.setAttribute('style', 'position:absolute;width:0px;height:0px;left:-500px;top:-500px;');
                        iframe.setAttribute('src', '/electronic/mediaPrint?entrytype=' + 'solid' + '&eleid=' + eleid + '&filetype=' + filename.substring(filename.lastIndexOf('.') + 1));
                        document.body.appendChild(iframe);
                        doc = iframe.contentWindow.document;
                        doc.close();
                        iframe.contentWindow.focus();
                        iframe.onload=function () {
                            iframe.contentWindow.print();
                        };
                    } else {
                        var iframe = document.getElementById("print-iframe");
                        iframe.setAttribute('src', '/electronic/mediaPrint?entrytype=' + 'solid' + '&eleid=' + eleid + '&filetype=' + filename.substring(filename.lastIndexOf('.') + 1));
                        var doc = iframe.contentWindow.document;
                        doc.close();
                        iframe.contentWindow.focus();
                        iframe.onload=function () {
                            iframe.contentWindow.print();
                        };
                    }
                }
            },
            "dzJyGridView button[itemId=appraise],dzPrintGridView button[itemId=appraise],stJyGridView button[itemId=appraise]":{
                click:function (view) {
                    var gridView;
                    if(view.findParentByType("dzJyGridView")){
                        gridView = view.findParentByType("dzJyGridView");
                    }else if(view.findParentByType("dzPrintGridView")){
                        gridView = view.findParentByType("dzPrintGridView");
                    }else{
                        gridView = view.findParentByType("stJyGridView");
                    }
                    var select = gridView.getSelectionModel().getSelection();
                    if(select.length!=1){
                        XD.msg("只能选择一条数据");
                        return;
                    }
                    var borrowdocid = select[0].get("id");
                    Ext.Ajax.request({
                        params: {
                            borrowdocid:borrowdocid
                        },
                        url: '/jyAdmins/getAppraise',
                        method: 'POST',
                        sync:false,
                        success: function (resp) {
                            var resptext = Ext.decode(resp.responseText);
                            var html;
                            if(resptext.success){
                                var labeltext = resptext.data.appraise;
                                var text;
                                    if(labeltext.indexOf("无可挑剔")!=-1){
                                        html =
                                            "<span>"
                                            +"<img id='starone' src='../img/star2.png' height='40' width='40'  alt='yes'> "
                                            +"<img id='startwo' src='../img/star2.png' height='40' width='40'  alt='yes'> "
                                            +"<img id='starthree' src='../img/star2.png' height='40' width='40'  alt='yes'> "
                                            +"<img id='starfour' src='../img/star2.png' height='40' width='40'  alt='yes'> "
                                            +"<img id='starfive' src='../img/star2.png' height='40' width='40'  alt='yes'> "
                                            +"</span>";
                                        text = "5-无可挑剔";
                                    }else if(labeltext.indexOf("非常满意")!=-1){
                                        html =
                                            "<span>"
                                            +"<img id='starone' src='../img/star2.png' height='40' width='40'  alt='yes'> "
                                            +"<img id='startwo' src='../img/star2.png' height='40' width='40'  alt='yes'> "
                                            +"<img id='starthree' src='../img/star2.png' height='40' width='40'  alt='yes'> "
                                            +"<img id='starfour' src='../img/star2.png' height='40' width='40'  alt='yes'> "
                                            +"<img id='starfive' src='../img/star1.png' height='40' width='40'  alt='no'> "
                                            +"</span>";
                                        text = "4-非常满意";
                                    }else if(labeltext.indexOf("满意")!=-1){
                                        html =
                                            "<span>"
                                            +"<img id='starone' src='../img/star2.png' height='40' width='40'  alt='yes'> "
                                            +"<img id='startwo' src='../img/star2.png' height='40' width='40'  alt='yes'> "
                                            +"<img id='starthree' src='../img/star2.png' height='40' width='40'  alt='yes'> "
                                            +"<img id='starfour' src='../img/star1.png' height='40' width='40'  alt='no'> "
                                            +"<img id='starfive' src='../img/star1.png' height='40' width='40'  alt='no'> "
                                            +"</span>";
                                        text = "3-满意";
                                    }else if(labeltext.indexOf("一般")!=-1){
                                        html =
                                            "<span>"
                                            +"<img id='starone' src='../img/star2.png' height='40' width='40'  alt='yes'> "
                                            +"<img id='startwo' src='../img/star2.png' height='40' width='40'  alt='yes'> "
                                            +"<img id='starthree' src='../img/star1.png' height='40' width='40'  alt='no'> "
                                            +"<img id='starfour' src='../img/star1.png' height='40' width='40'  alt='no'> "
                                            +"<img id='starfive' src='../img/star1.png' height='40' width='40'  alt='no'> "
                                            +"</span>";
                                        text = "2-一般";
                                    }else if(labeltext.indexOf("很差")!=-1){
                                        html =
                                            "<span>"
                                            +"<img id='starone' src='../img/star2.png' height='40' width='40'  alt='yes'> "
                                            +"<img id='startwo' src='../img/star1.png' height='40' width='40'  alt='no'> "
                                            +"<img id='starthree' src='../img/star1.png' height='40' width='40'  alt='no'> "
                                            +"<img id='starfour' src='../img/star1.png' height='40' width='40'  alt='no'> "
                                            +"<img id='starfive' src='../img/star1.png' height='40' width='40'  alt='no'> "
                                            +"</span>";
                                        text = "1-很差";
                                    }
                            }else{
                                html =
                                    "<span>"
                                    +"<img id='starone' src='../img/star2.png' height='40' width='40'  alt='yes'> "
                                    +"<img id='startwo' src='../img/star2.png' height='40' width='40'  alt='yes'> "
                                    +"<img id='starthree' src='../img/star2.png' height='40' width='40'  alt='yes'> "
                                    +"<img id='starfour' src='../img/star2.png' height='40' width='40'  alt='yes'> "
                                    +"<img id='starfive' src='../img/star2.png' height='40' width='40'  alt='yes'> "
                                    +"</span>";
                            }
                            var appraiseView = Ext.create("WhthinManage.view.AppraiseView");
                            var setAppraise = appraiseView.down('[itemId=setAppraiseId]');
                            setAppraise.setHtml(html);
                            if(resptext.data){
                                appraiseView.down("[itemId=contentId]").setValue(resptext.data.content);
                                appraiseView.down("[itemId=labelId]").setText(text);
                                if(iflag==1){
                                    appraiseView.down("[itemId=setAppraiseSubmit]").hide();
                                }else{
                                    appraiseView.down("[itemId=setAppraiseSubmit]").setText("修改");
                                }
                            }
                            appraiseView.borrowdocid = borrowdocid;
                            appraiseView.show();
                        },
                        failure: function () {
                            XD.msg('操作失败');
                        }
                    });
                }
            },
            "appraiseView [itemId=setAppraiseSubmit]":{
                click:function (view) {
                    var appraiseView = view.findParentByType('appraiseView');
                    var labeltext = appraiseView.down('[itemId=labelId]').text;
                    var content = appraiseView.down('[itemId=contentId]').getValue();
                    if(!content){
                        XD.msg("请输入反馈意见");
                        return;
                    }
                    if(labeltext.indexOf("5-无可挑剔")!=-1){
                        labeltext = "无可挑剔";
                    }else if(labeltext.indexOf("4-非常满意")!=-1){
                        labeltext = "非常满意";
                    }else if(labeltext.indexOf("3-满意")!=-1){
                        labeltext = "满意";
                    }else if(labeltext.indexOf("2-一般")!=-1){
                        labeltext = "一般";
                    }else if(labeltext.indexOf("1-很差")!=-1){
                        labeltext = "很差";
                    }
                    Ext.Ajax.request({
                        params: {
                            borrowdocid: appraiseView.borrowdocid,
                            labeltext: labeltext,
                            content:content
                        },
                        url: '/jyAdmins/setAppraise',
                        method: 'POST',
                        success: function () {
                            XD.msg('评分成功');
                            appraiseView.close();
                        },
                        failure: function () {
                            XD.msg('操作失败');
                        }
                    });
                }
            },
            "appraiseView [itemId=setAppraiseClose]": {
                click:function (view) {
                    view.findParentByType("window").close();
                }
            }
        });
    },
    getELetopBtn:function (btns,operate) {
        var btnFunctionList = [];
        Ext.Ajax.request({
            url: '/user/getWJQXbtn',
            async:false,
            success: function (response) {
                var  result = Ext.decode(response.responseText);
                btnFunctionList =result.data;
            },
            failure: function (form, action) {
                XD.msg('获取电子文件按钮权限失败！');
            }
        });

        for(var i = 0;i<btns.length;i++) {
            var btn = btns[i];

            if (btnFunctionList.indexOf(btn.text) != -1) {
                btn.show();
            }
            else {
                btn.hide();
            }

            if (operate == 'look' || operate == 'lookfile') {
                if (btn.text == '删除' || btn.text == '上传' || btn.text == '上移' || btn.text == '下移' || btn.text == '查看历史版本' || btn.text == '重命名') {
                    btn.hide();
                }
            }
            if (operate == 'add') {
                if (btn.text == '查看历史版本') {
                    btn.hide();
                }
            }
        }
    },
    showDealDetailsWin:function(borrowdocid){
        var dealDetailsWin = Ext.create('Ext.window.Window',{
            modal:true,
            width:1000,
            height:530,
            title:'办理详情',
            layout:'fit',
            closeToolText:'关闭',
            closeAction:'hide',
            items:[{
                xtype: 'DealDetailsGridView'
            }]
        });
        var store = dealDetailsWin.down('DealDetailsGridView').getStore();
        store.proxy.extraParams.borrowdocid = borrowdocid;
        window.borrowdocid = borrowdocid;
        store.reload();
        dealDetailsWin.show();
    },
    getEleids:function(entryid,borrowcodeid){
        var eleids=[];
        Ext.Ajax.request({
            url:'/electronic/geteleids',
            async:false,
            params:{
                entryid:entryid,
                borrowcodeid:borrowcodeid
            },
            success:function (response) {
                eleids = Ext.decode(response.responseText);

            }
        });
        return eleids;
    },

    //点击上一条打印电子文件
    preApplySetPrint:function(btn){
        var applySetPrintView = btn.findParentByType('printEleView');
        var form = applySetPrintView.down('printEleDetailView');
        this.refreshApplySetPrint(form, 'pre');
    },

    //点击下一条打印电子文件
    nextApplySetPrint:function(btn){
        var applySetPrintView = btn.findParentByType('printEleView');
        var form = applySetPrintView.down('printEleDetailView');
        this.refreshApplySetPrint(form, 'next');
    },

    refreshApplySetPrint:function(form, type){
        var entryids = form.entryids;
        var currentEntryid = form.entryid;
        var entryid;
        for(var i=0;i<entryids.length;i++){
            if(type == 'pre' && entryids[i] == currentEntryid){
                if(i==0){
                    i=entryids.length;
                }
                entryid = entryids[i-1];
                break;
            }else if(type == 'next' && entryids[i] == currentEntryid){
                if(i==entryids.length-1){
                    i=-1;
                }
                entryid = entryids[i+1];
                break;
            }
        }
        form.entryid = entryid;
        this.getApplySetPrint(form);
    },

    getApplySetPrint:function (form) {
        var count;
        var entryid = form.entryid;
        for (var i = 0; i < form.entryids.length; i++) {
            if (form.entryids[i] == entryid) {
                count = i + 1;
                break;
            }
        }
        var total = form.entryids.length;
        var applySetPrintView = form.findParentByType('printEleView');
        var totaltext = applySetPrintView.down('[itemId=totalText]');
        totaltext.setText('当前共有  ' + total + '  条，');
        var nowtext = applySetPrintView.down('[itemId=nowText]');
        nowtext.setText('当前记录是第  ' + count + '  条');
        var eleGrid = form.down('[itemId=eleGrid]');
        eleGrid.getStore().proxy.extraParams.entryid = form.entryid;
        eleGrid.getStore().proxy.extraParams.borrowcode = form.borrowcode;
        eleGrid.getStore().proxy.extraParams.type = form.type;
        eleGrid.getStore().reload();
        var allMediaFrame = document.querySelectorAll('#mediaFramePrint');
        var mediaFrame=allMediaFrame[0];
        mediaFrame.setAttribute('src','');
    }
});
function getDateStr(AddDayCount) {
    var dd = new Date();
    dd.setDate(dd.getDate()+AddDayCount);//获取AddDayCount天后的日期
    var y = dd.getFullYear();
    var m = dd.getMonth()+1;//获取当前月份的日期
    var d = dd.getDate();
    if (m >= 1 && m <= 9) {
        m = "0" + m;
    }
    if (d >= 0 && d <= 9) {
        d = "0" + d;
    }
    return y+""+m+""+d;
}

function getNowFormatDate() {
    var date = new Date();
    var seperator1 = "";
    var seperator2 = ":";
    var month = date.getMonth() + 1;
    var strDate = date.getDate();
    if (month >= 1 && month <= 9) {
        month = "0" + month;
    }
    if (strDate >= 0 && strDate <= 9) {
        strDate = "0" + strDate;
    }
    var currentdate = date.getFullYear() + '年' + month + '月' + strDate + '日'
    // + " " + date.getHours() + seperator2 + date.getMinutes();
    // + seperator2 + date.getSeconds();
    return currentdate;
}