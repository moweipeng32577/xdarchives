/**
 * Created by SunK on 2018/7/31 0031.
 */
var exportState = "";
var entryids = "";
var NodeIdf = "";
var exportSelectAll = "";
var userFieldCode = "";
var tempParams;
Ext.define('Export.controller.ExportController', {
    extend: 'Ext.app.Controller',
    views: [
        'ExportGridView',
        'ExportFormAndGridView',
        'ExportTopView',
        'ExportGroupSetView', 'ExportMessageView'
    ],
    models: ['ExportModel', 'ExportGroupSetModel'],
    stores: ['ExportStore', 'ExportGroupSetStore'],

    init: function () {
        this.control({
            'exportFormAndGrid [itemId=treepanelId]': {
                select: function (treemodel, record) {
                    var gridcard = this.findView(treemodel.view).down('[itemId=gridcard]');
                    var onlygrid = gridcard.down('[itemId=onlygrid]');
                    var pairgrid = gridcard.down('[itemId=pairgrid]');
                    var grid;
                    var nodeType = record.data.nodeType;
                    var bgSelectOrgan = gridcard.down('[itemId=bgSelectOrgan]');
                    //树节点为分类，更改右边页面为“请选择机构节点”
                    if (nodeType == 2) {
                        gridcard.setActiveItem(bgSelectOrgan);
                    } else {
                        if (record.data.classlevel == 2) {
                            gridcard.setActiveItem(pairgrid);
                            var ajgrid = pairgrid.down('[itemId=northgrid]');
                            ajgrid.setTitle("当前位置：" + record.data.text);
                            var jngrid = pairgrid.down('[itemId=southgrid]');
                            jngrid.setTitle("查看卷内");
                            if (jngrid.expandOrcollapse == 'expand') {
                                jngrid.expand();
                            } else {
                                jngrid.collapse();
                            }
                            jngrid.dataUrl = '/management/entries/innerfile/' + '' + '/';
                            jngrid.initGrid(this.getNodeid(record.get('nodeid')));
                            grid = ajgrid;
                        } else {
                            gridcard.setActiveItem(onlygrid);
                            onlygrid.setTitle("当前位置：" + record.data.text);
                            grid = onlygrid;
                        }
                        var gridview = gridcard.up('exportFormAndGrid').down('exportgrid');
                        gridview.setTitle("当前位置：" + record.data.text);//将表单与表格视图标题改成当前位置

                        grid.nodeid = record.get('fnid');
                        grid.initGrid({nodeid: record.get('fnid')});
                        NodeIdf = record.get('fnid');
                        var demoStore = Ext.getStore('ExportGroupSetStore');
                        demoStore.proxy.extraParams.fieldNodeid = NodeIdf;
                    }
                }
            },
            'exportFormAndGrid [itemId=northgrid]': {
                itemclick: this.itemclickHandler
            },
            'exportgrid [itemId=Excel]': {
                click: this.chooseFieldExportExcel
            },
            'exportgrid [itemId=Xml]': {
                click: this.chooseFieldExportXml
            },
            'exportgrid [itemId=ExcleAndElectronic]': {
                click: this.chooseFieldExportExcelAndFile
            },
            'exportgrid [itemId=XmlAndElectronic]': {
                click: this.chooseFieldExportXmlAndFile
            },
            'exportgrid [itemId=FieldTemp]': {
                click: this.downloadFieldTemp
            },
            'exportGroupSetView button[itemId="close"]': {
                click: function (view) {
                    view.findParentByType('exportGroupSetView').close();
                }
            },
            'ExportMessage button[itemId="cancelExport"]': {
                click: function (view) {
                    view.findParentByType('ExportMessage').close();
                }
            },
            'ExportMessage button[itemId="SaveExport"]': {//导出
                click: function (view) {
                    var exportMessageView = view.up('ExportMessage');
                    var fileName = exportMessageView.down('[itemId=userFileName]').getValue();
                    var zipPassword = exportMessageView.down('[itemId=zipPassword]').getValue();
                    var b = exportMessageView.down('[itemId=addZipKey]').checked
                    var form = exportMessageView.down('[itemId=form]')
                    tempParams['fileName'] = fileName;
                    tempParams['zipPassword'] = zipPassword;
                    tempParams['userFieldCode'] = userFieldCode;
                    if (fileName != null && fileName != "请输入..." && fileName != "") {
                        var pattern = new RegExp("[/:*?\"<>|]");
                        if (pattern.test(fileName) || fileName.indexOf('\\') > -1) {
                            XD.msg("文件名称不能包含下列任何字符：\\/:*?\"<>|");
                            return;
                        }
                        if (zipPassword == "" && b) {
                            XD.msg("zip压缩密码不能为空");
                            return;
                        }
                        if(tempParams.exportState=="Xml"&&tempParams.indexLength>10000){
                            Ext.Msg.alert("提示", "提示：导出xml文件只支持导入1万条以内！");
                            return;
                        }
                        Ext.MessageBox.wait('正在处理请稍后...')
                        Ext.Ajax.request({
                            method: 'post',
                            url: '/export/chooseFieldExport',
                            timeout: XD.timeout,
                            scope: this,
                            async: true,
                            params: tempParams,
                            /*{
                                Nodeids: NodeIdf,
                                fileName: fileName,
                                selectAll: exportSelectAll,
                                exportState: exportState,
                                zipPassword: zipPassword,
                                ids: entryids,
                                userFieldCode: userFieldCode
                            },*/
                            success: function (res) {
                                var obj = Ext.decode(res.responseText).data;
                                if (obj.fileSizeMsg == "NO") {
                                    XD.msg('原文总大小超出限制');
                                    Ext.MessageBox.hide()
                                    return;
                                }
                                if (obj.entrySizeMsg == "NO") {
                                    if (tempParams.exportState == "XmlAndFile" || tempParams.exportState == "ExcelAndFile") {
                                        XD.msg('条目数超出限制，一次支持支导出10万含原文的条目！');
                                    }
                                    if (tempParams.exportState == "Excel" || tempParams.exportState == "Xml") {
                                        XD.msg('条目数超出限制，一次只支持导出50w的条目！');
                                    }
                                    Ext.MessageBox.hide()
                                    return;
                                }
                                window.location.href = "/export/downloadZipFile?fpath=" + encodeURIComponent(obj.filePath)
                                Ext.MessageBox.hide()
                                XD.msg('文件生成成功，正在准备下载');
                                exportMessageView.close()
                            },
                            failure: function () {
                                Ext.MessageBox.hide()
                                XD.msg('文件生成失败');
                            }
                        });
                    } else {
                        XD.msg("文件名不能为空")
                    }
                }
            },
            'exportGroupSetView button[itemId="addAllOrNotAll"]': {
                click: function (view) {
                    var itemSelector = view.findParentByType('exportGroupSetView').down('itemselector');
                    if (view.getText() == '全选') {
                        var fromList = itemSelector.fromField.boundList,
                            allRec = fromList.getStore().getRange();
                        fromList.getStore().remove(allRec);
                        itemSelector.toField.boundList.getStore().add(allRec);
                        itemSelector.syncValue();//
                        view.setText('取消全选');
                    } else {
                        var toList = itemSelector.toField.boundList,
                            allRec = toList.getStore().getRange();
                        toList.getStore().remove(allRec);
                        itemSelector.fromField.boundList.getStore().add(allRec);
                        itemSelector.syncValue();
                        view.setText('全选');
                    }
                }
            },
            'exportGroupSetView button[itemId="save"]': {
                click: this.chooseSave
            }
        })
    },

    findView: function (btn) {
        return btn.up('exportFormAndGrid');
    },
    findTreeView: function (btn) {
        return btn.up('exportFormAndGrid').down('treepanel');
    },
    findInnerGrid: function (btn) {
        return this.findView(btn).down('[itemId=southgrid]');
    },
    //获取列表界面视图
    findGridView: function (btn) {
        return this.findView(btn).getComponent('gridview');
    },
    getNodeid: function (parentid) {
        var nodeid;
        var params = {};
        if (typeof(parentid) != 'undefined' && parentid != '') {
            params.parentid = parentid;
        }
        Ext.Ajax.request({
            url: '/publicUtil/getNodeid',
            async: false,
            params: params,
            success: function (response) {
                nodeid = Ext.decode(response.responseText).data;
            }
        });
        return nodeid;
    },

    itemclickHandler: function (view, record, item, index, e) {
        var fileArchivecode = record.get('archivecode');//案卷档号
        //用于案卷点击显示卷内文件条目，之前是根据案卷档号匹配，改为通过entryid获取档号设置，通过档号设置字段匹配
        var entryid = record.get('entryid');
        window.fileArchivecode = fileArchivecode;
        var southgrid = this.findInnerGrid(view);
        southgrid.dataUrl = '/management/entries/innerfile/' + entryid + '/';
        var nodeid = this.getNodeid(record.get('nodeid'));
        southgrid.initGrid({nodeid: nodeid});
        southgrid.setTitle('查看' + fileArchivecode + '案卷的卷内');
    },

    //导出xml
    exportXml: function (btn) {
        var filenames = "";
        var isbtn = "";
        var tree = this.findGridView(btn).down('treepanel');
        var nodeid = tree.selModel.getSelected().items[0].get('fnid');
        var grid = btn.up('exportgrid');
        var ids = [];
        var selectAll = grid.down('[itemId=selectAll]').checked;
        var pattern = new RegExp("[/:*?\"<>|]");
        Ext.each(grid.getSelectionModel().getSelection(), function () {
            ids.push(this.get('entryid'));
        });

        if (ids.length == 0 && selectAll == false) {
            XD.msg('请至少选择一条需要导出的数据');
            return;
        }
        var selectAll = grid.down('[itemId=selectAll]').checked;
        Ext.MessageBox.prompt("输入导出后的文件名", "文件名", function (btn2, text) {
            filenames = text;
            isbtn = btn2
            if (isbtn == "ok") {
                if (filenames) {
                    if (pattern.test(filenames) || filenames.indexOf('\\') > -1) {
                        XD.msg("文件名称不能包含下列任何字符：\\/:*?\"<>|");
                        return;
                    }

                    Ext.Ajax.request({
                        async: false,
                        method: 'POST',
                        params: {
                            ids: ids,
                            nodeId: nodeid,
                            fileName: filenames,
                            selectAll: selectAll
                        },
                        url: '/export/exportParameter',
                        success: function () {
                        }
                    });
                    var nw = window.open("/export/exportXml");
                    nw.document.title = '正在努力下载文件中.....';
                } else {
                    XD.msg('文件名不能为空');
                    return;
                }
            }
        });
    },

    //导出excel和原文
    exportExcelAndElectronic: function (btn) {
        var filenames = "";
        var isbtn = "";
        var tree = this.findGridView(btn).down('treepanel');
        var nodeid = tree.selModel.getSelected().items[0].get('fnid');
        var grid = btn.up('exportgrid');
        var ids = [];
        var selectAll = grid.down('[itemId=selectAll]').checked;

        var pattern = new RegExp("[/:*?\"<>|]");
        Ext.each(grid.getSelectionModel().getSelection(), function () {
            ids.push(this.get('entryid'));
        });
        if (ids.length == 0 && selectAll == false) {
            XD.msg('请至少选择一条需要导出的数据');
            return;
        }
        Ext.MessageBox.prompt("输入导出后的文件名", "文件名", function (btn2, text) {
            filenames = text;
            isbtn = btn2
            if (isbtn == "ok") {
                if (filenames) {
                    if (pattern.test(filenames) || filenames.indexOf('\\') > -1) {
                        XD.msg("文件名称不能包含下列任何字符：\\/:*?\"<>|");
                        return;
                    }

                    Ext.Ajax.request({
                        async: false,//同步
                        method: 'POST',
                        params: {
                            ids: ids,
                            nodeId: nodeid,
                            fileName: filenames,
                            selectAll: selectAll
                        },
                        url: '/export/exportParameter',
                        success: function () {
                        }
                    });
                    var nw = window.open("/export/exporteExcelAndElectronic");
                    nw.document.title = '正在努力下载文件中.....';
                } else {
                    XD.msg('文件名不能为空');
                    return;
                }
            }
        });
    },
    //导出Xml和原文
    exportXmlAndElectronic: function (btn) {
        var filenames = "";
        var isbtn = "";
        var tree = this.findGridView(btn).down('treepanel');
        var nodeid = tree.selModel.getSelected().items[0].get('fnid');
        var grid = btn.up('exportgrid');
        var ids = [];
        var selectAll = grid.down('[itemId=selectAll]').checked;
        var pattern = new RegExp("[/:*?\"<>|]");
        Ext.each(grid.getSelectionModel().getSelection(), function () {
            ids.push(this.get('entryid'));
        });

        if (ids.length == 0 && selectAll == false) {
            XD.msg('请至少选择一条需要导出的数据');
            return;
        }
        //var selectAll = grid.down('[itemId=selectAll]').checked;
        Ext.MessageBox.prompt("输入导出后的文件名", "文件名", function (btn2, text) {
            filenames = text;
            isbtn = btn2
            if (isbtn == "ok") {
                if (filenames) {
                    if (pattern.test(filenames) || filenames.indexOf('\\') > -1) {
                        XD.msg("文件名称不能包含下列任何字符：\\/:*?\"<>|");
                        return;
                    }

                    Ext.Msg.wait('正在进行导出，请耐心等候......', '正在操作');
                    Ext.Ajax.request({
                        async: true,
                        method: 'POST',
                        params: {
                            ids: ids,
                            nodeId: nodeid,
                            fileName: filenames,
                            selectAll: selectAll
                        },
                        url: '/export/exportParameter',
                        success: function (data) {
                            var nw = window.open('/export/exporteXmlAndElectronic');
                            nw.document.title = '正在努力下载文件中.....';
                            Ext.MessageBox.hide();
                        }
                    });
                } else {
                    XD.msg('文件名不能为空');
                    return;
                }
            }
        });
    },
    //导出excel
    exportExcel: function (btn) {
        var filenames = "";
        var isbtn = "";
        var tree = this.findGridView(btn).down('treepanel');
        var nodeid = tree.selModel.getSelected().items[0].get('fnid');
        var grid = btn.up('exportgrid');
        var ids = [];
        var selectAll = grid.down('[itemId=selectAll]').checked;
        var pattern = new RegExp("[/:*?\"<>|]");
        Ext.each(grid.getSelectionModel().getSelection(), function () {
            ids.push(this.get('entryid'));
        });
        if (ids.length == 0 && selectAll == false) {
            XD.msg('请至少选择一条需要导出的数据');
            return;
        }
        //var selectAll = grid.down('[itemId=selectAll]').checked;
        Ext.MessageBox.prompt("输入导出后的文件名", "文件名", function (btn2, text) {
            filenames = text;
            isbtn = btn2
            if (isbtn == "ok") {
                if (filenames) {
                    if (pattern.test(filenames) || filenames.indexOf('\\') > -1) {
                        XD.msg("文件名称不能包含下列任何字符：\\/:*?\"<>|");
                        return;
                    }

                    Ext.Ajax.request({
                        async: false,
                        method: 'POST',
                        params: {
                            ids: ids,
                            nodeId: nodeid,
                            fileName: filenames,
                            selectAll: selectAll
                        },
                        url: '/export/exportParameter',
                        success: function () {
                        }
                    });
                    var nw = window.open("/export/exportExcle");
                    nw.document.title = '正在努力下载文件中.....';
                } else {
                    XD.msg('文件名不能为空');
                    return;
                }
            }
        });
    }/*,
     //导出字段模板
     exportTemp:function(btn){
     var isbtn="";
     var tree = this.findGridView(btn).down('treepanel');
     var nodeid = tree.selModel.getSelected().items[0].get('fnid');

     var grid = btn.up('exportgrid');
     Ext.Msg.show({
     title: '提示',
     message: '点击"确认"下载当前节点字段模板',
     buttons: Ext.Msg.OKCANCEL,
     buttonText: {cancel: '取消', ok: '下载'},
     fn: function (btn) {
     if (btn === 'ok') {
     window.open("/export/exportTemp?nodeid="+nodeid)
     } else {

     }
     }
     });
     }*/
    ,
    //--------自选字段导出--s----//
    exportFunction: function (view, state) {
        exportState = "Excel";
        var userGridView = view.findParentByType('exportgrid');
        var selectAll = userGridView.down('[itemId=selectAll]').checked;
        var record = userGridView.getSelection();
        var isSelectAll = false;
        if (selectAll) {
            record = userGridView.acrossDeSelections;
            isSelectAll = true;
        }
        var tmp = [];
        for (var i = 0; i < record.length; i++) {
            tmp.push(record[i].get('entryid'));
        }
        var entryids = tmp.join(',');
        tempParams = userGridView.getStore().proxy.extraParams;
        tempParams['entryids'] = entryids;
        tempParams['isSelectAll'] = isSelectAll;
        tempParams['exportState'] = state;
        var gridStore=userGridView.getStore();
        tempParams['indexLength'] = gridStore.totalCount;
        if (selectAll == false && entryids.length == 0) {
            XD.msg('请至少选择一条需要导出的数据');
            return;
        }
        var selectItem = Ext.create("Export.view.ExportGroupSetView", {});
        selectItem.items.get(0).getStore().load({});
        selectItem.show();

    },
    /* chooseFieldExportXml: function (view) {
         exportState = "Xml";
         var userGridView = view.findParentByType('exportgrid');
         var select = userGridView.getSelection();
         var tree = this.findGridView(view).down('treepanel');
         var ids = [];
         var selectAll = userGridView.down('[itemId=selectAll]').checked;
         exportSelectAll = selectAll
         var pattern = new RegExp("[`*':'.<>/?*|‘”“'\\\\？\"\"]");
         Ext.each(userGridView.getSelectionModel().getSelection(), function () {
             ids.push(this.get('entryid'));
         });
         entryids = ids;
         if (ids.length == 0 && selectAll == false) {
             XD.msg('请至少选择一条需要导出的数据');
             return;
         } else {
             var selectItem = Ext.create("Export.view.ExportGroupSetView", {});
             selectItem.items.get(0).getStore().load({});
             selectItem.show();
         }
     },
     chooseFieldExportXmlAndFile: function (view) {
         exportState = "XmlAndFile";
         var userGridView = view.findParentByType('exportgrid');
         var select = userGridView.getSelection();
         var tree = this.findGridView(view).down('treepanel');
         var ids = [];
         var selectAll = userGridView.down('[itemId=selectAll]').checked;
         exportSelectAll = selectAll
         var pattern = new RegExp("[`*':'.<>/?*|‘”“'\\\\？\"\"]");
         Ext.each(userGridView.getSelectionModel().getSelection(), function () {
             ids.push(this.get('entryid'));
         });
         entryids = ids;
         if (ids.length == 0 && selectAll == false) {
             XD.msg('请至少选择一条需要导出的数据');
             return;
         } else {
             var selectItem = Ext.create("Export.view.ExportGroupSetView", {});
             selectItem.items.get(0).getStore().load({});
             selectItem.show();
         }
     },
     chooseFieldExportExcelAndFile: function (view) {
         exportState = "ExcelAndFile";
         var userGridView = view.findParentByType('exportgrid');
         var select = userGridView.getSelection();
         var tree = this.findGridView(view).down('treepanel');
         var ids = [];
         var selectAll = userGridView.down('[itemId=selectAll]').checked;
         exportSelectAll = selectAll
         var pattern = new RegExp("[`*':'.<>/?*|‘”“'\\\\？\"\"]");
         Ext.each(userGridView.getSelectionModel().getSelection(), function () {
             ids.push(this.get('entryid'));
         });
         entryids = ids;
         if (ids.length == 0 && selectAll == false) {
             XD.msg('请至少选择一条需要导出的数据');
             return;
         } else {
             var selectItem = Ext.create("Export.view.ExportGroupSetView", {});
             selectItem.items.get(0).getStore().load({});
             selectItem.show();
         }
     },*/
    chooseFieldExportExcel: function (view) {
        this.exportFunction(view, "Excel");
    },
    chooseFieldExportXml: function (view) {
        this.exportFunction(view, "Xml");
    },
    chooseFieldExportXmlAndFile: function (view) {
        this.exportFunction(view, "XmlAndFile");
    },
    chooseFieldExportExcelAndFile: function (view) {
        this.exportFunction(view, "ExcelAndFile");
    },
    chooseSave: function (view) {
        var filenames = "";
        var isbtn = "";
        var pattern = new RegExp("[/:*?\"<>|]");
        var selectView = view.findParentByType('exportGroupSetView');
        var FieldCode = selectView.items.get(0).getValue()
        var exporUrl = "";
        userFieldCode = FieldCode;
        if (FieldCode.length > 0) {

            var win = Ext.create("Export.view.ExportMessageView", {});
            win.show();
        } else {
            XD.msg("请选择需要导出的字段")
        }
    },
    //--下载节点字段模板
    downloadFieldTemp: function (btn) {
        var reqUrl = "/export/downloadFieldTemp?nodeid=" + NodeIdf;
        window.location.href = reqUrl;
    },

    addZipPassword: function (view) {
        var exportMessageView = view.findParentByType('ExportMessage');
        var isAddZipKey = exportMessageView.down('[itemId=addZipKey]').checked;
    }
});