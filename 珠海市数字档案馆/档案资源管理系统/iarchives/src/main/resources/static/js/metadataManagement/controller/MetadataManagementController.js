/**
 * Created by SunK on 2018/7/31 0031.
 */
var NodeIdf = "";
var formAndGridView;
var gridviews = "";
Ext.define('MetadataManagement.controller.MetadataManagementController', {
    extend: 'Ext.app.Controller',
    views: [
        'MetadataManagementGridView',
        'MetadataManagementFormAndGridView',
        'MetadataManagementTopView',
        'MetadataManagementGroupSetView',
        'MetadataManagementMessageView',
        'MetadataManagementInnerGrid',
        'FormView', 'MetadataManagementFormView',
        'AddMetadataView'
    ],
    models: ['MetadataManagementModel', 'MetadataManagementGroupSetModel',
        'MetadataManagementGridModel'],
    stores: ['MetadataManagementStore', 'MetadataManagementGroupSetStore',
        'MetadataManagementGridStore', 'AddMetadataAccreditStore', 'AddMetadataOperationStore'
    ,'AddmetadataUserStore'],

    init: function () {
        this.control({
            'metadataManagementFormAndGrid [itemId=treepanelId]': {
                select: function (treemodel, record) {
                    var gridcard = this.findView(treemodel.view).down('[itemId=gridcard]');
                    var onlygrid = gridcard.down('[itemId=onlygrid]');
                    var pairgrid = gridcard.down('[itemId=pairgrid]');
                    var grid;
                    var nodeType = record.data.nodeType;
                    var bgSelectOrgan = gridcard.down('[itemId=bgSelectOrgan]');
                    //树节点为分类，更改右边页面为“请选择机构节点”
                    gridcard.setActiveItem(pairgrid);
                    var ajgrid = pairgrid.down('[itemId=northgrid]');
                    ajgrid.setTitle("当前位置：" + record.data.text);
                    var jngrid = pairgrid.down('[itemId=southgrid]');
                    jngrid.setTitle("查看元数据追溯");
                    if (jngrid.expandOrcollapse == 'expand') {
                        jngrid.expand();
                    } else {
                        jngrid.collapse();
                    }
                    // jngrid.dataUrl = '/management/entries/innerfile/' + '' + '/';
                    // jngrid.initGrid(this.getNodeid(record.get('nodeid')));
                    grid = ajgrid;
                    formAndGridView = gridcard.up('metadataManagementFormAndGrid');
                    var gridview = gridcard.up('metadataManagementFormAndGrid').down('metadataManagementgrid');
                    gridviews = gridview;
                    gridview.setTitle("当前位置：" + record.data.text);//将表单与表格视图标题改成当前位置
                    grid.nodeid = record.get('fnid');
                    grid.initGrid({nodeid: record.get('fnid')});
                    NodeIdf = record.get('fnid');
                    var demoStore = Ext.getStore('MetadataManagementGroupSetStore');
                    demoStore.proxy.extraParams.fieldNodeid = NodeIdf;
                }
            },
            'metadataManagementFormAndGrid [itemId=northgrid]': {
                itemclick: this.itemclickHandler
            },
            'metadataManagementGroupSetView button[itemId="close"]': {
                click: function (view) {
                    view.findParentByType('metadataManagementGroupSetView').close();
                }
            },
            'metadataManagementGroupSetView button[itemId="addAllOrNotAll"]': {
                click: function (view) {
                    var itemSelector = view.findParentByType('metadataManagementGroupSetView').down('itemselector');
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
            'metadataManagementgrid [itemId=look]': {//查看
                click: this.lookHandler
            },
            'metadataManagementgrid [itemId=del]': {//删除
                click: this.delHandler
            },
            'metadataManagementgrid [itemId=modify]': {//修改
                click: this.modifyHandler
            },
            'metadataManagementgrid [itemId=add]': {//增加
                click: this.saveHandler
            },
            'managementform [itemId=back]': {//返回按钮
                click: function (btn) {
                    var treepanel = this.findTreeView(btn);
                    var nodeid = treepanel.selModel.getSelected().items[0].get('fnid');
                    var currentManagementform = this.getCurrentManagementform(btn);
                    var formview = currentManagementform.down('dynamicform');
                    if (formview.nodeid != nodeid) {
                        //切换到列表界面,同时刷新列表数据
                        this.activeGrid(btn, false);
                        this.findInnerGrid(btn).getStore().reload();
                    } else {
                        this.activeGrid(btn, true);
                        this.findInnerGrid(btn).getStore().removeAll();
                    }
                }
            },
            'managementform [itemId=save]': {
                click: this.submitForm
            },
            'entrygrid [itemId=addMetadata]': {//追溯元数据的-增加
                click: function (btn) {
                    var grid = this.getGrid(btn);
                    var record;
                    var selectAll;
                    if (window.isMedia != true) {
                        record = grid.selModel.getSelection();
                        selectAll = grid.down('[itemId=selectAll]').checked;
                    } else
                        record = grid.acrossSelections;
                    var selectCount = record.length;
                    if (selectCount != 1) {
                        XD.msg("请选择一条操作记录!");
                        return;
                    }
                    var entryid = record[0].get('entryid');
                    var entrygrid = btn.findParentByType('entrygrid');
                    var win = new Ext.create('MetadataManagement.view.AddMetadataView', {
                        title: '增加参数',
                        entrygrid: entrygrid,
                        entryid:entryid
                    });
                    win.show();
                }
            },
            'entrygrid [itemId=deleteMetadata]': {//追溯元数据的-删除
                click: function (view) {
                    var entrygrid = view.findParentByType('entrygrid');
                    var select = entrygrid.getSelectionModel();
                    if (select.getSelection().length ==0) {
                        XD.msg("请选择一条操作记录!");
                    } else {
                        var ids = [];
                        for(var i=0;i<select.getSelection().length;i++){
                            ids.push(select.getSelection()[i].get('sid'));
                        }
                        XD.confirm('是否删除这'+select.getSelection().length+'条数据',function () {
                            Ext.Ajax.request({
                                url:'/metadataManagement/deleteServiceMetadata',
                                method:'POST',
                                params:{
                                    ids:ids
                                },
                                success:function (rep) {
                                    var respText = Ext.decode(rep.responseText);
                                    if(!respText.success){
                                        XD.msg('删除失败');
                                    }else{
                                        XD.msg('删除成功');
                                        entrygrid.getStore().reload();
                                    }
                                },
                                failure:function () {
                                    XD.msg('操作失败');
                                }
                            });
                        });
                    }
                }
            },
            'entrygrid [itemId=modifyMetadata]': {//追溯元数据的-修改
                click: function (btn) {
                    var grid = this.getGrid(btn);
                    var record;
                    var selectAll;
                    if (window.isMedia != true) {
                        record = grid.selModel.getSelection();
                        selectAll = grid.down('[itemId=selectAll]').checked;
                    } else
                        record = grid.acrossSelections;
                    var selectCount = record.length;
                    if (selectCount != 1) {
                        XD.msg("请选择一条操作记录!");
                        return;
                    }
                    var entryid = record[0].get('entryid');

                    var entrygrid = btn.findParentByType('entrygrid');
                    var select = entrygrid.getSelectionModel();
                    if (select.getSelection().length != 1) {
                        XD.msg("请选择一条操作记录!");
                    } else if (select.getSelection().length > 1) {
                        XD.msg("只能选择一条操作记录!");
                    } else {
                        var win = new Ext.create('MetadataManagement.view.AddMetadataView', {
                            title: '修改',
                            entrygrid:entrygrid,
                            entryid:entryid
                        });
                        var form = win.down('form');
                        form.load({
                            url: '/metadataManagement/getServiceMetadataByid',
                            method: 'POST',
                            params: {
                                sid: select.getSelection()[0].get('sid')
                            },
                            success: function (form, action) {
                                var respText = Ext.decode(action.response.responseText);
                                if (!respText.success) {
                                    XD.msg('获取表单信息失败');
                                    return;
                                }
                                win.down('[itemId=shortnameComboId]').getStore().reload();
                                var realnameCombStore = win.down('[itemId=realnameComboId]').getStore();
                                realnameCombStore.proxy.extraParams.userid = select.getSelection()[0].get('userid');
                                realnameCombStore.reload();
                                win.show();
                            },
                            failure: function () {
                                XD.msg('操作失败');
                            }
                        });
                    }
                }
            },
            'entrygrid [itemId=lookMetadata]': {//追溯元数据的-查看
                click: function (view) {
                    var entrygrid = view.findParentByType('entrygrid');
                    var select = entrygrid.getSelectionModel();
                    if (select.getSelection().length != 1) {
                        XD.msg("请选择一条操作记录!");
                    } else if (select.getSelection().length > 1) {
                        XD.msg("只能选择一条操作记录!");
                    } else {
                        var win = new Ext.create('MetadataManagement.view.AddMetadataView', {
                            title: '增加参数'
                        });
                        win.down('[itemId=save]').hide();
                        // win.down('form').loadRecord(select.getLastSelected());
                        var form = win.down('form');
                        form.load({
                            url: '/metadataManagement/getServiceMetadataByid',
                            method: 'POST',
                            params: {
                                sid: select.getSelection()[0].get('sid')
                            },
                            success: function (form, action) {
                                var respText = Ext.decode(action.response.responseText);
                                if (!respText.success) {
                                    XD.msg('获取表单信息失败');
                                    return;
                                }
                                win.down('[itemId=shortnameComboId]').getStore().reload();
                                var realnameCombStore = win.down('[itemId=realnameComboId]').getStore();
                                realnameCombStore.proxy.extraParams.userid = select.getSelection()[0].get('userid');
                                realnameCombStore.reload();
                                win.show();
                            }
                        });
                    }
                }
            }, 'accreditMetadataWindow button[itemId=cancel]': {
                click: function (view) {
                    var systemConfigWindow = view.findParentByType('accreditMetadataWindow');
                    systemConfigWindow.close();
                }
            },
            'accreditMetadataWindow button[itemId=save]': {
                click: function (view) {
                    var accreditMetadataWindow = view.findParentByType('accreditMetadataWindow');
                    var entrygrid = accreditMetadataWindow.entrygrid;
                    var form = accreditMetadataWindow.down('form');

                    if(!form.isValid()){
                        XD.msg('存在必填项未填写');
                        return;
                    }
                    form.submit({
                        url:'/metadataManagement/addMetadata?entryid='+accreditMetadataWindow.entryid,
                        method:'POST',
                        scope: this,
                        success:function (form,action) {
                            if(accreditMetadataWindow.title=='修改'){
                                XD.msg('修改成功');
                            }else{
                                XD.msg('新增成功');
                            }
                            entrygrid.getStore().reload();
                            accreditMetadataWindow.close();
                        },
                        failure:function () {
                            XD.msg('操作失败');
                        }
                    });
                }
            }
        })
    },

    findView: function (btn) {
        return btn.up('metadataManagementFormAndGrid');
    },
    findTreeView: function (btn) {
        return btn.up('metadataManagementFormAndGrid').down('treepanel');
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
        if (typeof (parentid) != 'undefined' && parentid != '') {
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
        southgrid.dataUrl = '/metadataManagement/entries/innerfile/' + entryid + '/';
        var nodeid = this.getNodeid(record.get('nodeid'));
        southgrid.initGrid({nodeid: nodeid});
        southgrid.setTitle('查看' + fileArchivecode + '的追溯元数据');
    },

    findDfView: function (btn) {
        return this.findView(btn).down('formView').down('managementform').down('dynamicform');
    },
    findFormToView: function (btn) {
        return this.findView(btn).down('formView').down('managementform');
    },
    getCodesetting: function (nodeid) {
        var isExist = false;//档号构成字段的集合
        Ext.Ajax.request({//获得档号构成字段的集合
            url: '/codesetting/getCodeSettingFields',
            async: false,
            params: {
                nodeid: nodeid
            },
            success: function (response) {
                var res = Ext.decode(response.responseText).success;
                if (res) {
                    isExist = true;
                }
            }
        });
        return isExist;
    },
    getNodename: function (nodeid) {
        var nodename;
        Ext.Ajax.request({
            async: false,
            url: '/nodesetting/getFirstLevelNode/' + nodeid,
            success: function (response) {
                nodename = Ext.decode(response.responseText);
            }
        });
        return nodename;
    },
    ifSettingCorrect: function (nodeid, templates) {
        var hasArchivecode = false;//表单字段是否包含档号（archivecode）
        Ext.each(templates, function (item) {
            if (item.fieldcode == 'archivecode') {
                hasArchivecode = true;
            }
        });
        if (hasArchivecode) {//若表单字段包含档号，则判断档号设置是否正确
            var codesettingState = this.ifCodesettingCorrect(nodeid);
            if (!codesettingState) {
                XD.msg('请检查档号设置信息是否正确');
                return;
            }
        }
        return '档号设置正确';
    },
    ifCodesettingCorrect: function (nodeid) {
        var codesetting = [];
        Ext.Ajax.request({
            url: '/codesetting/getCodeSettingFields',
            async: false,
            params: {
                nodeid: nodeid
            },
            success: function (response) {
                if (Ext.decode(response.responseText).success == true) {
                    codesetting = Ext.decode(response.responseText).data;
                }
            }
        });
        if (codesetting.length == 0) {
            return;
        }
        return '档号设置信息正确';
    },
    //查看
    lookHandler: function (btn) {
        var grid = this.getGrid(btn);
        var form = this.findDfView(btn);
        var record;
        var selectAll;
        if (window.isMedia != true) {
            record = grid.selModel.getSelection();
            selectAll = grid.down('[itemId=selectAll]').checked;
        } else
            record = grid.acrossSelections;
        var selectCount = record.length;
        if (selectAll) {
            XD.msg('不支持选择所有页查看');
            return;
        }
        var tree = this.findGridView(btn).down('treepanel');
        var node = tree.selModel.getSelected().items[0];

        if (!node) {
            XD.msg('请选择节点');
            return;
        }
        if (selectCount == 0) {
            XD.msg('请至少选择一条需要查看的数据');
            return;
        }
        var initFormFieldState = this.initFormField(form, 'hide', node.get('fnid'));
        if (!initFormFieldState) {//表单控件加载失败
            return;
        }
        var entryids = [];
        for (var i = 0; i < record.length; i++) {
            entryids.push(record[i].get('entryid'));
        }
        form.operate = 'look';
        form.entryids = entryids;
        form.entryid = entryids[0];
        this.initFormData('look', form, entryids[0]);
        this.activeToForm(form);
    },
    //删除
    delHandler: function (btn) {
        var grid = this.getGrid(btn);
        var selectAll;
        var selLen;
        if (window.isMedia != true) {
            selectAll = grid.down('[itemId=selectAll]').checked;
            selLen = grid.selModel.getSelectionLength();
        } else {
            selLen = grid.acrossSelections.length;
        }
        var tree = this.findGridView(btn).down('treepanel');
        var node = tree.selModel.getSelected().items[0];
        if (!node) {
            XD.msg('请选择节点');
            return;
        }
        if (selLen == 0) {
            XD.msg('请至少选择一条需要删除的数据');
            return;
        }
        XD.confirm('确定要删除这 ' + selLen + ' 条数据吗?', function () {
            var record;
            if (window.isMedia != true)
                record = grid.selModel.getSelection();
            else
                record = grid.acrossSelections;
            var isSelectAll = false;
            if (selectAll) {
                record = grid.acrossDeSelections;
                isSelectAll = true;
            }
            var tmp = [];
            for (var i = 0; i < record.length; i++) {
                tmp.push(record[i].get('entryid'));
            }
            var entryids = tmp.join(',');
            // var tempParams = grid.getStore().proxy.extraParams;
            var tempParams;
            if (window.isMedia == true)
                tempParams = grid.down('dataview').getStore().proxy.extraParams;
            else
                tempParams = grid.getStore().proxy.extraParams;
            tempParams['entryids'] = entryids;
            tempParams['isSelectAll'] = isSelectAll;
            Ext.Msg.wait('正在删除数据，请耐心等待……', '正在操作');
            Ext.Ajax.request({
                method: 'post',
                scope: this,
                url: '/management/delete',
                params: tempParams,
                timeout: XD.timeout,
                success: function (response) {
                    var resp = Ext.decode(response.responseText);
                    if ('无法删除' == resp.msg) {
                        Ext.Msg.close();
                        var titles = resp.data;
                        var title;
                        for (var i = 0; i < titles.length; i++) {
                            if (i == 0) {
                                title = '[' + titles[i] + ']';
                            } else {
                                title = title + '，' + '[' + titles[i] + ']';
                            }
                        }
                        XD.msg('无法删除，这  ' + titles.length + '  条题名为  ' + title + '  还处于未归状态')
                    } else {
                        XD.msg(resp.msg);
                        if (window.isMedia == true)
                            grid.initGrid({nodeid: node.data.fnid});
                        else {
                            grid.getStore().proxy.url = '/management/entriesPost';
                            grid.getStore().proxy.extraParams.entryids = '';
                            grid.delReload(grid.selModel.getSelectionLength());
                            //grid.initGrid({nodeid: node.data.fnid},true);//刷新整个数据管理列表以及下面的数据显示
                            this.findInnerGrid(btn).getStore().removeAll();
                            this.findInnerGrid(btn).setTitle('');
                        }
                        Ext.MessageBox.hide();
                    }
                }
            })
        }, this);
    },

    //修改
    modifyHandler: function (btn) {
        formvisible = true;
        formlayout = 'formview';
        var managementform = this.findFormToView(btn);
        managementform.down('electronic').operateFlag = 'modify';
        managementform.down('electronic').eletype = "management";
        // managementform.down('electronic').down('[itemId=toolbar2]').hide()  //隐藏保存版本 查看历史按钮
        managementform.saveBtn = managementform.down('[itemId=save]');
        managementform.continueSaveBtn = managementform.down('[itemId=continuesave]');
        managementform.operateFlag = 'modify';
        var grid = this.getGrid(btn);
        var form = managementform.down('dynamicform');
        var record;
        var selectAll;
        if (grid.selModel == null)
            record = grid.acrossSelections;
        else {
            record = grid.selModel.getSelection();
            selectAll = grid.down('[itemId=selectAll]').checked
        }
        var selectCount = record.length;
        if (selectAll) {
            XD.msg('不支持选择所有页修改');
            return;
        }
        var tree = this.findGridView(btn).down('treepanel');
        var node = tree.selModel.getSelected().items[0];
        if (!node) {
            XD.msg('请选择节点');
            return;
        }
        if (selectCount == 0) {
            XD.msg('请至少选择一条需要修改的数据');
            return;
        }
        var initFormFieldState = this.initFormField(form, 'show', node.get('fnid'));
        var codesetting = this.getCodesetting(node.get('fnid'));
        var nodename = this.getNodename(node.get('fnid'));
        var info = false;
        if (codesetting) {//如果设置了档号字段
            info = true;
        } else {
            if (nodename == '未归管理' || nodename == '文件管理' || nodename == '资料管理') {
                info = true;
            }
        }
        if (info) {
            if (typeof (initFormFieldState) != 'undefined') {
                // var entryid;
                // if (selectAll) {
                //     entryid = grid.selModel.selected.items[0].get("entryid");
                // } else {
                //     entryid = record[0].get("entryid");
                // }
                var entryids = [];
                for (var i = 0; i < record.length; i++) {
                    entryids.push(record[i].get('entryid'));
                }
                form.operate = 'modify';
                form.entryids = entryids;
                form.entryid = entryids[0];
                this.initFormData('modify', form, entryids[0]);
                this.activeToForm(form);
                if (form.operateType) {
                    form.operateType = undefined;
                }
            }
        } else {
            XD.msg('请检查档号模板信息是否正确');
        }
    },

    ilookHandler: function (btn) {
        var grid = this.getInnerGrid(btn);
        var form = this.findDfView(btn);
        var records = grid.selModel.getSelection();
        var nodeid = grid.dataParams.nodeid;
        // var entryid = records[0].get('entryid');
        var initFormFieldState = this.initFormField(form, 'hide', nodeid);
        if (!initFormFieldState) {//表单控件加载失败
            return;
        }
        var entryids = [];
        for (var i = 0; i < records.length; i++) {
            entryids.push(records[i].get('entryid'));
        }
        form.operate = 'look';
        form.entryids = entryids;
        form.entryid = entryids[0];
        this.initFormData('look', form, entryids[0]);
        this.activeToForm(form);
    },
    getGrid: function (btn) {
        var grid;
        if (!btn.findParentByType('formAndGrid')) {
            grid = this.findActiveGrid(btn);
        } else {
            grid = this.findGridToView(btn);
        }
        return grid;
    },

    getInnerGrid: function (btn) {
        var grid;
        if (!btn.findParentByType('formAndInnerGrid')) {
            grid = this.findInnerGrid(btn);
        } else {
            grid = this.findInnerGridView(btn);
        }
        return grid;
    },

    findActiveGrid: function (btn) {
        var active = this.findView(btn).down('[itemId=gridcard]').getLayout().getActiveItem();
        if (active.getXType() == "managementgrid" || active.getXType() == 'mediaItemsDataView') {//添加声像页面的获取
            return active;
        } else if (active.getXType() == "panel") {
            return active.down('[itemId=northgrid]');
        }
    },

    findActiveInnerGrid: function (btn) {
        var active = this.findView(btn).down('[itemId=gridcard]').getLayout().getActiveItem();
        if (active.getXType() == "managementgrid") {
            return active;
        } else if (active.getXType() == "panel") {
            return this.findView(btn).down('formAndInnerGrid').down('managementgrid');
        }
    },

    findInnerGrid: function (btn) {
        return this.findView(btn).down('[itemId=southgrid]');
    },
    initFormField: function (form, operate, nodeid) {
//        if (form.nodeid != nodeid) {//切换节点后，form和tree的节点id不相等
        form.nodeid = nodeid;//用左侧树节点的id初始化form的nodeid参数
        form.removeAll();//移除form中的所有表单控件
        var field = {
            xtype: 'hidden',
            name: 'entryid'
        };
        form.add(field);
        var formField = form.getFormField();//根据节点id查询表单字段
        if (formField.length == 0) {
            XD.msg('请检查档号设置信息是否正确');
            return;
        }
        form.templates = formField;
        form.initField(formField, operate);//重新动态添加表单控件
//        }
        return '加载表单控件成功';
    }, //获取表单字段
    getFormField: function (nodeid) {
        var formField;
        Ext.Ajax.request({
            url: '/template/form',
            async: false,
            params: {
                nodeid: nodeid
            },
            success: function (response) {
                formField = Ext.decode(response.responseText);
                console.log(formField);
            }
        });
        return formField;
    }, initFormData: function (operate, form, entryid, state) {
        var nullvalue = new Ext.data.Model();
        var managementform = form.up('managementform');
        var fields = form.getForm().getFields().items;
        var prebtn = form.down('[itemId=preBtn]');
        var nextbtn = form.down('[itemId=nextBtn]');
        var savebtn = managementform.down('[itemId=save]');
        var continuesavebtn = managementform.down('[itemId=continuesave]');
        var count = 1;
        if (operate == 'modify' || operate == 'look') {
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
        }
        for (var i = 0; i < fields.length; i++) {
            if (fields[i].value && typeof (fields[i].value) == 'string' && fields[i].value.indexOf('label') > -1) {
                continue;
            }
            if (fields[i].xtype == 'combobox') {
                fields[i].originalValue = null;
            }
            nullvalue.set(fields[i].name, null);
        }
        form.loadRecord(nullvalue);
        var etips = form.up('managementform').down('[itemId=etips]');
        etips.show();
        if (operate != 'look' && operate != 'lookfile') {
            var settingState = this.ifSettingCorrect(form.nodeid, form.templates);
            if (!settingState) {
                return;
            }
            Ext.each(fields, function (item) {
                if (!item.freadOnly) {
                    item.setReadOnly(false);
                }
            });
        } else {
            Ext.each(fields, function (item) {
                item.setReadOnly(true);
            });
        }
        var eleview = this.getCurrentManagementform(form).down('electronic');
        var solidview = this.getCurrentManagementform(form).down('solid');
        // var longview = this.getCurrentManagementform(form).down('long');
        if (state == '案卷著录' || state == '卷内著录') {
            //通过节点查询当前模板的默认值
            Ext.Ajax.request({
                method: 'POST',
                params: {
                    nodeid: form.nodeid,
                    entryid: entryid,
                    type: state
                },
                url: '/management/getDefaultInfo',//通过节点的id获取模板中所有配置值默认数据
                success: function (response) {
                    var info = Ext.decode(response.responseText);
                    form.loadRecord({
                        getData: function () {
                            return info.data;
                        }
                    });
                }
            });
            eleview.initData();
            solidview.initData();
            // longview.initData();
        } else {
            Ext.Ajax.request({
                method: 'GET',
                scope: this,
                url: '/management/entries/' + entryid,
                success: function (response) {
                    var entry = Ext.decode(response.responseText);
                    if (operate == 'insertion') {
                        prebtn.setVisible(false);
                        nextbtn.setVisible(false);
                        this.entryID = entryid;
                        delete entry.entryid;
                    }
                    if (operate == 'lookfile') {
                        prebtn.setVisible(false);
                        nextbtn.setVisible(false);
                        savebtn.setVisible(false);
                        continuesavebtn.setVisible(false);
                    }
                    var data = Ext.decode(response.responseText);
                    if (data.organ) {
                        entry.organ = data.organ;//机构
                    }
                    if (operate == 'add') {
                        delete entry.entryid;
                        entry.filingyear = new Date().getFullYear();
                        entry.descriptiondate = Ext.util.Format.date(new Date(), 'Y-m-d H:i:s');
                        if (data.keyword && entry.keyword) {
                            entry.keyword = data.keyword;//主题词
                        }
                        Ext.Ajax.request({
                            async: false,
                            url: '/user/getUserRealname',
                            success: function (response) {
                                entry.descriptionuser = Ext.decode(response.responseText).data;
                            }
                        });
                    }
                    if (operate == 'add' || operate == 'modify') {
                        if (!data.organ) {
                            Ext.Ajax.request({
                                async: false,
                                url: '/nodesetting/findByNodeid/' + form.nodeid,
                                success: function (response) {
                                    entry.organ = Ext.decode(response.responseText).data.nodename;
                                }
                            });
                        }
                    }
                    form.loadRecord({
                        getData: function () {
                            return entry;
                        }
                    });
                    if (operate == 'add') {
                        var formValues = form.getValues();
                        var formParams = {};
                        for (var name in formValues) {//遍历表单中的所有值
                            formParams[name] = formValues[name];
                        }
                        formParams.nodeid = form.nodeid;
                        formParams.nodename = this.getNodename(form.nodeid);
                        var archive = '';
                        var calFieldName = '';
                        var calValue = '';
                        Ext.Ajax.request({//计算项的数值获取并设置
                            url: form.calurl,//动态URL
                            async: true,
                            params: formParams,
                            success: function (response) {
                                var result = Ext.decode(response.responseText).data;
                                if (result) {
                                    calFieldName = result.calFieldName;
                                    calValue = result.calValueStr;
                                    archive = result.archive;
                                }
                                var calField = form.getForm().findField(calFieldName);
                                if (calField == null) {
                                    return;
                                }
                                calField.setValue(calValue);//设置档号最后一个构成字段的值，填充至文本框中
                                var archiveCode = form.getForm().findField('archivecode');
                                if (archiveCode == null) {
                                    return;
                                }
                                archiveCode.setValue(archive);
                            }
                        });
                    }
                    var fieldCode = form.getRangeDateForCode();//字段编号，用于特殊的自定义字段(范围型日期)
                    if (fieldCode != null) {
                        //动态解析数据库日期范围数据并加载至两个datefield中
                        form.initDaterangeContent(entry);
                    }
                    eleview.initData(entry.entryid);
                    solidview.initData(entry.entryid);
                    // longview.initData(entry.entryid);
                }
            });
        }
       // form.formStateChange(operate);
        form.fileLabelStateChange(eleview, operate);
        form.fileLabelStateChange(solidview, operate);
        // form.fileLabelStateChange(longview,operate);
        this.changeBtnStatus(form,operate);
    },
    changeBtnStatus:function(form, operate){
        var savebtn,continuesave;
        if (form.findParentByType('formAndGrid')) {
            savebtn = this.findFormView(form).down('[itemId=save]');
            continuesave = this.findFormView(form).down('[itemId=continuesave]');
        }
        if (form.findParentByType('formAndInnerGrid')) {
            savebtn = this.findFormInnerView(form).down('[itemId=save]');
            continuesave = this.findFormInnerView(form).down('[itemId=continuesave]');
        }
        if (form.findParentByType('formView')||form.findParentByType('acquisitionTransdocView')) {
            savebtn = this.findFormToView(form).down('[itemId=save]');
            continuesave = this.findFormToView(form).down('[itemId=continuesave]');
        }
        if(operate == 'look'){//查看时隐藏保存及连续录入按钮
            savebtn.setVisible(false);
            continuesave.setVisible(false);
        }else if(operate == 'modify' || operate == 'insertion'){//修改或插件时隐藏连续录入按钮
            savebtn.setVisible(true);
            continuesave.setVisible(false);
        }else{
            savebtn.setVisible(true);
            // continuesave.setVisible(true);
        }
    },
    getCurrentManagementform: function (btn) {
        if (btn.up('formAndGrid')) {//如果是案卷表单
            return this.findFormView(btn);
        }
        if (btn.up('formAndInnerGrid')) {//如果是卷内表单
            return this.findFormInnerView(btn);
        }
        if (btn.up('formView') || btn.xtype == 'entrygrid' || btn.xtype == 'managementgrid') {
            return formAndGridView.down('formView').down('managementform');
        }
    },
    //切换到单个表单界面视图
    activeToForm: function (form) {
        var view = this.findView(form);
        var formView = view.down('formView');
        var managementform = formView.down('managementform');
        view.setActiveItem(formView);
        managementform.items.get(0).enable();
        managementform.setActiveTab(0);
        return formView;
    },
    //切换到列表界面视图
    activeGrid: function (btn, flag) {
        var view = this.findView(btn);
        view.setActiveItem(this.findGridView(btn));
        formAndGridView.setActiveItem(this.findGridView(btn));
        // this.findFormView(btn).saveBtn = undefined;
        // this.findFormView(btn).continueSaveBtn = undefined;
        // this.findFormInnerView(btn).saveBtn = undefined;
        // this.findFormInnerView(btn).continueSaveBtn = undefined;
        var allMediaFrame = document.querySelectorAll('#mediaFrame');
        if (allMediaFrame) {
            for (var i = 0; i < allMediaFrame.length; i++) {
                allMediaFrame[i].setAttribute('src', '');
            }
        }
        if (document.getElementById('solidFrame')) {
            document.getElementById('solidFrame').setAttribute('src', '');
        }
        // if(document.getElementById('longFrame')){
        //     document.getElementById('longFrame').setAttribute('src','');
        // }
        if (flag) {//根据参数确定是否需要刷新数据
            var grid = this.findActiveGrid(btn);
//            grid.initGrid();
            grid.notResetInitGrid();
        }
    },
    //获取表单界面视图
    findFormView: function (btn) {
        return this.findView(btn).down('metadataManagementgrid');
    },
    //保存表单数据，返回列表界面视图
    submitForm: function (btn) {
        var currentManagementform = this.getCurrentManagementform(btn);
        var eleids = currentManagementform.down('electronic').getEleids();
        var formview = currentManagementform.down('dynamicform');
        //字段编号，用于特殊的自定义字段(范围型日期)
        var nodename = this.getNodename(formview.nodeid);
        if (nodename == '未归管理') {  //未归管理下，自动获取页数
            var pages = formview.down('[name=pages]');
            if (pages) {
                if (pages.getValue() == "" || (pages.getValue() == "0" && eleids != "")) {
                    Ext.Ajax.request({
                        method: 'POST',
                        scope: this,
                        url: "/electronic/saveSetPages",
                        async: false,
                        params: {
                            eleids: eleids,
                            entrytype: "management"
                        },
                        success: function (response) {
                            var data = Ext.decode(response.responseText).data;
                            pages.setValue(data);
                        },
                        failure: function () {
                            XD.msg("自动获取页数失败");
                        }
                    })
                }
            }
        }
        var fieldCode = formview.getRangeDateForCode();
        var params = {
            nodeid: formview.nodeid,
            eleid: eleids,
            isMedia: window.isMedia,
            type: currentManagementform.operateFlag,
            operate: nodename
        };
        if (fieldCode != null) {
            params[fieldCode] = formview.getDaterangeValue();
        }
        var archivecodeSetState = formview.setArchivecodeValueWithNode(nodename);
        if (!archivecodeSetState) {//若档号设置失败，则停止后续的表单提交
            return;
        }
        var operateType = formview.operateType;
        var submitType = formview.submitType;
        Ext.MessageBox.wait('正在保存请稍后...', '提示');
        formview.submit({
            method: 'POST',
            url: '/management/entries',
            params: params,
            scope: this,
            success: function (form, action) {
                Ext.MessageBox.hide();
                var treepanel = this.findTreeView(btn);
                var nodeid = treepanel.selModel.getSelected().items[0].get('fnid');
                if (action.result.success == true) {
                    if (operateType == 'insertion') {//插件、插卷
                        var pages = action.result.data.pages;
                        var state = this.updateSubsequentData(this.entryID, submitType, pages);
                        //切换到列表界面,同时刷新列表数据
                        if (formview.nodeid != nodeid) {
                            this.activeGrid(btn, false);
                            this.findInnerGrid(btn).getStore().reload();
                        } else {
                            this.activeGrid(btn, true);
                            this.findInnerGrid(btn).getStore().removeAll();
                        }
                        if (!state) {
                            return;//保存条目成功，但插件后更新后续数据计算项及档号失败
                        }
                        //XD.msg(action.result.msg);//避免两个提示同时出现
                    }
                    //多条时切换到下一条。单条时或最后一条时切换到列表界面,同时刷新列表数据
                    if (formview.entryids && formview.entryids.length > 1 && formview.entryid != formview.entryids[formview.entryids.length - 1]) {
                        var allMediaFrame = document.querySelectorAll('#mediaFrame');
                        if (allMediaFrame) {
                            for (var i = 0; i < allMediaFrame.length; i++) {
                                allMediaFrame[i].setAttribute('src', '');
                            }
                        }
                        this.refreshFormData(formview, 'next');
                    } else {
                        if (formview.nodeid != nodeid) {
                            this.activeGrid(btn, false);
                            this.findInnerGrid(btn).getStore().reload();
                        } else {
                            this.activeGrid(btn, true);
                            this.findInnerGrid(btn).getStore().removeAll();
                        }
                    }
                    XD.msg(action.result.msg);
                    var entryids = [action.result.data.entryid];
                    //进行采集业务元数据
                    captureServiceMetadataByZL(entryids, '元数据管理', '著录');
                }
            },
            failure: function (form, action) {
                Ext.MessageBox.hide();
                XD.msg(action.result.msg);
            }
        });
        if (formview.operateType) {
            formview.operateType = undefined;
        }
    },
    //数据著录
    saveHandler: function (btn) {
        formvisible = true;
        formlayout = 'formgrid';
        var managementform = this.findFormToView(btn);
        // var managementform = btn.up('managementform');
        managementform.down('electronic').operateFlag = 'add';
        managementform.saveBtn = managementform.down('[itemId=save]');
        managementform.continueSaveBtn = managementform.down('[itemId=continuesave]');
        managementform.operateFlag = 'add';
        var grid = this.getGrid(btn);
        var form = managementform.down('dynamicform');
        var tree = this.findGridView(btn).down('treepanel');
        var selectCount;
        var selectAll;
        if (grid.selModel == null)
            selectCount = grid.acrossSelections.length;
        else {
            selectCount = grid.selModel.getSelection().length;
            selectAll = grid.down('[itemId=selectAll]').checked;
            if (selectAll) {
                selectCount = grid.selModel.selected.length;//当前页选中
            }
        }
        var node = tree.selModel.getSelected().items[0];
        var initFormFieldState = this.initFormField(form, 'show', node.get('fnid'));
        form.down('[itemId=preNextPanel]').setVisible(false);
        var codesetting = this.getCodesetting(node.get('fnid'));
        var nodename = this.getNodename(node.get('fnid'));
        var info = false;
        if (codesetting) {//如果设置了档号字段
            info = true;
        } else {
            if (nodename == '未归管理' || nodename == '文件管理' || nodename == '资料管理') {
                info = true;
            }
        }
        if (info) {
            if (typeof (initFormFieldState) != 'undefined') {
                if (selectCount == 0) {
                    this.initFormData('add', form, '', '案卷著录');
                    this.activeForm(form);
                    // this.initSouthGrid(form);
                } else if (selectCount != 1) {
                    XD.msg('只能选择一条数据')
                } else {
                    //选择数据著录，则加载当前数据到表单界面
                    var entryid;
                    if (window.isMedia == true)
                        entryid = grid.acrossSelections[0].get('entryid');
                    else if (selectAll) {
                        entryid = grid.selModel.selected.items[0].get("entryid");
                    } else {
                        entryid = grid.selModel.getSelection()[0].get("entryid");
                    }
                    this.initFormData('add', form, entryid, '案卷数据著录');
                    this.activeForm(form);
                    // this.initSouthGrid(form);
                }
                if (form.operateType) {
                    form.operateType = undefined;
                }
            }
        } else {
            XD.msg('请检查档号模板信息是否正确');
        }
    },
    //切换到表单界面视图
    activeForm: function (form) {
        var view = this.findView(form);
        var formAndGridView = view.down('formView');//保存表单与表格视图
        view.setActiveItem(formAndGridView);

        var formview = formAndGridView.down('managementform');
        formview.items.get(0).enable();
        formview.setActiveTab(0);
        return formAndGridView;
    }

});

/**
 *获取业务元数据
 * @param entryids 条目集合
 * @param module  模块名
 * @oaram operation 业务行为（著录..）
 * @returns {*}
 */
function captureServiceMetadataByZL(entryids, module, operation) {
    var r;
    Ext.Ajax.request({
        url: '/serviceMetadata/captureServiceMetadataByZL',
        async: true,
        methods: 'Post',
        params: {
            entryids: entryids,
            module: module,
            operation: operation
        },
        success: function (response) {
            r = Ext.decode(response.responseText);
            console.log(r.msg + ",条目数：" + r.data);
        }
    });
    return r;
}