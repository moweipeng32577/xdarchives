/**
 * Created by xd on 2017/10/21.
 */
var UnZipPath = "";
Ext.define('Datareceive.controller.DatareceiveController', {
    extend: 'Ext.app.Controller',

    views: ['DatareceiveView', 'DatareceiveOpenGridView', 'DatareceiveThematicGridView',
        'DatareceivedGridView','ImportMsgView','ImportView','DataNodeComboView','ImportDataPackage',
        'DatareceiveThematicAlreadyGridView','DatareceivedResultGridView','DatareceivedResultDetailView'],//加载view
    stores: ['DatareceiveOpenGridStore','DatareceivedGridStore','ImportGridStore',
            'TemplateStore','DatareceiveThematicGridStore','DatareceiveThematicAlreadyGridStore',
            'DatareceivedResultGridStore'],//加载store
    models: ['DatareceiveOpenGridModel','ImportGridModel','DatareceivedResultGridModel'],//加载model

    init: function () {
        var dataReceiveView;
        var dataType;
        this.control({
            'datareceiveOpenGridView': {
                afterrender: function (view) {
                    dataReceiveView=view;
                    view.initGrid({state:'待接收',type: 'dataopen'});
                    var fnButton=functionButton;
                    for(var i=0;i<fnButton.length;i+=2){
                        if (view.down('[itemId=' + fnButton[i].itemId + ']') != null) {
                            view.down('[itemId=' + fnButton[i].itemId + ']').show();
                        }
                    }
                }
            },
            'datareceiveView': {
                tabchange: function (view) {
                    var grid;
                    var type;
                    if (view.activeTab.title == '开放数据') {
                        grid = view.down('datareceiveOpenGridView');
                        type = 'dataopen';
                    } else if (view.activeTab.title == '专题档案') {
                        grid = view.down('datareceiveThematicGridView');
                        type = 'thematic';
                    }
                    dataReceiveView=grid;
                    dataType=type;
                    var fnButton=functionButton;
                    for(var i=0;i<fnButton.length;i+=2){
                        if (grid.down('[itemId=' + fnButton[i].itemId + ']') != null) {
                            grid.down('[itemId=' + fnButton[i].itemId + ']').show();
                        }
                    }
                    grid.initGrid({state:'待接收',type: type});
                }
            },
            'datareceiveOpenGridView  [itemId=upload]': {//上传
                click:this.uploadDataPackage
            }, 'datareceiveOpenGridView  [itemId=download]': {//下载
                click:function (btn) {
                    var grid = btn.up('datareceiveOpenGridView');
                    this.downloadDataPackage(grid)
                }
            },
            'datareceiveThematicGridView  [itemId=upload]': {//上传
                click:this.uploadDataPackage
            }, 'datareceiveThematicGridView  [itemId=download]': {//下载
                click:function (btn) {
                    var grid = btn.up('datareceiveThematicGridView');
                    this.downloadDataPackage(grid)
                }
            },
            'datareceiveOpenGridView  [itemId=receive]': {
                click: this.receiveHandler
            },
            'datareceiveOpenGridView  [itemId=received]': {
                click: this.receivedHandler
            },
            'treelist': {
                selectionchange: this.systemChange
            },
            'dataNodeComboView': {
                change: this.nodeSelected
            },
            '[itemId=fieldgrid]': {
                edit: this.edit
            },
            'importMsgView button[itemId="import"]': {
                click: this.impHandler
            },
            'import button[itemId=impBtn]': {
                click: function (btn) {
                    var workspace = btn.up('[itemId=workspace]');
                    var exportMissView = Ext.create('Datareceive.view.ImportMsgView');
                    exportMissView.preview = workspace;
                    var importview = btn.up('import');
                    exportMissView.importview = importview;
                    exportMissView.show();
                }
            },
            'import button[itemId="back"]':{//导入-返回
                click: function(btn){
                    var a=btn.up('window').down('import');
                    var fieldstore=a.down('[itemId=fieldgrid]').getStore();
                    fieldstore.removeAll();
                    btn.up('window').hide();
                    Ext.Ajax.request({
                        method: 'post',
                        url:'/import/deletUploadFile',
                        timeout:3600000,
                        params: {filePath:UnZipPath}
                    });
                }
            },
            'datareceivedGridView  [itemId=delete]': {
                click: this.deleteHandler
            },
            'datareceiveThematicGridView  [itemId=receive]': {
                click: this.thematicReceiveHandler
            },
            'datareceiveThematicGridView  [itemId=received]': {
                click: this.thematicReceivedHandler
            },
            'datareceiveThematicAlreadyGridView  [itemId=delete]': {
                click: this.thematicDeleteHandler
            },
            //执行验证
            'datareceiveOpenGridView button[itemId=implement]': {
                click:this.implementHandler
            },
            //查看验证
            'datareceiveOpenGridView button[itemId=lookdetail]': {
                click:this.lookResultHandler
            },
            //打印
            'datareceiveOpenGridView button[itemId=print]': {
                click:this.printHandler
            },
            //打印
            'datareceiveThematicGridView button[itemId=print]': {
                click:this.printHandler
            },
            //查看验证明细
            'datareceivedResultGridView [itemId=lookdetail]': {//查看验证明细
                click: this.lookdetailsHandler
            },
            'fieldset':{
                change:function (field,value) {

                }
            },
            'importDataPackage [itemId=uploadID]': {//上传数据包
                click: function (btn) {
                    var importDataPackage=btn.findParentByType("importDataPackage");
                    importDataPackage.down('form').getForm().submit({
                        waitTitle: '提示',
                        waitMsg: '请稍后，上传数据包...',
                        url: '/datareceive/uploadDataPackage',
                        params: {
                            dataType:dataType
                        },
                        scope:this,
                        success: function (basic, action) {
                            btn.findParentByType('window').close();
                            Ext.MessageBox.hide();
                            var result = action.result;
                            XD.msg(result.msg);
                            //更新数据
                            dataReceiveView.initGrid({state:'待接收',type: dataType});
                        },
                        failure: function(form, action) {
                            Ext.MessageBox.hide();
                            var result = action.result;
                            XD.msg(result.msg);
                        },
                    });
                }
            },
            'importDataPackage [itemId=closeBtnID]': {//上传数据包-关闭
                click: function (btn) {
                    btn.findParentByType('window').close();
                }
            },
        });
    },
    uploadDataPackage:function(btn){
        var importDataPackage = Ext.create('Ext.window.Window', {
            height: '20%',
            width: '65%',
            draggable: false,//禁止拖动
            resizable: false,//禁止缩放
            modal: true,
            closeToolText:'关闭',
            title: '上传数据包',
            layout: 'fit',
            items: [{
                xtype: 'importDataPackage'
            }]
        });
        importDataPackage.show();
    },
    downloadDataPackage:function (grid) {
        var record = grid.selModel.getSelection();
        if(record.length !=1){
            XD.msg('请选择一条下载数据');
            return;
        }
        location.href="/datareceive/downloadDataPackage?receiveid="+ record[0].get('receiveid');
    },
    receiveHandler: function (btn) {
        var grid = btn.up('datareceiveOpenGridView');
        var record = grid.selModel.getSelection();
        if(record.length == 0){
            XD.msg('请选择一条需要接收的数据');
            return;
        }
        if(record.length >1){
            XD.msg('只能选择一条需要接收的数据');
            return;
        }
        var reportGridWin = Ext.create('Ext.window.Window', {
            width: '100%',
            height: '100%',
            header: false,
            draggable: false,//禁止拖动
            resizable: false,//禁止缩放
            modal: true,
            closeToolText: '关闭',
            layout: 'fit',
            items: [{
                xtype: 'import',
                grid: grid
            }]
        });
        var fnid = record[0].get('nodeid');
        var text = record[0].get('currentnode');
        var filepath = record[0].get('filepath');
        var combo = reportGridWin.down('dataNodeComboView');
        reportGridWin.down('form').getForm().findField('target').setValue(fnid);
        reportGridWin.down('form').getForm().findField('source').setValue(filepath);
        combo.setValue(text);
        combo.fireEvent('change', combo, {
            fnid:fnid,
            text:text
        });
        reportGridWin.show();
    },
    receivedHandler: function (btn) {
        var win = Ext.create('Ext.window.Window',{
            width:'80%',
            height:'80%',
            title: '已接收数据包',
            draggable : false,//禁止拖动
            resizable : false,//禁止缩放
            modal:true,
            closeToolText:'关闭',
            closeAction:'hide',
            layout:'fit',
            items:[{
                xtype: 'datareceivedGridView'
            }]
        });
        var grid = win.down('datareceivedGridView');
        grid.initGrid({state:'已接收',type:'dataopen'});
        win.show();
    },
    thematicReceiveHandler: function (btn) {
        var grid = btn.up('datareceiveThematicGridView');
        var record = grid.selModel.getSelection();
        if(record.length == 0){
            XD.msg('请选择一条需要接收的数据');
            return;
        }
        if(record.length >1){
            XD.msg('只能选择一条需要接收的数据');
            return;
        }
        XD.confirm('确定要接收这'+record.length+'条专题数据包吗?', function () {
            var names = [];
            for (var i = 0; i < record.length; i++) {
                names.push(record[i].get('filename'));
            }
            var filenames = names.join(",");
            Ext.MessageBox.wait('正在接收请稍后...', '提示');
            Ext.Ajax.request({
                method: 'post',
                url: '/datareceive/thematicReceive',
                params: {
                    filenames:filenames
                },
                success: function (response, opts) {
                    XD.msg('接收成功');
                    grid.initGrid();
                    Ext.MessageBox.hide();
                },
                failure: function (response, opts) {
                    XD.msg('操作失败');
                    Ext.MessageBox.hide();
                }
            });
        });
    },
    thematicReceivedHandler: function (btn) {
        var win = Ext.create('Ext.window.Window',{
            width:'80%',
            height:'80%',
            title: '已接收数据包',
            draggable : false,//禁止拖动
            resizable : false,//禁止缩放
            modal:true,
            closeToolText:'关闭',
            closeAction:'hide',
            layout:'fit',
            items:[{
                xtype: 'datareceiveThematicAlreadyGridView'
            }]
        });
        var grid = win.down('datareceiveThematicAlreadyGridView');
        grid.initGrid({state:'已接收',type:'thematic'});
        win.show();
    },
    /**
     * 选中节点，使用不同的导入工作页面
     * @param treelist
     * @param record
     */
    systemChange: function (treelist, record) {
        var workspace = treelist.up('import').down('[itemId=workspace]');
        workspace.setTitle('导入[' + record.data.text + ']数据');
    },

    /**
     * 选中目的节点
     * @param combo
     * @param item
     */
    nodeSelected: function (combo, item) {
        //如果源数据包和目的节点都有值，则提交表单
        var form = combo.up('form');
        var source = form.getForm().findField('source');
        var nodeid = form.getForm().findField('target').getValue();
        var flag = this.getOrganid(nodeid);
        if (!flag) {
            XD.msg('当前选的节点不是机构节点');
            return;
        }
        if (source.getValue() != null) {
            this.submit(form);
        }
        //根据目的节点刷新模板数据
        var templateStore = this.getStore('TemplateStore');
        templateStore.load({
            params: {
                nodeid: form.getForm().findField('target').getValue()
            }
        });
    },

    /**
     * 修改字段设置后，调整导入预览标题
     * @param editor
     * @param e
     */
    edit: function (editor, e) {
        var workspace = editor.grid.up('[itemId=workspace]');
        var grid = workspace.down('[itemId=previewgrid]');
        if (grid != null) {
            for (var i = 0; i < grid.getColumns().length; i++) {
                var column = grid.getColumns()[i];
                if (column.dataIndex == e.record.data.source) {
                    column.setText(e.record.data.target == "" ? e.record.data.source : e.record.data.target);
                }
            }
        } else {
            var targetFieldstore = this.getStore('TemplateStore').data.items;
            // 修改的字段坐标
            var index = parseInt(e.node.innerText.split('	')[0]) - 1;
            var workspace = editor.grid.up('[itemId=workspace]');
            var fieldstore = workspace.down('[itemId=fieldgrid]').getStore().data.items;
            for (var i = 0; i < targetFieldstore.length; i++) {
                if (targetFieldstore[i].data.fieldname == e.value) {
                    fieldstore[index].data.targetFieldCode = targetFieldstore[i].data.fieldcode;
                    return;
                }
            }
        }
    },

    //判断节点是否为机构
    getOrganid: function (nodeid) {
        var organid;
        Ext.Ajax.request({
            url: '/import/getOgranid',
            async: false,
            params: {
                nodeid: nodeid
            },
            success: function (response) {
                organid = Ext.decode(response.responseText).data;
            }
        });
        return organid;
    },

    /**
     * 完成源数据文件选择和目的数据节点选择
     * 解析文件格式，进行字段设置和导入预览
     * @param form
     */
    submit: function (form) {
        form.getForm().submit({
            url: '/import/uploadOpenData',
            waitTitle: '提示',
            waitMsg: '请稍后，正在解析数据格式...',
            scope: this,
            params: {
                systype: 'Import'
            },
            success: function (basic, action) {
                var result =  Ext.decode(action.result.data)
                //1.提交成功后，刷新字段设置
                var head = result.header;
                var zipPath = result.UnZipPath;
                var filePath = result.fileTransferPath;
                var wg11Index = result.wg11Index;
                if (zipPath != null) {
                    UnZipPath = zipPath;
                } else if (filePath != null) {
                    UnZipPath = filePath;
                }
                //对xml进行限制
                if(result.fileSub=="Xml"&&result.rowCount>5000){
                    Ext.Msg.alert("提示", "提示：导入xml文件只支持导入1万条以内！");
                    return;
                }
                //进行文件最大行数判断
                if(result.rowCount>5000){
                    impCountMsg="导入数据量过大导致导入速度过慢，建议点击【取消】按钮，可等导入完成后再到“综合管理-数据查重”" +
                        "中进行档号查重的检查";
                }else {
                    impCountMsg="是否判断重复？";
                }
                var workspace = form.up('[itemId=workspace]');
                var importview = form.up('import');
                importview.wg11Index = wg11Index;
                var fieldstore = workspace.down('[itemId=fieldgrid]').getStore();
                fieldstore.removeAll();
                for (var i = 0; i < head.length; i++) {
                    fieldstore.add({source: head[i]});
                }
                //2.插入预览数据
                var column = [];
                var fields = [];
                for (var i = 0; i < head.length; i++) {
                    column.push({text: head[i], dataIndex: head[i]});
                    fields.push({name: head[i]});
                }
                var grid = workspace.down('[itemId=previewgrid]');
                var store = Ext.create('Ext.data.ArrayStore');
                store.setFields(fields);
                store.loadData(result. data);
                grid.reconfigure(store, column);
                //3.读取预置字段设置
                Ext.Ajax.request({
                    url: '/import/template/init',
                    params: {
                        //nodename:form.down('dataNodeComboView').value
                        nodeid: form.getForm().findField('target').getValue()
                    },
                    scope: this,
                    success: function (response, opts) {
                        var data = Ext.decode(response.responseText);
                        var templateStore = this.getStore('TemplateStore');
                        var templatemap = new Object();
                        for (var i = 0; i < templateStore.getCount(); i++) {
                            templatemap[templateStore.getAt(i).get('fieldcode')] = templateStore.getAt(i).get('fieldname');
                        }
                        for (var i = 0; i < fieldstore.getCount(); i++) {
                            for (var j = 0; j < data.length; j++) {
                                var target = data[j][fieldstore.getAt(i).get('source')]
                                if (target != undefined && templatemap[target] != undefined) {
                                    fieldstore.getAt(i).set('target', templatemap[target]);
                                }
                            }
                        }
                        for (var i = 0; i < grid.getColumns().length; i++) {
                            var column = grid.getColumns()[i];
                            for (var j = 0; j < fieldstore.getCount(); j++) {
                                if (column.dataIndex == fieldstore.getAt(j).get('source')) {
                                    column.setText(fieldstore.getAt(j).get('target') == "" ? fieldstore.getAt(j).get('source') : fieldstore.getAt(j).get('target'));
                                }
                            }
                        }
                    }
                });
            }
        });
    },
    /**
     * 开始执行导入
     * @param btn
     */
    impHandler: function (btn) {
        // var workspace = btn.up('[itemId=workspace]');
        var msgview = btn.up('importMsgView');
        var importview = msgview.importview;
        var taitanXml = importview.down('[itemId=taitanXml]');
        var socialSecurityXml = importview.down('[itemId=socialSecurityXml]');
        var wg11Index = importview.wg11Index;
        var radios = document.getElementsByName("type");
        var temp = ['NO', 'OK'];
        var impType = "";
        for (var i = 0; i < radios.length; i++) {
            if (radios[i].checked == true) {
                impType = temp[i];
            }
        }
        if (impType == "") {
            XD.msg('请选择判断操作！');
            return;
        }
        msgview.impType = impType;
        var workspace = msgview.preview;
        var store = workspace.down('[itemId=fieldgrid]').getStore();
        var jsonString = '';
        for (var i = 0; i < store.getCount(); i++) {
            var temp = Ext.encode(store.getAt(i).data);
            if (i == 0) jsonString += '[';
            jsonString += temp;
            if (i == store.getCount() - 1) {
                jsonString += ']';
            } else {
                jsonString += ',';
            }
        }
        var basicform = workspace.down('form').getForm();
        var target = basicform.findField('target').getValue();
        var file = basicform.findField('source').getValue();
        var fileName = file.substring(file.lastIndexOf("\\") + 1);
        var myMask = new Ext.LoadMask({msg: '正在导入数据...', target: workspace});
        var fPath = UnZipPath;
        if (target == "" || fileName == "") {
            Ext.Msg.alert("提示", "请确认节点数据和源文件是否正确！");
            return;
        }
        var suffix = fileName.substring(fileName.indexOf(".") + 1).toLowerCase();
        if ((suffix == "xml" || suffix == "xls" || suffix == "xlsx" || suffix == "zip")) {
            msgview.close();
            myMask.show();
            Ext.Ajax.setTimeout(3600000);
            Ext.Ajax.request({
                method: 'post',
                url: '/import/import',
                params: {
                    filePath: fPath,
                    fields: jsonString,
                    target: target,
                    filename: fileName,
                    isRepeat: impType,
                    taitanXml:taitanXml.getValue(),
                    socialSecurityXml:socialSecurityXml.getValue(),
                    wg11Index:wg11Index
                },
                scope: this,
                success: function (response, opts) {
                    myMask.hide();
                    //导入完成后刷新文件上传框
                    basicform.findField('source').setValue("");
                    //导入完成后删除上传文件
                    Ext.Ajax.request({
                        method: 'post',
                        url: '/import/deletUploadFile',
                        params: {
                            filePath: fPath
                        }
                    });
                    //
                    var rep = Ext.decode(response.responseText);
                    var erroMessage = rep.erroMessage;
                    if (erroMessage != "") {
                        Ext.MessageBox.confirm('提示', erroMessage);
                        return;
                    }
                    if (rep.error > 0) {
                        Ext.MessageBox.confirm('提示', '源数据文件共包含[' + rep.num + ']条数据，其中成功导入['
                            + (rep.num - rep.error) + ']条，失败[' + rep.error + ']条。点击确定后下载失败文件！',
                            function (btn, text) {
                                if (btn == 'yes') {
                                    var downForm = document.createElement('form');
                                    downForm.className = 'x-hidden';
                                    downForm.method = 'post';
                                    downForm.action = '/import/downloadImportFailure';
                                    var data = document.createElement('input');
                                    data.type = 'hidden';
                                    data.name = 'file';
                                    data.value = rep.errorfile;
                                    downForm.appendChild(data);
                                    document.body.appendChild(downForm);
                                    downForm.submit();
                                } else {
                                    Ext.Ajax.request({
                                        method: 'post',
                                        url: '/import/deleteFailureFile',
                                        params: {
                                            confirm: 'confirm'
                                        }
                                    });
                                }
                            }, this);
                    } else {
                        Ext.MessageBox.alert('提示', '源数据文件共包含[' + rep.num + ']条数据，成功导入['
                            + (rep.num - rep.error) + ']条', function(){
                            var fieldstore=importview.down('[itemId=fieldgrid]').getStore();
                            fieldstore.removeAll();
                            importview.up('window').hide();
                            Ext.Ajax.request({
                                method: 'post',
                                url: '/datareceive/deletOpenFile',
                                params: {fileName: fileName},
                                success: function (response, opts) {
                                    importview.grid.initGrid();
                                }
                            });
                        });
                    }
                    Ext.Ajax.request({
                        method: 'post',
                        url: '/import/deletUploadFile',
                        params: {filePath: UnZipPath}
                    });
                },
                failure: function (response, opts) {
                    myMask.hide();
                    var rep = Ext.decode(response.responseText);
                    Ext.Msg.alert(rep);
                }
            });
        } else {
            Ext.Msg.alert("提示", "只支持xml,excel,zip格式文件！");
            return;
        }
    },
    deleteHandler: function (btn) {
        var grid = btn.up('datareceivedGridView');
        var record = grid.selModel.getSelection();
        if(record.length == 0){
            XD.msg('请选择需要删除的数据');
            return;
        }
        var names = [];
        for (var i = 0; i < record.length; i++) {
            names.push(record[i].get('filename'));
        }
        var filenames = names.join(",");
        XD.confirm('确定要删除这'+names.length+'条数据吗?', function () {
            Ext.MessageBox.wait('正在删除请稍后...', '提示');
            Ext.Ajax.request({
                method: 'post',
                url: '/datareceive/deletReleasedFile',
                params: {
                    filenames:filenames
                },
                success: function (response, opts) {
                    XD.msg('删除成功');
                    grid.initGrid();
                    Ext.MessageBox.hide();
                },
                failure: function (response, opts) {
                    XD.msg('操作失败');
                    Ext.MessageBox.hide();
                }
            });
        },this);
    },
    thematicDeleteHandler: function (btn) {
        var grid = btn.up('datareceiveThematicAlreadyGridView');
        var record = grid.selModel.getSelection();
        if(record.length == 0){
            XD.msg('请选择需要删除的数据');
            return;
        }
        var names = [];
        for (var i = 0; i < record.length; i++) {
            names.push(record[i].get('filename'));
        }
        var filenames = names.join(",");
        XD.confirm('确定要删除这'+names.length+'条数据吗?', function () {
            Ext.MessageBox.wait('正在删除请稍后...', '提示');
            Ext.Ajax.request({
                method: 'post',
                url: '/datareceive/deletThematicFile',
                params: {
                    filenames:filenames
                },
                success: function (response, opts) {
                    XD.msg('删除成功');
                    grid.initGrid();
                    Ext.MessageBox.hide();
                },
                failure: function (response, opts) {
                    XD.msg('操作失败');
                    Ext.MessageBox.hide();
                }
            });
        },this);
    },
    implementHandler: function (btn) {
        var docGrid = btn.up('datareceiveOpenGridView');
        var record = docGrid.getSelectionModel().getSelection();
        if(record.length<1){
            XD.msg('请至少选择一条需要验证的记录');
            return;
        }
        var ids = [];
        Ext.each(record,function(){
            ids.push(this.get('receiveid').trim());
        });
        var receiveids = ids.join(",");
        XD.confirm('确定要执行验证这' + ids.length + '条数据吗', function () {
            Ext.MessageBox.wait('正在进行数据包安全认证...', '提示');
            Ext.Ajax.request({
                method: 'post',
                url: '/datareceive/verification',
                params: {
                    receiveids:receiveids
                },
                success: function (response, opts) {
                    Ext.MessageBox.hide();
                    var respText = Ext.decode(response.responseText);
                    XD.msg(respText.msg);
                },
                failure: function (response, opts) {
                    Ext.MessageBox.hide();
                    XD.msg('操作失败');
                }
            });
        },this);
    },
    lookResultHandler: function (btn) {
        var docGrid = btn.up('datareceiveOpenGridView');
        var record = docGrid.getSelectionModel().getSelection();
        if(record.length == 0){
            XD.msg('请选择一条需要查看的数据');
            return;
        }
        if(record.length >1){
            XD.msg('只能选择一条需要查看的数据');
            return;
        }
        var win = Ext.create('Ext.window.Window',{
            width:'80%',
            height:'80%',
            title: '已接收数据包',
            draggable : false,//禁止拖动
            resizable : false,//禁止缩放
            modal:true,
            closeToolText:'关闭',
            closeAction:'hide',
            layout:'fit',
            items:[{
                xtype: 'datareceivedResultGridView'
            }]
        });
        var grid = win.down('datareceivedResultGridView');
        grid.initGrid({receiveid:record[0].get('receiveid')});
        win.show();
    },
    printHandler: function (btn) {
        var docGrid;
        if(btn.up('datareceiveOpenGridView')){
            docGrid = btn.up('datareceiveOpenGridView');
        }else{
            docGrid = btn.up('datareceiveThematicGridView')
        }
        var record = docGrid.getSelectionModel().getSelection();
        if(record.length<1){
            XD.msg('请选择需要打印的记录');
            return;
        }
        var ids = [];
        var params = {};
        Ext.each(record,function(){
            ids.push(this.get('receiveid').trim());
        });
        if(reportServer == 'UReport') {
            params['receiveid'] = ids.join(",");
            XD.UReportPrint(null, '接收单据管理', params);
        } else if(reportServer == 'FReport'){
            XD.FRprint(null, '接收单据管理', ids.length > 0 ? "'docid':'" + ids.join(",")+"'": '');
        }
    },
    lookdetailsHandler: function (btn) {
        var grid = btn.up('datareceivedResultGridView');
        var record = grid.selModel.getSelection();
        if (record.length == 0) {
            XD.msg('请选择一条需要查看的数据');
            return;
        }
        if (record.length > 1) {
            XD.msg('只能选择一条数据');
            return;
        }
        if (record[0].data.authenticity.indexOf("未检测") > -1 || record[0].data.authenticity == '') {
            XD.msg('该记录未检测，无法查看检测明细！');
            return;
        }
        var win = Ext.create('Datareceive.view.DatareceivedResultDetailView');
        win.down('[itemId=closeBtn]').on('click', function () {
            win.close()
        });
        win.down('[itemId=authenticity]').html = record[0].data.authenticity;
        win.down('[itemId=integrity]').html = record[0].data.integrity;
        win.down('[itemId=usability]').html = record[0].data.usability;
        win.down('[itemId=safety]').html = record[0].data.safety;
        win.show();
    }
});