/**
 * Created by xd on 2017/10/21.
 */
var dataSourceType;//数据源类型
Ext.define('JyAdmins.controller.JyAdminsController', {
    extend: 'Ext.app.Controller',

    views: ['JyAdminsView', 'DzJyTreeView', 'DzJyGridView', 'LookBorrowdocMxGridView', 'LookBorrowdocMxView',
        'StJyGridView', 'StJyTreeView', 'XjDescAddView','DealDetailsGridView','DzPrintTreeView',
    'DzPrintGridView','PrintEleDetailView','PrintEleView','FindNotFileFormView','AppraiseView',
    'LookBorrowDetailFormView','LookBorrowdocDetailView','LookEvidenceTextView','LookPrintDetailFormView'],//加载view
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
                        var returnstate = dzJyGridView.down('[itemId=returnstateId]'); //归还状态列
                        buttons[4].hide();
                        tbseparators[3].hide();
                        buttons[5].hide();
                        tbseparators[4].hide();
                        returnstate.hide();
                        if ('已通过' == treetext) {
                            buttons[0].show();
                            tbseparators[0].show();
                            buttons[1].show();
                            tbseparators[1].show();
                            buttons[2].hide();
                            tbseparators[2].hide();
                            buttons[3].hide();
                            tbseparators[3].hide();
                            buttons[4].show();
                            tbseparators[4].hide();
                            buttons[5].show();
                            tbseparators[5].show();
                            returnstate.show();
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
                            if('已送审' == treetext){
                                buttons[4].hide();
                                tbseparators[3].hide();
                            }
                            buttons[0].show();
                            tbseparators[0].show();
                            buttons[1].show();
                            tbseparators[1].show();
                            buttons[2].hide();
                            tbseparators[2].hide();
                            buttons[3].show();
                        }
                        this.findWork('/jyAdmins/findByWorkId?workType=电子查档',dzJyGridView,treetext);
                        if(iflag==1){//利用平台
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
                            buttons[1].show();
                            tbseparators[1].show();
                            buttons[2].hide();
                            tbseparators[2].hide();
                            buttons[3].show();
                        }
                        this.findWork('/jyAdmins/findByWorkId?workType=实体查档',stJyGridView,treetext);
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
                            buttons[1].show();
                            tbseparators[1].show();
                            buttons[2].hide();
                            tbseparators[2].hide();
                            buttons[3].show();
                        }
                        this.findWork('/jyAdmins/findByWorkId?workType=电子打印',dzPrintGridView,treetext);
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
            'dzJyGridView button[itemId=urging]': {//电子查档催办
                click: function (view) {
                    var dzJyGridView = view.findParentByType('dzJyGridView');
                    this.borrowUrging(dzJyGridView);
                }
            },
            'stJyGridView button[itemId=urging]': {//实体查档催办
                click: function (view) {
                    var stJyGridView = view.findParentByType('stJyGridView');
                    this.borrowUrging(stJyGridView);
                }
            },
            'dzPrintGridView button[itemId=urging]': {//电子打印催办
                click: function (view) {
                    var dzPrintGridView = view.findParentByType('dzPrintGridView');
                    this.borrowUrging(dzPrintGridView);
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

            'dzJyGridView button[itemId=lookBorrowMsgId]': {//查档管理　查看单据信息
                click: function () {
                    var select = window.wdzJyGridView.getSelectionModel();
                    if (select.getSelected().length != 1) {
                        XD.msg('请选择一条数据');
                        return;
                    }
                    var borrowdocid = select.getSelected().items[0].get("id");
                    borrowcodeid = select.getSelected().items[0].get("id");
                    var borrowcode = select.getSelected().items[0].get("borrowcode");
                    dataSourceType=select.getSelected().items[0].get("datasourcetype")
                    var mxView = Ext.create('JyAdmins.view.LookBorrowdocDetailView');
                    var lookBorrowdocMxGridView = mxView.down('lookBorrowdocMxGridView');
                    if('已送审' == window.wdzJyGridView.treeItem){
                        lookBorrowdocMxGridView.down('[itemId=responsibleId]').hide();
                        lookBorrowdocMxGridView.down('[itemId=lyqxId]').hide();
                        lookBorrowdocMxGridView.down('[itemId=seriald]').hide();
                        lookBorrowdocMxGridView.down('[itemId=entrysecurityId]').hide();
                    }
                    if(iflag==1){
                        lookBorrowdocMxGridView.down('[itemId=lookEntryId]').hide();
                        lookBorrowdocMxGridView.down('[itemId=renew]').hide();
                    }
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
                    var gridView = mxView.getComponent("lookBorrowdocMxGridViewId");
                    gridView.borrowdocid = borrowdocid;
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


                    var entryAddForm = Ext.create('JyAdmins.view.FindNotFileFormView',{
                        borrowId :borrowId,
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


                    var entryAddForm = Ext.create('JyAdmins.view.FindNotFileFormView',{
                        borrowId :borrowId,
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
            }, 'dzPrintGridView button[itemId=showFileId]': {//电子打印管理　出具查无此档证明
                click: function (btn) {
                    var select = btn.findParentByType('dzPrintGridView').getSelectionModel();
                    if (select.getSelected().length != 1) {
                        XD.msg('请选择一条数据');
                        return;
                    }
                    var borrowId =select.getSelected().items[0].data.id;
                    var entryAddForm = Ext.create('JyAdmins.view.FindNotFileFormView',{
                        borrowId :borrowId,
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
                      success: function (form, action) {
                          XD.msg('操作成功,开始打印！');
                          var filenoneid = action.result.data.filenoneid;
                          formView.close();
                          //（判断需要打印那一份）打印开始
                          params['filenoneid'] = filenoneid;
                          if(data.organname == null || data.organname == ""){
                              if(data.manname != null && data.manname !='' && data.womanname != null && data.womanname != '') {
                                  //XD.UReportPrint("婚姻登记证明模板","婚姻登记证明模板（双人)",params);
                                  watermarkReport(filenoneid.trim(),'报表原件水印','婚姻登记证明模板（双人)');
                              } else{
                                 // XD.UReportPrint("婚姻登记证明模板","婚姻登记证明模板（单人)",params);
                                  watermarkReport(filenoneid.trim(),'报表原件水印','婚姻登记证明模板（单人)');
                              }
                          }else{
                              if(data.manname != null && data.manname !='' && data.womanname != null && data.womanname != '') {
                                  //XD.UReportPrint("婚姻登记证明模板","婚姻登记证明模板（单位双人)",params);
                                  watermarkReport(filenoneid.trim(),'报表原件水印','婚姻登记证明模板（单位双人)');
                              } else{
                                  //XD.UReportPrint("婚姻登记证明模板","婚姻登记证明模板（单位单人)",params);
                                  watermarkReport(filenoneid.trim(),'报表原件水印','婚姻登记证明模板（单位单人)');
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
                    if(data.filenoneid == null || data.filenoneid == ''||form.getForm().isDirty() == true) {
                        XD.msg("请先保存表单在进行打印操作！")
                    }else{
                        filenoneid.push(data.filenoneid.trim());
                        params['filenoneid'] = filenoneid.join(",");
                        var filenoneidStr= filenoneid.join(",");
                        if(data.organname == null || data.organname == ""){
                            if(data.manname != null && data.manname !='' && data.womanname != null && data.womanname != '') {
                                //XD.UReportPrint("婚姻登记证明模板","婚姻登记证明模板（双人)",params);
                                watermarkReport(filenoneidStr,'报表原件水印','婚姻登记证明模板（双人)');
                            }else{
                                //XD.UReportPrint("婚姻登记证明模板","婚姻登记证明模板（单人)",params);
                                watermarkReport(filenoneidStr,'报表原件水印','婚姻登记证明模板（单人)');
                            }
                        } else{
                            if((data.manname != null && data.manname !='' && data.womanname != null && data.womanname != '')) {
                                //XD.UReportPrint("婚姻登记证明模板","婚姻登记证明模板（单位双人)",params);
                                watermarkReport(filenoneidStr,'报表原件水印','婚姻登记证明模板（单位双人)');
                            } else{
                                //XD.UReportPrint("婚姻登记证明模板","婚姻登记证明模板（单位单人)",params);
                                watermarkReport(filenoneidStr,'报表原件水印','婚姻登记证明模板（单位单人)');
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
                    var mxView = Ext.create('JyAdmins.view.LookBorrowdocMxView');
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
                    var mxView = Ext.create('JyAdmins.view.LookBorrowdocMxView');
                    var lookBorrowdocMxGridView = mxView.down('lookBorrowdocMxGridView');
                    lookBorrowdocMxGridView.down('[itemId=renew]').hide();
                    lookBorrowdocMxGridView.down('[itemId=lookEntryId]').hide();
                    lookBorrowdocMxGridView.borrowcode = borrowcode;
                    var lookPrintDetailFormView = mxView.down('lookPrintDetailFormView');
                    lookPrintDetailFormView.borrowcode = borrowcode;
                    lookPrintDetailFormView.load({
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
                    var gridView = mxView.getComponent("lookBorrowdocMxGridViewId");
                    lookBorrowdocMxGridView.borrowdocid = borrowdocid;
                    lookBorrowdocMxGridView.down('[itemId=reItem]').hide();//隐藏未归还状态列
                    var buttons = mxView.down('toolbar').query('button');
                    buttons[0].hide();
                    buttons[1].hide();
                    buttons[2].show();
                    if(iflag!=1){  //利用平台不显示打印按钮
                        buttons[3].show();
                    }
                    lookBorrowdocMxGridView.initGrid({borrowdocid: borrowdocid});
                }
            },
            'dzJyGridView [itemId=printId],dzJyGridView [itemId=print]': {//电子查档管理 打印单据清册
                click:function () {
                    var ids = [];
                    var params = {};
                    if (window.wdzJyGridView.getSelectionModel().getSelected().length != 1) {
                        XD.msg('请选择一条数据');
                        return;
                    }
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
                    if (window.wstJyGridView.getSelectionModel().getSelected().length != 1) {
                        XD.msg('请选择一条数据');
                        return;
                    }
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
            'dzJyGridView [itemId=printApproval]': {//电子查档管理 打印审批单据
                click:function () {
                    var ids = [];
                    var code=[];
                    var params = {};
                    var app1=" ",app2=" ",app3=" ",app4=" ";
                    if(window.wdzJyGridView.getSelectionModel().getSelection().length!=1){
                        XD.msg("请选择一条需要打印的数据");
                        return;
                    }
                    Ext.each(window.wdzJyGridView.getSelectionModel().getSelection(),function(){
                        ids.push(this.get('id').trim());
                        code.push(this.get("borrowcode"));
                    });
                    Ext.Ajax.request({
                        method:'GET',
                        url:'/jyAdmins/getApproveByDocid',
                        async:false,
                        params:{
                            docid:ids
                        },
                        success:function(result){
                            responseText=Ext.decode(result.responseText);
                            if(responseText.success) {
                                if(responseText.data.length>3){
                                    app1 = responseText.data[0];
                                    app2 = responseText.data[1];
                                    app3 = responseText.data[2];
                                    app4 = responseText.data[3];
                                }else if(responseText.data.length>2){
                                    app1 = responseText.data[0];
                                    app2 = responseText.data[1];
                                    app3 = responseText.data[2];
                                }else if(responseText.data.length>1){
                                    app1 = responseText.data[0];
                                    app2 = responseText.data[1];
                                }else if(responseText.data.length>0){
                                    app1 = responseText.data[0];
                                }
                            }
                        }
                    });
                    if(reportServer == 'UReport') {
                        if(app1!= ' ')
                            params['app1'] = app1;
                        if(app2!= ' ')
                            params['app2'] = app2;
                        if(app3!= ' ')
                            params['app3'] = app3;
                        if(app4!= ' ')
                            params['app4'] = app4;
                        params['borrowcode'] =code ;
                        params['docid'] = ids;
                        XD.UReportPrint(null, '从化区国家档案馆档案调出审批表', params);
                    }
                    else if(reportServer == 'FReport') {
                        XD.FRprint(null, '从化区国家档案馆档案调出审批表', ids.length > 0 ? "'borrowcode':'" + ids.join(",") + "'" : '');
                    }
                }
            },
            'dzJyGridView [itemId=printEleApproval]': {//电子查档管理 打印电子数据审批单
                click:function () {
                    var ids = [];
                    var code=[];
                    var params = {};
                    var app1=" ",app2=" ",app3=" ",app4=" ";
                    if(window.wdzJyGridView.getSelectionModel().getSelection().length!=1){
                        XD.msg("请选择一条需要打印的数据");
                        return;
                    }
                    Ext.each(window.wdzJyGridView.getSelectionModel().getSelection(),function(){
                        ids.push(this.get('id').trim());
                        code.push(this.get("borrowcode"));
                    });
                    Ext.Ajax.request({
                        method:'GET',
                        url:'/jyAdmins/getApproveByDocid',
                        async:false,
                        params:{
                            docid:ids
                        },
                        success:function(result){
                            responseText=Ext.decode(result.responseText);
                            if(responseText.success) {
                                if(responseText.data.length>3){
                                    app1 = responseText.data[0];
                                    app2 = responseText.data[1];
                                    app3 = responseText.data[2];
                                    app4 = responseText.data[3];
                                }else if(responseText.data.length>2){
                                    app1 = responseText.data[0];
                                    app2 = responseText.data[1];
                                    app3 = responseText.data[2];
                                }else if(responseText.data.length>1){
                                    app1 = responseText.data[0];
                                    app2 = responseText.data[1];
                                }else if(responseText.data.length>0){
                                    app1 = responseText.data[0];
                                }
                            }
                        }
                    });
                    if(reportServer == 'UReport') {
                        if(app1!= ' ')
                            params['app1'] = app1;
                        if(app2!= ' ')
                            params['app2'] = app2;
                        if(app3!= ' ')
                            params['app3'] = app3;
                        if(app4!= ' ')
                            params['app4'] = app4;
                        params['borrowcode'] =code ;
                        params['docid'] = ids;
                        XD.UReportPrint(null, '从化区国家档案馆利用声像档案（电子数据）审批表', params);
                    }
                    else if(reportServer == 'FReport') {
                        XD.FRprint(null, '从化区国家档案馆利用声像档案（电子数据）审批表', ids.length > 0 ? "'borrowcode':'" + ids.join(",") + "'" : '');
                    }
                }
            },
            'dzPrintGridView button[itemId=print]': {//电子打印管理 打印单据清册
                click:function (btn) {
                    var ids = [];
                    var params = {};
                    if (btn.findParentByType('dzPrintGridView').getSelectionModel().getSelection().length != 1) {
                        XD.msg('请选择一条数据');
                        return;
                    }
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
            'mediaFormView [itemId=mediaBack]': {//查看返回
                click: this.lookBack
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
                    if(dataSourceType=="soundimage"){
                        var nodename=select.getSelected().items[0].get('nodefullname');
                        var mediaFormView = this.getNewMediaFormView(btn,'look',nodename,select.getSelected().items);
                        var form = mediaFormView.down('[itemId=dynamicform]');
                        var initFormFieldState = this.initFormField(form, 'hide', select.getSelected().items[0].get('nodeid'));
                        if(!initFormFieldState){//表单控件加载失败
                            return;
                        }
                        form.operate = 'look';
                        var records = select.getSelected().items;
                        form.selectItem = records;
                        form.entryid = records[0].get('entryid');
                        this.initMediaFormData('look', form, records[0].get('entryid'), records[0]);
                        mediaFormView.down("[itemId=batchUploadBtn]").hide();
                        mediaFormView.down('[itemId=save]').hide();
                        if(nodename.indexOf('音频') !== -1){
                            mediaFormView.down('[itemId=mediaDetailViewItem]').expand();
                        }
                        var media = Ext.create("Ext.window.Window", {
                            width: '100%',
                            height: '100%',
                            title: '查看原文',
                            closable: false,
                            modal: true,
                            closeToolText: '关闭',
                            layout: 'fit',
                            items: [{
                                xtype: mediaFormView
                            }]
                        });
                        window.mediaFormView = media;
                        media.show();
                    }else {
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
                        solidview.entryid = entryid;
                        if (iflag == 1) {
                            solidview.winType = 'lyjylook';
                        } else {
                            solidview.winType = 'gljylook';
                        }
                        window.remainEleids = eleids;
                        var treeStore = solidview.down('treepanel').getStore();
                        if (typeof (entryid) == 'undefined') {
                            solidview.down('treepanel').getRootNode().removeAll();
                            return;
                        }
                        solidview.unionAll = true;
                        treeStore.reload();
                        for (var i = 0; i < solidview.down('toolbar').query('button').length; i++) {
                            if (solidview.down('toolbar').query('button')[i].itemId == 'print' && iflag != 1) {
                                continue;
                            }
                            solidview.down('toolbar').query('button')[i].hide();
                        }
                        this.getELetopBtn(solidview.down('toolbar').query('button'), 'look');
                        media.show();
                        window.media = media;
                        Ext.on('resize', function () {
                            window.media.setPosition(0, 0);
                            window.media.fitContainer();
                        });
                    }
                }
            },
            'lookBorrowdocMxGridView button[itemId=hide]': {
                click: function (btn) {
                    var lookBorrowdocMxGridView = btn.findParentByType("lookBorrowdocMxGridView");
                    var borrowdocid = lookBorrowdocMxGridView.borrowdocid;
                    if(btn.findParentByType('lookBorrowdocMxView')){
                        var lookBorrowdocMxView = btn.findParentByType('lookBorrowdocMxView');
                        lookBorrowdocMxView.close();
                    }else{
                        var lookBorrowdocDetailView = btn.findParentByType('lookBorrowdocDetailView');
                        lookBorrowdocDetailView.close();
                    }
                    if(iflag!=1){
                        return;
                    }
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
                                var appraiseView = Ext.create("JyAdmins.view.AppraiseView");
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
                    if(entrys[0].get('type')!='实体查档'&&entrys[0].get('type')!='电子、实体查档'){
                        XD.msg('请选择实体查档进行续借！');
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

                    var XjDescAddView = Ext.create("JyAdmins.view.XjDescAddView");
                    XjDescAddView.entryids = entryIds;
                    XjDescAddView.stlookgrid = btn.findParentByType('lookBorrowdocMxGridView');
                    XjDescAddView.show();
                }
            },

            'xjDescAddView button[itemId=xjAddSubmit]': {
                click: function (btn) {
                    var xjDescAddView = btn.findParentByType('xjDescAddView');
                    var form = xjDescAddView.down('form');
                    var ts = form.down('textfield').value;
                    var desci = form.down('textarea').value;

                    if (isNaN(ts) || parseInt(ts) < 1) {
                        XD.msg('请输入正确格式');
                        return;
                    }

                    Ext.Ajax.request({
                        params: {
                            borrowid: xjDescAddView.stlookgrid.borrowdocid,
                            ids: xjDescAddView.entryids,
                            ts: ts,
                            xjapprove: desci+'\n'+'续借天数：'+ts+'\t'+'续借日期：'+getNowFormatDate(),
                            flag:true
                        },
                        url: '/electron/stXjAddFormBill',
                        method: 'POST',
                        sync: true,
                        success: function () {
                            XD.msg('续借成功');
                            xjDescAddView.stlookgrid.initGrid();
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
            // 'jyAdminsView': {
            //     render: function (view) {
            //         view.down('[itemId=stJyTreeViewID]').on('render', function (view) {
            //             view.getSelectionModel().select(view.getRootNode().childNodes[0]);//默认选中第一个
            //         });
            //     }
            // },

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
                            XD.msg('存在打印申请已到期，不允许查看原文！');
                            return;
                        }
                        if(select[i].get('lyqx')==='拒绝'){
                            XD.msg('存在数据未审批通过，不允许查看原文！');
                            return;
                        }
                        entryids.push(select[i].get("entryid"));
                    }
                    var iframe = document.getElementById("mediaFramePrint");
                    if(iframe){
                        iframe.parentNode.removeChild(iframe);
                    }
                    var printEleView = Ext.create("JyAdmins.view.PrintEleView");
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
                    var printEleView = Ext.create("JyAdmins.view.PrintEleView");
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
                            var appraiseView = Ext.create("JyAdmins.view.AppraiseView");
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
            },

            'lookBorrowDetailFormView button[itemId=electronId]': {
                click: function (view) {
                    var electronicProView = Ext.create("Ext.window.Window", {
                        width: '100%',
                        height: '100%',
                        title: '查看文件',
                        modal: true,
                        header: false,
                        draggable: false,//禁止拖动
                        resizable: false,//禁止缩放
                        closeToolText: '关闭',
                        layout: 'fit',
                        items: [{xtype: 'electronicPro'}]
                    });
                    var lookBorrowDetailFormView = view.findParentByType('lookBorrowDetailFormView');
                    electronicProView.down('electronicPro').initData(lookBorrowDetailFormView.borrowcode);
                    electronicProView.show();
                }
            },

            'lookPrintDetailFormView button[itemId=electronId]': {
                click: function (view) {
                    var electronicProView = Ext.create("Ext.window.Window", {
                        width: '100%',
                        height: '100%',
                        title: '查看文件',
                        modal: true,
                        header: false,
                        draggable: false,//禁止拖动
                        resizable: false,//禁止缩放
                        closeToolText: '关闭',
                        layout: 'fit',
                        items: [{xtype: 'electronicPro'}]
                    });
                    var lookPrintDetailFormView = view.findParentByType('lookPrintDetailFormView');
                    electronicProView.down('electronicPro').initData(lookPrintDetailFormView.borrowcode);
                    electronicProView.show();
                }
            },

            'lookBorrowdocMxGridView button[itemId=lookEntryId]': { //查看条目
                click: function (btn) {
                    var record = btn.findParentByType('lookBorrowdocMxGridView').getSelectionModel().getSelection();
                    if (record.length < 1) {
                        XD.msg('请至少选择一条数据');
                        return;
                    }
                    var entryids = [];
                    var nodeids = [];
                    for(var i=0;i<record.length;i++){
                        entryids.push(record[i].get('entryid'));
                        nodeids.push(record[i].get('nodeid'));
                    }
                    var lookEntryView =  Ext.create("Ext.window.Window",{
                        width:'100%',
                        height:'100%',
                        plain: true,
                        header: false,
                        border: false,
                        closable: false,
                        frame:false,
                        draggable : false,//禁止拖动
                        resizable : false,//禁止缩放
                        modal:true,
                        closeToolText:'关闭',
                        layout:'fit',
                        items:[{
                            xtype: 'EntryFormView'
                        }]
                    });
                    var entryid = record[0].get('entryid');
                    var form = lookEntryView.down('dynamicform');
                    form.operate = 'look';
                    form.entryids = entryids;
                    form.nodeids = nodeids;
                    form.entryid = entryids[0];
                    form.nodeid = nodeids[0];
                    this.initFormField(form, 'hide', record[0].get('nodeid'));
                    this.initFormData('look',form, entryid);
                    lookEntryView.show();
                }
            },

            'EntryFormView [itemId=preBtn]':{
                click:this.preHandler
            },

            'EntryFormView [itemId=nextBtn]':{
                click:this.nextHandler
            },

            'EntryFormView [itemId=back]':{
                click:function (view) {
                    view.findParentByType('window').close();
                }
            }
        });
    },
    //获取新的mediaFormView
    getNewMediaFormView: function (btn, operate, mediaType,record) {
        var formAndGrid= btn.up("lookBorrowdocMxGridView")
        var entryid = '';
        var across=record[0];
        if (typeof across !== 'undefined' && (operate === 'look' || operate === 'modify')) {
            entryid = across.get('entryid');
        }
        var accept, uploadLabel;
        var dynamicRegion = 'west', header = true, col_spli = true, collapsed = false, collapsible = false;
        if (mediaType.indexOf('照片') !== -1) {
            accept = {
                title: 'Images',
                extensions: 'jpeg,jpg,png,bmp,gif,tiff,tif,crw,cr2,nef,raf,raw,kdc,mrw,nef,orf,dng,ptx,pef,arw,x3f,rw2',
                mimeTypes: 'image/*'
            };
            uploadLabel = '上传照片';
        } else if (mediaType.indexOf('视频') !== -1) {
            accept = {
                title: 'Videos',
                extensions: 'mp4,avi',
                mimeTypes: 'video/*'
            };
            uploadLabel = '上传视频';
        } else if (mediaType.indexOf('音频') !== -1) {
            accept = {
                title: 'Audio',
                extensions: 'mp3',
                mimeTypes: 'audio/*'
            };
            uploadLabel = '上传音频';
            dynamicRegion = 'south';
            header = false;
            col_spli = false;
            collapsed = true;
            collapsible = true;
        }
        formAndGrid.remove(formAndGrid.down('[itemId=amediaFormView]'));//删除原有的mediaFormView，确保干净
        var dynamicFromItem = {
            region: dynamicRegion,
            title: '条目',
            iconCls: 'x-tab-entry-icon',
            itemId: 'dynamicform',
            xtype: 'dynamicform',
            calurl: '/management/getCalValue',
            items: [{
                xtype: 'hidden',
                name: 'entryid'
            }],
            width: '70%',
            flex: 4,
            collapsible: col_spli,
            split: col_spli
        };
        var detailViewItem = {
            region: 'center',
            header: header,
            title: uploadLabel.substr(2, 2),
            iconCls: 'x-tab-electronic-icon',
            itemId: 'mediaDetailViewItem',
            entrytype: '',
            layout: 'fit',
            xtype: 'panel',
            items: [{
                itemId: 'mediaHtml',
                html: '<div id="mediaDiv" class="pw-view" style="background:white"></div>'
            }],
            flex: 1,
            collapsed: collapsed,
            collapsible: collapsible
        };
        formAndGrid.add({
            itemId: 'amediaFormView',
            xtype: 'mediaFormView',
            entryid: entryid,
            flag: false,//默认不用刷新
            acceptMedia: accept,
            uploadLabel: uploadLabel,
            mediaType: mediaType,
            items: [dynamicFromItem, detailViewItem]
        });
        return formAndGrid.down('[itemId=amediaFormView]');
    },

    initMediaFormData: function (operate, form, entryid, record) {
        var nullvalue = new Ext.data.Model();
        form.down('[itemId=preBtn]').hide();
        form.down('[itemId=nextBtn]').hide();
        var mediaFormView = form.up('mediaFormView');
        var fields = form.getForm().getFields().items;
        var prebtn = mediaFormView.down('[itemId=MpreBtn]');
        var nextbtn = mediaFormView.down('[itemId=MnextBtn]');
        var totaltext = form.up("mediaFormView").down('[itemId=MtotalText]');
        totaltext.setText('当前共有  1  条，');
        var nowtext = form.up("mediaFormView").down('[itemId=MnowText]');
        nowtext.setText('当前记录是第  1  条');
        totaltext.show();
        nowtext.show();
        prebtn.hide();
        nextbtn.hide();
        for (var i = 0; i < fields.length; i++) {
            if (fields[i].value && typeof(fields[i].value) == 'string' && fields[i].value.indexOf('label') > -1) {
                continue;
            }
            if (fields[i].xtype == 'combobox') {
                fields[i].originalValue = null;
            }
            nullvalue.set(fields[i].name, null);
        }
        form.loadRecord(nullvalue);
        if (operate != 'look' && operate != 'lookfile') {

        } else {
            Ext.each(fields, function (item) {
                item.setReadOnly(true);
            });
        }
        var urls= '/management/entries/' + entryid+"?xtType="+"声像系统";
        Ext.Ajax.request({
            method: 'GET',
            scope: this,
            url: urls,
            success: function (response) {
                var entry = Ext.decode(response.responseText);
                var data = Ext.decode(response.responseText);
                if (data.organ) {
                    entry.organ = data.organ;//机构
                }
                var fieldCode = form.getRangeDateForCode();//字段编号，用于特殊的自定义字段(范围型日期)
                if (fieldCode != null) {
                    //动态解析数据库日期范围数据并加载至两个datefield中
                    form.initDaterangeContent(entry);
                }
                form.loadRecord({
                    getData: function () {
                        return entry;
                    }
                });
                form.entryid = entry.entryid;
                if (operate == 'look' || operate == 'modify') {
                    Ext.Ajax.request({
                        method: 'POST',
                        params: {entryid: entry.entryid},
                        url: '/electronic/getSxElectronicByEntryid',
                        async: false,
                        success: function (response) {
                            var eleRecord = Ext.decode(response.responseText).data;
                            mediaFormView.currentMD5 = eleRecord.md5;
                            if (record.get('background') === '') {
                                mediaFormView.compressing = true;
                                var videoHtml = '<img src="/img/defaultMedia/videoloading.gif" style="position:absolute;top:0;right:0;left:0;bottom:0;margin:auto;width:350px;height:240px"/>';
                                document.getElementById('mediaDiv').innerHTML = videoHtml;
                            } else if (mediaFormView.mediaType.indexOf('照片') !== -1) {
                                if (typeof(mediaFormView.photoView) == 'undefined') {
                                    mediaFormView.photoView = new PhotoView({
                                        eleid: 'mediaDiv',
                                        src: '/electronic/loadSpecialMedia?entryid=' + entryid+"&fileType=photo",
                                        initWidth: '90%'
                                    });
                                } else {
                                    Ext.apply(mediaFormView.uploader.options, {
                                        server: '/electronic/serelectronics/' + mediaFormView.entrytype + "/" + form.entryid
                                    });
                                    mediaFormView.photoView.changeImg('/electronic/loadSpecialMedia?entryid=' + entryid);
                                }
                            } else if (mediaFormView.mediaType.indexOf('视频') !== -1) {
                                mediaFormView.compressing = false;
                                var videoHtml = '<a href="/electronic/loadSpecialMedia?entryid=' + entryid + '&fileType=video" style="position:absolute;top:0;right:0;left:0;bottom:0;margin:auto;width:520px;height:320px" id="player"></a>';
                                document.getElementById('mediaDiv').innerHTML = videoHtml;
                                flowplayer("player", "../js/flowplayerFlash/flowplayer.swf", {
                                    plugins: {
                                        controls: {
                                            height: 30,
                                            tooltips: {
                                                buttons: true,
                                                play: '播放',
                                                fullscreen: '全屏',
                                                fullscreenExit: '退出全屏',
                                                pause: '暂停',
                                                mute: '静音',
                                                unmute: '取消静音'
                                            }
                                        }
                                    },
                                    canvas: {
                                        backgroundColor: '#000',
                                        backgroundGradient: [0, 0]//无渐变色
                                    },
                                    clip: {
                                        autoPlay: false,
                                        autoBuffering: true
                                    },
                                    onStart: function (clip) {
                                        animate(this, clip, {
                                            height: 320,
                                            width: 520
                                        })
                                    },
                                    onFullscreen: function (clip) {
                                        setTimeout(function () {
                                            animate(this, clip, {
                                                height: screen.height,
                                                width: screen.width
                                            }, clip);
                                        }, 1000);
                                    }
                                });
                            } else if (mediaFormView.mediaType.indexOf('音频') !== -1) {
                                mediaFormView.compressing = false;
                                var videoHtml = '<div class="audio-box"></div>';
                                document.getElementById('mediaDiv').innerHTML = videoHtml;
                                Ext.Ajax.request({
                                    params: {entryid: entryid},
                                    url: '/electronic/getBrowseByEntryid',
                                    success: function (response) {
                                        var responseText = Ext.decode(response.responseText);
                                        if (responseText.data !== null) {
                                            var name = responseText.data.filename;
                                            name = name.substring(0, name.lastIndexOf('.'));
                                            var audioFn = audioPlay({
                                                song: [{
                                                    title: name,
                                                    src: responseText.data.filepath + "/" + responseText.data.filename,
                                                    cover: '../../img/defaultMedia/default_audio.png'
                                                }],
                                                error: function (msg) {
                                                    XD.msg(msg.meg);
                                                    console.log(msg)
                                                }
                                            });
                                            if (audioFn) {
                                                audioFn.loadFile(false);
                                            }
                                        }
                                    },
                                    failure: function () {
                                        XD.msg('获取浏览音频中断');
                                    }
                                });
                            }
                        },
                        failure: function () {
                            XD.msg('操作失败！');
                        }
                    });
                }
            }
        });
    },
    lookBack: function (btn) {
        if (window.play) {
            window.play(false);//音频停止播放
        }
        btn.up('mediaFormView').destroy();//销毁，防止视频在后台继续播放
        window.mediaFormView.close();
    },
    borrowUrging:function(view){
        var select = view.getSelectionModel();
        if (!select.hasSelection()) {
            XD.msg('请选择一条数据!');
            return;
        }
        var details = select.getSelection();
        if(details.length!=1){
            XD.msg('只支持单条数据催办!');
            return;
        }
        Ext.Ajax.request({
            params: {borrowcode: details[0].get("borrowcode"),sendMsg:view.down("[itemId=message]").checked},
            url: '/jyAdmins/manualUrging',
            method: 'POST',
            sync: true,
            success: function (response) {
                var respText = Ext.decode(response.responseText);
                XD.msg(respText.msg);
            },
            failure: function () {
                XD.msg('操作失败');
            }
        });
    },
    findWork:function(url,view,treetext){
        view.down('[itemId=urging]').hide();
        view.down('[itemId=message]').hide();
        if(treetext!="已送审"){
            return;
        }
        Ext.Ajax.request({//根据审批id判断是否可以催办
            url: url ,
            method: 'GET',
            success: function (resp) {
                var respDate = Ext.decode(resp.responseText).data;
                if(respDate.urgingstate=="1"){
                    view.down('[itemId=urging]').show();
                    view.down('[itemId=message]').show();
                }
            }
        });
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
    },

    //获取电子文件顶部按钮显示权限
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
    initFormField:function(form, operate, nodeid){
        form.nodeid = nodeid;//用左侧树节点的id初始化form的nodeid参数
        form.removeAll();//移除form中的所有表单控件
        var field = {
            xtype: 'hidden',
            name: 'entryid'
        };
        form.add(field);
        var formField;
        if(dataSourceType=='soundimage'){  //声像系统
            formField = form.getSxFormField();//根据节点id查询表单字段
            form.xtType="声像系统";
        }else{
            formField = form.getFormField();//根据节点id查询表单字段
            form.xtType="档案系统";
        }
        if(formField.length==0){
            XD.msg('请检查模板设置信息是否正确');
            return;
        }
        form.templates = formField;
        form.initField(formField,operate);//重新动态添加表单控件
        return '加载表单控件成功';
    },

    getCurrentSimpleSearchform:function (btn) {
        return btn.up('EntryFormView');
    },

    //点击上一条
    preHandler:function(btn){
        var currentSimpleSearchform = this.getCurrentSimpleSearchform(btn);
        var form = currentSimpleSearchform.down('dynamicform');
        this.refreshFormData(form, 'pre');
    },

    //点击下一条
    nextHandler:function(btn){
        var currentSimpleSearchform = this.getCurrentSimpleSearchform(btn);
        var form = currentSimpleSearchform.down('dynamicform');
        this.refreshFormData(form, 'next');
    },

    refreshFormData:function(form, type){
        var entryids = form.entryids;
        var nodeids = form.nodeids;
        var currentEntryid = form.entryid;
        var entryid;
        var nodeid;
        for(var i=0;i<entryids.length;i++){
            if(type == 'pre' && entryids[i] == currentEntryid){
                if(i==0){
                    i=entryids.length;
                }
                entryid = entryids[i-1];
                nodeid = nodeids[i-1];
                break;
            }else if(type == 'next' && entryids[i] == currentEntryid){
                if(i==entryids.length-1){
                    i=-1;
                }
                entryid = entryids[i+1];
                nodeid = nodeids[i+1];
                break;
            }
        }
        form.entryid = entryid;
        if(form.operate != 'undefined'){
            this.initFormField(form, 'hide', nodeid);//上下条时切换模板
            this.initFormData(form.operate, form, entryid);
            return;
        }
        this.initFormField(form, 'hide', nodeid);
        this.initFormData('look', form, entryid);
    },

    initFormData:function (operate, form, entryid) {
        var formview = form.up('EntryFormView');
        var nullvalue = new Ext.data.Model();
        var fields = form.getForm().getFields().items;
        if(operate == 'look') {
            for (var i = 0; i < form.entryids.length; i++) {
                if (form.entryids[i] == entryid) {
                    count = i + 1;
                    break;
                }
            }
            var total = form.entryids.length;
            var totaltext = form.down('[itemId=totalText]');
            totaltext.setText('当前共有  ' + total + '  条，');
            var nowtext = form.down('[itemId=nowText]');
            nowtext.setText('当前记录是第  ' + count + '  条');

            Ext.each(fields,function (item) {
                item.setReadOnly(true);
            });
        }else{
            Ext.each(fields,function (item) {
                if(!item.freadOnly){
                    item.setReadOnly(false);
                }
            });
        }
        for(var i = 0; i < fields.length; i++){
            if(fields[i].value&&typeof(fields[i].value)=='string'&&fields[i].value.indexOf('label')>-1){
                continue;
            }
            if(fields[i].xtype == 'combobox'){
                fields[i].originalValue = null;
            }
            nullvalue.set(fields[i].name, null);
        }
        form.loadRecord(nullvalue);
        var etips = formview.down('[itemId=etips]');
        etips.show();
        Ext.Ajax.request({
            method:'GET',
            scope:this,
            url:'/management/entries/'+entryid,
            success:function(response){
                if(operate!='look'){
                    var settingState = ifSettingCorrect(form.nodeid,form.templates);
                    if(!settingState){
                        return;
                    }
                }
                var entry = Ext.decode(response.responseText);
                form.loadRecord({getData:function(){return entry;}});
                //字段编号，用于特殊的自定义字段(范围型日期)
                var fieldCode = form.getRangeDateForCode();
                if(fieldCode!=null){
                    //动态解析数据库日期范围数据并加载至两个datefield中
                    form.initDaterangeContent(entry);
                }
                //初始化原文数据
                var eleview = formview.down('electronic');
                var solidview = formview.down('solid');
                solidview.xtType="";
                eleview.initData(entryid);
                solidview.initData(entryid);
                form.fileLabelStateChange(eleview,operate);
                form.fileLabelStateChange(solidview,operate);
            }
        });
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

function watermarkReport(filenoneidStr,watermarkName,reportName){
    Ext.Ajax.request({//获取pdf报表的base64编码，在后台生成一个pdf，对此PDF加水印
        url: '/acquisition/getWaterprintPdf',
        params:{
            filenoneid:filenoneidStr,
            watermarkName:watermarkName,
            reportName:encodeURI(reportName)
        },
        method: 'POST',
        success: function (resp) {//得到pdf报表的base64编码
            XD.msg(Ext.decode(resp.responseText).msg);
            var pdfData= Ext.decode(resp.responseText).data;//报表的base64编码
            //打开加水印的pdf报表文件
            sessionStorage.setItem("_imgUrl", pdfData);
            var url = '../../../js/pdfJs/web/ureportviewer.html';
            window.open(url, '_blank');

        },
        failure:function(){
            XD.msg('表单生成失败');
        }
    });
}