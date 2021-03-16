/**
 * Created by yl on 2019/1/9.
 * 电子文件接收
 */
Ext.define('OfflineAccession.controller.OfflineAccessionController', {
    extend: 'Ext.app.Controller',
    views: [
        'OfflineAccessionBatchGridView','OfflineAccessionBatchFormView','TreeComboboxView',
        'OfflineAccessionView','OfflineAccessionFourSexView','OfflineAccessionResultGridView',
        'OfflineAccessionShowDocGridView'
    ],
    models: [
        'OfflineAccessionBatchGridModel','OfflineAccessionResultGridModel','OfflineAccessionDocGridModel'
    ],
    stores: [
        'OfflineAccessionBatchGridStore','OfflineAccessionResultGridStore','OfflineAccessionDocGridStore'
    ],
    init:function(){
         window.batchGridView;
         window.uploadFile;
        this.control({
            'offlineAccessionBatchGridView [itemId=addBatch]':{//新增批次
                 click:function (btn) {
                     window.batchGridView = btn.up('offlineAccessionBatchGridView');
                     var batchAddForm = Ext.create('OfflineAccession.view.OfflineAccessionBatchFormView');
                     var batchForm = batchAddForm.down('form');
                     batchForm.load({
                         url: '/offlineAccession/getBatchAddForm',
                         success: function (form, action) {

                         },
                         failure: function () {
                             XD.msg('获取表单信息失败');
                         }
                     });
                     batchAddForm.show();
                 }
            },
            'offlineAccessionBatchFormView [itemId =batchclose]':{// 关闭新增批次界面
                click:function (btn) {
                    btn.up('offlineAccessionBatchFormView').close();
                }
            },

            'offlineAccessionBatchFormView [itemId =batchsubmit]':{// 提交批次
                click:function (btn) {
                  var formview =  btn.up('offlineAccessionBatchFormView');
                  var form = formview.down('form');
                    if(!form.isValid()){
                        XD.msg('有必填项未填写，请处理后再提交');
                        return;
                    }
                    form.submit({
                        waitTitle: '提示',// 标题
                        waitMsg: '正在提交数据请稍后...',// 提示信息
                        url: '/offlineAccession/addBatch',
                        method: 'POST',
                        success: function (form, action) {
                            var respText = Ext.decode(action.response.responseText);
                            if (respText.success == true) {
                                XD.msg(respText.msg);
                                window.batchGridView.getStore().reload();
                             //取消选择
                                for(var i = 0; i < window.batchGridView.getStore().getCount(); i++){
                                    window.batchGridView.getSelectionModel().deselect(window.batchGridView.getStore().getAt(i));
                                }
                                window.batchGridView.acrossSelections = [];
                                formview.close();//添加成功后关闭窗口
                            } else {
                                XD.msg(respText.msg);
                            }
                        },
                        failure: function (form, action) {
                            var respText = Ext.decode(action.response.responseText);
                            XD.msg(respText.msg);
                            window.batchGridView.getStore().reload();
                            //取消选择
                            for(var i = 0; i < window.batchGridView.getStore().getCount(); i++){
                                window.batchGridView.getSelectionModel().deselect(window.batchGridView.getStore().getAt(i));
                            }
                            window.batchGridView.acrossSelections = [];
                            formview.close();//添加成功后关闭窗口
                        }
                    });
                }
            },


            'offlineAccessionBatchGridView [itemId=showBatch]':{//查看批次
                click:function (btn) {
                    var batchGridview = btn.up('offlineAccessionBatchGridView');
                    var record = batchGridview.getSelectionModel().getSelection();
                    if(record.length != 1){
                        XD.msg("只能选择一条需要操作的数据");
                        return;
                    }

                    var showdocWin = Ext.create('Ext.window.Window',{
                        modal:true,
                        width:900,
                        height:530,
                        title:'查看批次数据',
                        layout:'fit',
                        closeToolText:'关闭',
                        closeAction:'hide',
                        items:[{
                            xtype: 'offlineAccessionShowDocGridView',
                            batchid:record[0].data.batchid
                        }],
                        listeners:{
                            "close":function () {

                            }
                        }
                    });
                    var gridview = showdocWin.down('offlineAccessionShowDocGridView');
                    gridview.parentGrid =batchGridview,
                    gridview.initGrid({batchid:gridview.batchid});
                    showdocWin.show();
                }
            },

            'offlineAccessionBatchGridView [itemId=deleteBtnID]': {//删除批次
                click:function (btn) {
                    var batchGridView = btn.up('offlineAccessionBatchGridView');
                    var select = batchGridView.getSelectionModel();
                    if (select.getSelected().length<1) {
                        XD.msg('至少选择一条数据');
                        return;
                    }
                    var record = select.selected.items;
                    var batchids = [];
                    for (var i = 0; i < record.length; i++) {
                        batchids.push(record[i].get('batchid'));
                    }

                    XD.confirm('确定要删除这' + record.length + '条数据吗',function(){
                        Ext.Ajax.request({
                            params: {'batchids': batchids},
                            url: '/offlineAccession/batchDel',
                            method: 'POST',
                            sync: true,
                            success: function (resp) {
                                var respText = Ext.decode(resp.responseText);
                                if (respText.success == true) {
                                    batchGridView.getStore().reload();
                                    for(var i = 0; i < batchGridView.getStore().getCount(); i++){
                                        batchGridView.getSelectionModel().deselect(batchGridView.getStore().getAt(i));
                                    }
                                    batchGridView.acrossSelections = [];
                                }
                                XD.msg(respText.msg);
                            },
                            failure: function() {
                                XD.msg('操作失败');
                            }
                        });
                    },this);

                }
            },

            'offlineAccessionBatchGridView [itemId=print]':{   // 打印
                click:this.printHandler
            },

            'offlineAccessionBatchGridView [itemId=offAccession]':{//离线接收
                click:function (btn) {
                    var batchGridview = btn.up('offlineAccessionBatchGridView');
                    var record = batchGridview.getSelectionModel().getSelection();
                    if(record.length != 1){
                        XD.msg("只能选择一条需要操作的数据");
                        return;
                    }
                    var win = Ext.create('OfflineAccession.view.OfflineAccessionView',{
                        grid:batchGridview
                    });
                    win.fileNames=[];
                    win.show();
                }
            },
            'offlineAccessionView [itemId=accessionNext]': {//离线接收下一步
                click: this.accessionNext
            },
            'offlineAccessionResultGridView [itemId=lookdetails]': {//检测详情(上传)
                click: this.lookdetails
            },
            'offlineAccessionShowDocGridView [itemId=lookdetails]': {//检测详情(查看)
                click: this.lookdetails
            },
            'offlineAccessionResultGridView [itemId=lookPackpage]': {//查看数据包(上传)
                click: this.lookPackpage
            },
            'offlineAccessionShowDocGridView [itemId=lookPackpage]': {//查看数据包(查看)
                click: this.lookPackpage
            },
            'offlineAccessionResultGridView [itemId=insert]': {//接入系统(上传时)
                click: this.insert
            },
            'offlineAccessionShowDocGridView [itemId=insert]': {//接入系统(查看时)
                click: this.insert
            },
            'packageWindow [itemId=treepanelId]': {
                render: function (view) {
                    view.getRootNode().on('expand', function (node) {
                        node.getOwnerTree().getSelectionModel().select(node.childNodes[0]);
                    })
                },
                select: function (treemodel, record) {
                    if(record.get('children')!=null){
                        var dataview = this.findPackageWindow(treemodel.view).down('[itemId=dataview]');
                        var childrens = [];
                        for (var i = 0; i < record.get('children').length; i++) {
                            childrens.push(record.get('children')[i].text);
                        }
                        dataview.getStore().proxy.extraParams.childrens = childrens;
                        dataview.getStore().reload();
                    }
                    if(record.data.text.indexOf('.xml')>-1){
                        Ext.MessageBox.wait('正在解析获取元数据...', '提示');
                        var form = this.findMetadataForm(treemodel.view).getForm();
                        setTimeout(function(){
                            Ext.Ajax.request({
                                url:'/offlineAccession/getMetadata',
                                params:{
                                    fileName:window.filename,
                                    xmlName:record.data.text
                                },
                                success:function(response){
                                    Ext.MessageBox.hide();
                                    var responseText = Ext.decode(response.responseText);
                                    form.reset();
                                    form.setValues(responseText);
                                }
                            });
                        },100);
                    }
                }
            },
        });
    },
    findPackageWindow: function (btn) {
        return btn.up('packageWindow');
    },
    findMetadataForm: function (btn) {
        return this.findPackageWindow(btn).down('[itemId=metadataForm]');
    },

    accessionNext: function (btn) {
        var win =  btn.up('offlineAccessionView');
        if(win.fileNames.length==0){
            XD.msg('检测不到接收包，请上传文件');
            return;
        }
        var names = win.fileNames;

        Ext.MessageBox.wait('正在进行数据包安全认证...', '提示');
        setTimeout(function() {
            Ext.MessageBox.hide();
            var fileNames = [];
            var notpass = '';
            var pass ='';
            for (var i = 0; i < names.length; i++) {
                var name = names[i];
                if (name.indexOf('不通过') > -1) {
                    if ('' == notpass) {
                        notpass += '【' + name + '】数据包安全认证不通过'
                    } else {
                        notpass += ',【' + name + '】数据包安全认证不通过'
                    }
                } else {
                    if('' == pass){
                        pass += '【' + name + '】'
                    }else{
                        pass += ',【' + name + '】'
                    }
                    fileNames.push(name);
                }
            }
            if(notpass.length>0){
                Ext.MessageBox.alert("提示", notpass, callBack);
            }else{
                Ext.MessageBox.alert("提示", pass +'数据包通过安全认证', callBack);
            }
            function callBack() {
                win.close();
                var accessionWin = Ext.create('OfflineAccession.view.OfflineAccessionFourSexView');
                accessionWin.show();
                var record = win.grid.getSelectionModel().getSelection();
                var accessionGrid  = accessionWin.down('offlineAccessionResultGridView');
                accessionGrid.parentGrid = win.grid;
                accessionGrid.getStore().removeAll();
                Ext.MessageBox.wait('正在进行四性验证...', '提示');
                //进行四性验证
                setTimeout(function(){
                    XD.msg('检测完成！');
                    Ext.MessageBox.hide();
                    accessionGrid.initGrid({fileNames:fileNames.join(',')});
                },500);
                //保存四性鉴定结果
                setTimeout(function(){
                    var store = accessionGrid.getStore();
                    var batchdocList = [];

                    for(var i = 0; i < store.getCount(); i++){
                      var data = store.getAt(i);
                         var form = {
                             filename:data.data.filename,
                             authenticity:data.data.authenticity,
                             checkstatus:data.data.checkstatus,
                             integrity:data.data.integrity,
                             safety:data.data.safety,
                             usability:data.data.usability,
                             batchid:record[0].data.batchid
                         }
                        batchdocList.push(form)
                        var batchdocListJson = JSON.stringify(batchdocList);
                    }

                    Ext.Ajax.request({
                        url:'/offlineAccession/addBatchdoc',
                        params: {
                            batchdocListJson : batchdocListJson
                        },
                        traditional: true,
                        success:function(response){
                            window.uploadFile = Ext.decode(response.responseText)
                        }
                    })
                },1000);
            }
        },1500);
    },
    lookdetails:function (btn) {
        var grid = btn.up('offlineAccessionResultGridView');
        if(grid == undefined){
            grid = btn.up('offlineAccessionShowDocGridView');
        }
        var records = grid.selModel.getSelection();
        if(records.length != 1){
            XD.msg("请选择一条数据进行查看！");
            return;
        }
        var win = Ext.create('OfflineAccession.view.OfflineAccessionValidateView');
        win.down('[itemId=closeBtn]').on('click',function(){win.close()});
        win.down('[itemId=authenticity]').html = records[0].data.authenticity;
        win.down('[itemId=integrity]').html = records[0].data.integrity;
        win.down('[itemId=usability]').html = records[0].data.usability;
        win.down('[itemId=safety]').html = records[0].data.safety;
        win.show();
    },
    lookPackpage:function (btn) {
        var grid = btn.up('offlineAccessionResultGridView');
        if(grid == undefined){
            grid = btn.up('offlineAccessionShowDocGridView');
        }

        var records = grid.selModel.getSelection();
        if(records.length != 1){
            XD.msg("请选择一条数据进行查看！")
            return
        }
        window.filename = records[0].data.filename;
        var win = Ext.create('OfflineAccession.view.OfflineAccessionPackageView');
        win.show();
    },
    insert:function (btn) {
        var fileNames = [];
        var docid = [];
        var grid = btn.up('offlineAccessionResultGridView');
        if(grid == undefined){
            grid = btn.up('offlineAccessionShowDocGridView');
            var records = grid.selModel.getSelection();
            if (records.length == 0) {
                XD.msg('请选择数据接入系统');
                return;
            }
            for(var i = 0; i < records.length; i++){
                fileNames.push(records[i].data.filename);
                docid.push(records[i].data.id);
            }
        }
        else{
            //可能会出现数据不一致的bug，以后在处理
            var records = grid.selModel.getSelection();
            if (records.length == 0) {
                XD.msg('请选择数据接入系统');
                return;
            }
            for(var i = 0; i < records.length; i++){
                fileNames.push(window.uploadFile[i].filename);
                docid.push(window.uploadFile[i].id);
            }
        }
        var parentGrid = grid.parentGrid;
        var parentrecords = parentGrid.getSelectionModel().getSelection();

        Ext.MessageBox.wait('正在接入系统中...', '提示');
        Ext.Ajax.request({
            url:'/offlineAccession/insertCapture',
            params:{
                nodeid:parentrecords[0].data.nodeid,
                fileNames:fileNames.join(','),
                docid :docid.join(','),
            },
            success:function(response){
                Ext.MessageBox.hide();
                var responseText = Ext.decode(response.responseText);
                XD.msg(responseText.msg);
                if(grid.xtype == 'offlineAccessionResultGridView'){
                    var names = grid.dataParams.fileNames;
                    //刷新表格（上传界面）
                    grid.initGrid({fileNames:names,insertFileNames:fileNames.join(',')});

                }
                else {
                    //刷新表格（查看界面）
                    grid.getStore().reload();
                }

            }
        });
    },

    /**
     * 打印单据
     * @param btn
     */
    printHandler:function (btn) {
        var ids = [];
        var params = {};
        var docGrid = btn.up('offlineAccessionBatchGridView');
        var record = docGrid.getSelectionModel().getSelection();
        if (record.length < 1) {
            XD.msg('请选择需要打印的单据');
            return;
        }
        Ext.each(record, function () {
            ids.push(this.get('batchid'));
        });
        //打印未审核单据
        if (reportServer == 'UReport') {
            params['batchid'] = ids.join(",");
            XD.UReportPrint(null, '离线接收单据', params);
        } else if (reportServer == 'FReport') {
            XD.FRprint(null, '离线接收单据', ids.length > 0 ? "'batchid':'" + ids.join(",") + "'" : '');
        }

    }
});
