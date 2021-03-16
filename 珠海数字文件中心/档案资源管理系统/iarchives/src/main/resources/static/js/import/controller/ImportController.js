/**
 * 数据导入控制器
 * Created by Rong on 2018/10/9.
 */
var UnZipPath = "";
var impCountMsg = "";
Ext.define('Import.controller.ImportController', {
    extend: 'Ext.app.Controller',
    views: ['ImportView', 'DataNodeComboView'],
    stores: ['ImportGridStore', 'TemplateStore'],
    models: ['ImportGridModel'],

    init: function () {
        this.control({
            'import': {
                afterrender: this.afterrender
            },
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
                    var workspace = btn.up('[itemId=workspace]');
                    var exportMissView = Ext.create('Import.view.ImportMsgView');
                    exportMissView.preview = workspace;
                    exportMissView.show();
                }
            },
            'importMsgView button[itemId="import"]': {
                click: this.impHandler
            }
        });
    },

    /**
     * 默认选中第一个节点
     * @param panel 左边树容器Panel
     */
    afterrender: function (panel) {
        var treelist = panel.down('treelist');
        treelist.setSelection(treelist.getStore().getAt(0));
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
        var nodeid = form.getForm().findField('target').getValue();
        var flag = this.getOrganid(nodeid);
        if (!flag) {
            XD.msg('当前选的节点不是机构节点');
            return;
        }
        if (target.getValue() != null) {
            this.submit(form);
        }
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
                //进行文件最大行数判断
                if (action.result.rowCount > 5000) {
                    impCountMsg = "导入数据量过大导致导入速度过慢，建议点击【取消】按钮，可等导入完成后再到“综合管理-数据查重”" +
                        "中进行档号查重的检查";
                } else {
                    impCountMsg = "是否判断重复？";
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
        for (var i = 0; i < grid.getColumns().length; i++) {
            var column = grid.getColumns()[i];
            if (column.dataIndex == e.record.data.source) {
                column.setText(e.record.data.target == "" ? e.record.data.source : e.record.data.target);
            }
        }
    },

    /**
     * 开始执行导入
     * @param btn
     */
    impHandler: function (btn) {
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
        var autoCreateArchivecode = workspace.down('[itemId=autoCreateArchivecode]');
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
        var suffix = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
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
                    autoCreateArchivecode:autoCreateArchivecode.getValue()
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
