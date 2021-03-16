/**
 * 数据导入控制器
 * Created by Rong on 2018/10/9.
 */
var UnZipPath = "";
var impCountMsg="";
var codeSetValues;
Ext.define('Inware.controller.ImportController', {
    extend: 'Ext.app.Controller',
    views: ['ImportView', 'DataNodeComboView','ImportMsgView','ImportResultPreviewGrid','ImportSetCodeView'],
    stores: ['ImportGridStore', 'TemplateStore'],
    models: ['ImportGridModel'],

    init: function () {
        this.control({
            'treelist': {
                selectionchange: this.systemChange
            },
            'filefield': {
                change: this.fileSelected
            },
            'dataNodeComboView': {
                change: this.nodeSelected
            },
            '[itemId=fieldgrid]': {
                edit: this.edit
            },
            '[itemId=impBtn]': {
                click: function (btn) {
                    var source = btn.up('import').down('filefield').getValue();
                    var target = btn.up('import').down('dataNodeComboView').getRawValue();
                    if (!source){
                        XD.msg('源文件不能为空');
                        return;
                    }
                    /*if (!target){
                        XD.msg('档案分类节点不能为空');
                        return;
                    }*/
                  XD.confirm('请确实数据无误后开始导入?',function () {
                      var workspace = btn.up('import');
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
                      if (fileName == "") {
                          Ext.Msg.alert("提示", "请确认源文件是否正确！");
                          return;
                      }
                      var suffix = fileName.substring(fileName.indexOf(".") + 1).toLowerCase();
                      if ((suffix == "xml" || suffix == "xls" || suffix == "xlsx" || suffix == "zip")) {
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
                                  isRepeat: 'OK',//默认是覆盖
                                  isEntityStorage:true
                              },
                              success: function (response, opts) {
                                  myMask.hide();
                                  //导入完成后刷新文件上传框
                                  basicform.findField('source').setRawValue("");
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
                                          + (rep.num - rep.error) + ']条');
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
                  })
                }
            },
            '[itemId=impTempBtn]': {
                click: function (btn) {
                    var source = btn.up('import').down('filefield').getValue();
                    var target = btn.up('import').down('dataNodeComboView').getRawValue();
                    if (!source){
                        XD.msg('源文件不能为空');
                        return;
                    }
                    /*if (!target){
                     XD.msg('档案分类节点不能为空');
                     return;
                     }*/
                    XD.confirm('请确实数据无误后开始导入?',function () {
                        var workspace = btn.up('import');
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
                        if (fileName == "") {
                            Ext.Msg.alert("提示", "请确认源文件是否正确！");
                            return;
                        }
                        var suffix = fileName.substring(fileName.indexOf(".") + 1).toLowerCase();
                        if ((suffix == "xml" || suffix == "xls" || suffix == "xlsx" || suffix == "zip")) {
                            myMask.show();
                            Ext.Ajax.setTimeout(3600000);
                            Ext.Ajax.request({
                                method: 'post',
                                url: '/import/importKf',
                                params: {
                                    filePath: fPath,
                                    fields: jsonString,
                                    target: target,
                                    filename: fileName,
                                    isRepeat: 'OK',//默认是覆盖
                                    isEntityStorage:true
                                },
                                success: function (response, opts) {
                                    myMask.hide();
                                    //导入完成后刷新文件上传框
                                    basicform.findField('source').setRawValue("");
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
                                            + (rep.num - rep.error) + ']条');
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
                            Ext.Msg.alert("提示", "只支持excel格式文件！");
                            return;
                        }
                    })
                }
            },
            'importMsgView button[itemId="import"]': {
                click: this.impHandler
            },'wareFormView [itemId=exportBtn]':{
                click:this.afterrender
            },'import [itemId=back]':{//关闭导入窗口
                click:function (btn) {
                    btn.up('[itemId=impWind]').close();
                }
            },'[itemId=impDealBtn]':{//导入后的数据处理
                click:function (btn) {
                    //btn.up('[itemId=impWind]').close();
                    var importResultPreviewWin = Ext.create('Ext.window.Window', {
                        width: '100%',
                        height: '100%',
                        title: '入库预览',
                        draggable: true,//可拖动
                        resizable: false,//禁止缩放
                        modal: true,
                        closeAction: 'hide',
                        closeToolText: '关闭',
                        layout: 'fit',
                        itemId:'importResultPreviewWin',
                        items: [{
                            xtype: 'importResultPreviewGrid',
                        }]
                    });
                    var resultPreviewGrid = importResultPreviewWin.down('importResultPreviewGrid');
                    importResultPreviewWin.show();
                    resultPreviewGrid.initGrid();

                }
            },'wareFormView [itemId=exportTemplateBtn]':{
                click:function (btn) {
                    var nodeid = "publicNode";
                    var reqUrl="/export/downloadFieldTemp?nodeid="+nodeid+"&isEntryStorage=true";
                    window.location.href=reqUrl;
                }
            },'importResultPreviewGrid [itemId=backBtn]':{//入库预览  返回
                click:function (btn) {
                    btn.up('[itemId=importResultPreviewWin]').close();
                }
            },'importResultPreviewGrid [itemId=checkImportBtn]':{//入库预览  入库信息匹配
                click:function (btn) {
                    if(!codeSetValues){
                        XD.msg('前先设置匹配字段');
                        return;
                    }
                    var gridView=btn.up('importResultPreviewGrid');
                    var store=gridView.getStore();
                    Ext.MessageBox.wait('正在处理请稍后...');
                    Ext.Ajax.request({
                        url: '/import/kfCheck',
                        params: {
                            codeSetValues: codeSetValues
                        },
                        method: 'POST',
                        success: function (response) {
                            Ext.MessageBox.hide();
                            var respText = Ext.decode(response.responseText);
                            var sumstr=respText.msg;
                            XD.msg('匹配完成！'+sumstr);
                            window.checkMsg=sumstr;
                            window.isCheck=true;//标记已检查
                            window.hasExcel=1;//标记异常信息已生成
                            window.txtResultLabelId.setText('');//清空结果集描述
                            store.proxy.extraParams.resultType = '';
                            store.reload();
                        },
                        failure: function () {
                            Ext.MessageBox.hide();
                            XD.msg('操作失败');
                        }
                    });
                }
            },'importResultPreviewGrid [itemId=setCodeBtn]':{//入库预览  入库匹配字段设置
                click:function (btn) {
                    var gridView=btn.up('importResultPreviewGrid');
                    var columns=gridView.columnManager.columns;
                    var stores;//保存字段
                    stores={fields:["text","value"],data:[]};
                    for(var i=1;i<columns.length;i++){//重新添加下拉多选框的选择项
                        if(columns[i].text&&columns[i].dataIndex&&columns[i].dataIndex!='eleid'){
                            stores.data.push({"text": columns[i].text,"value":columns[i].dataIndex});
                        }
                    }
                    var importSetCodeView = Ext.create('Inware.view.ImportSetCodeView');
                    importSetCodeView.down('tagfield').setStore(stores);//更新字段
                    window.labelText=gridView.down('[itemId=txtLabelId]');//匹配字段描述
                    window.txtResultLabelId=gridView.down('[itemId=txtResultLabelId]');//匹配结果集
                    importSetCodeView.show();
                }
            },'importResultPreviewGrid [itemId=doImportBtn]':{//入库预览  执行入库
                click:function (btn) {
                    if(!codeSetValues){
                        XD.msg('前先设置匹配字段');
                        return;
                    }
                    if(!window.isCheck){
                        XD.msg('请先执行入库信息匹配');
                        return;
                    }
                    Ext.MessageBox.wait('正在对可以进行入库的匹配结果集进行入库操作...');
                    Ext.Ajax.request({
                        url: '/import/importCheck',
                        params: {
                            codeSetValues: codeSetValues
                        },
                        method: 'POST',
                        success: function (response) {
                            Ext.MessageBox.hide();
                            var respText = Ext.decode(response.responseText);
                            XD.msg(respText.msg);
                            window.txtResultLabelId.setText('');//清空结果集描述
                        },
                        failure: function () {
                            Ext.MessageBox.hide();
                            XD.msg('操作失败');
                        }
                    });
                }
            },'importResultPreviewGrid [itemId=exportBtn]':{//入库预览  入库失败信息导出
                click:function (btn) {
                    if(!codeSetValues){
                        XD.msg('前先设置匹配字段');
                        return;
                    }
                    if(!window.isCheck){
                        XD.msg('请先执行入库信息匹配');
                        return;
                    }
                    if(window.hasExcel==0){
                        XD.msg('失败信息的文件已清空，请执行入库信息匹配重新生成后再下载');
                        return;
                    }
                    //Ext.MessageBox.wait('正在对入库异常结果集进行导出操作...');
                    var downForm = document.createElement('form');
                    downForm.className = 'x-hidden';
                    downForm.method = 'post';
                    downForm.action = '/import/downloadImportFailure';
                    var data = document.createElement('input');
                    data.type = 'hidden';
                    data.name = 'file';
                    data.value = '';
                    downForm.appendChild(data);
                    document.body.appendChild(downForm);
                    downForm.submit();
                    window.hasExcel=0;//标记异常信息已下载（会删除服务器上的原文件）
                }
            },'importResultPreviewGrid [itemId=notFundID]':{//匹配结果查询  没匹配到相关条目
                click:function (btn) {
                    this.resultReload(btn,1,"没匹配到相关条目");
                }
            },'importResultPreviewGrid [itemId=moreFundID]':{//匹配结果查询 匹配到多条条目
                click:function (btn) {
                    this.resultReload(btn,2,"匹配到多条条目");
                }
            },'importResultPreviewGrid [itemId=notDetailID]':{//匹配结果查询  存储位置不够详细
                click:function (btn) {
                    this.resultReload(btn,3,"存储位置不够详细");
                }
            },'importResultPreviewGrid [itemId=noSpaceID]':{//匹配结果查询  放入密集架空间不足
                click:function (btn) {
                    this.resultReload(btn,4,"放入密集架空间不足");
                }
            },'importResultPreviewGrid [itemId=inStorageID]':{//匹配结果查询  已入库
                click:function (btn) {
                    this.resultReload(btn,5," 已入库");
                }
            },'importResultPreviewGrid [itemId=noZoneID]':{//匹配结果查询  存储位置没有匹配到
                click:function (btn) {
                    this.resultReload(btn,6,"存储位置没有匹配到");
                }
            },'importResultPreviewGrid [itemId=nullID]':{//匹配结果查询  存储位置信息为空
                click:function (btn) {
                    this.resultReload(btn,7,"存储位置信息为空");
                }
            },'importResultPreviewGrid [itemId=successId]':{//匹配结果查询  可以进行入库
                click:function (btn) {
                    this.resultReload(btn,8,"可以进行入库");
                }
            },'importResultPreviewGrid [itemId=allID]':{//匹配结果查询  显示所有
                click:function (btn) {
                    if(window.checkMsg){//显示匹配统计结果
                        XD.msg(window.checkMsg);
                    }
                    this.resultReload(btn,'',"全部");
                }
            },'importSetCodeView [itemId=setCode]':{//入库匹配字段设置  确定
                click:function (btn) {
                    var setCodeView=btn.up('importSetCodeView');
                    var tagfield=setCodeView.down('tagfield');
                    var values=tagfield.lastValue;
                    var items=tagfield.valueStore.data.items;
                    if(values.length==0){
                       XD.msg('前先选择匹配字段');
                       return;
                    }
                    //获取相关的字段设置到js全局变量
                    var ppTxt='';//匹配字段内容
                    for(var i=0;i<values.length;i++){
                        if(i==0){
                            codeSetValues=values[i];
                            ppTxt=items[i].data.text;
                        }else{
                            codeSetValues+=','+values[i];
                            ppTxt+=','+items[i].data.text;
                        }
                    }
                    setCodeView.close();
                    //设置匹配字段内容的页面显示
                    window.labelText.setText('当前匹配字段设置: '+ppTxt+" 。");
                }
            }
        });
    },

    resultReload:function(btn,type,resultTxt){//匹配结果按条件刷新
        if(!codeSetValues){
            XD.msg('前先设置匹配字段');
            return;
        }
        if(!window.isCheck){
            XD.msg('请先执行入库信息匹配');
            return;
        }
        var gridView=btn.up('importResultPreviewGrid');
        window.txtResultLabelId.setText('匹配结果: '+resultTxt+" 。");
        var store = gridView.getStore();
        store.proxy.extraParams.resultType = type;
        store.reload();
    },

    /**
     * 默认选中第一个节点
     * @param panel 左边树容器Panel
     */
    afterrender: function (panel) {
        var reportGridWin = Ext.create('Ext.window.Window', {
            width: '100%',
            height: '100%',
            header: false,
            draggable: false,//禁止拖动
            resizable: false,//禁止缩放
            modal: true,
            closeToolText: '关闭',
            layout: 'fit',
            itemId:'impWind',
            items: [{
                xtype: 'import'
            }]
        });
        var reportGrid = reportGridWin.down('import');
        //reportGrid.initGrid({nodeid:reportGrid.nodeid + ',公有报表'});
        reportGridWin.show();
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
        var source = form.down('filefield');
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
     * 选中源数据包
     * @param field
     * @param value
     */
    fileSelected: function (field, value) {
        //如果源数据包和目的节点都有值，则提交表单
        var form = field.up('form');
        var target = form.down('dataNodeComboView');
        /*var nodeid = form.getForm().findField('target').getValue();
        var flag = this.getOrganid(nodeid);
        if (!flag) {
            XD.msg('当前选的节点不是机构节点');
            return;
        }
        if (target.getValue() != null) {
            this.submit(form);
        }*/
        this.submit(form);
    },

    /**
     * 完成源数据文件选择和目的数据节点选择
     * 解析文件格式，进行字段设置和导入预览
     * @param form
     */
    submit: function (form) {
        form.getForm().submit({
            url: '/import/upload',
            waitTitle: '提示',
            waitMsg: '请稍后，正在解析数据格式...',
            scope: this,
            params: {
                systype: 'Import'
            },
            success: function (basic, action) {
                //1.提交成功后，刷新字段设置
                var head = action.result.header;
                var zipPath = action.result.UnZipPath;
                var filePath = action.result.fileTransferPath;
                if (zipPath != null) {
                    UnZipPath = zipPath;
                } else if (filePath != null) {
                    UnZipPath = filePath;
                }
                //对xml进行限制
                if(action.result.fileSub=="Xml"&&action.result.rowCount>5000){
                    Ext.Msg.alert("提示", "提示：导入xml文件只支持导入1万条以内！");
                    return;
                }
                //进行文件最大行数判断
                if(action.result.rowCount>5000){
                    impCountMsg="导入数据量过大导致导入速度过慢，建议点击【取消】按钮，可等导入完成后再到“综合管理-数据查重”" +
                        "中进行档号查重的检查";
                }else {
                    impCountMsg="是否判断重复？";
                }
                var workspace = form.up('[itemId=workspace]');
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
                store.loadData(action.result.data);
                grid.reconfigure(store, column);
                //3.读取预置字段设置
                Ext.Ajax.request({
                    url: '/import/template/init',
                    params: {
                        //nodename:form.down('dataNodeComboView').value
                        isEntryStorage:true,
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

    /**
     * 开始执行导入
     * @param btn
     */
    impHandler: function (btn) {
        // var workspace = btn.up('[itemId=workspace]');
        var msgview = btn.up('importMsgView');
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
                    isRepeat: 'OK',
                    isEntityStorage:true
                },
                success: function (response, opts) {
                    myMask.hide();
                    //导入完成后刷新文件上传框
                    basicform.findField('source').setRawValue("");
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
                            + (rep.num - rep.error) + ']条');
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
    }
});