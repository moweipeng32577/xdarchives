/**
 * Created by Administrator on 2020/9/17.
 */
Ext.define('BranchAudit.controller.BranchAuditController', {
    extend: 'Ext.app.Controller',
    views: ['BranchAuditView','ThematicProdDetailGridView','ThematicProdGridView',
        'ThematicProdAddWindow','ThematicProdDetailAddWindow','ElectronicView'],
    stores: ['ThematicProdDetailGridStore','ThematicProdGridStore'],
    models: ['ThematicProdGridModel','ThematicProdDetailGridModel'],
    init: function () {
        this.control({
            'thematicProdGridView ': {
                afterrender: function (view, e, eOpts) {
                    view.initGrid({state:'已提交'});
                }
            },
            'thematicProdGridView button[itemId=thematicSeeBtnID]': {
                click: function (view, e, eOpts) {
                    var thematicProdGridView = view.findParentByType('thematicProdGridView');
                    var select = thematicProdGridView.getSelectionModel();
                    if (select.getSelection().length == 0) {
                        XD.msg('请选择操作记录');
                    } else if (select.getSelection().length > 1) {
                        XD.msg('查看只能选中一条数据');
                    } else {
                        var window = Ext.create('BranchAudit.view.ThematicProdAddWindow');
                        window.title = '查看';
                        var record = select.getSelection()[0];
                        window.down('[itemId=component]').setDisabled(true);
                        window.down('[itemId=component]').autoEl.src = "/thematicProd/getBackground?url=" + encodeURIComponent(record.data.backgroundpath);
                        var form = window.down('form');
                        form.loadRecord(record);
                        var fields = form.getForm().getFields().items;
                        Ext.each(fields, function (item) {
                            item.setReadOnly(true);//设置查看表单中非按钮控件属性为只读
                        });
                        window.down('[itemId = thematicProSaveBtnID]').setHidden(true);
                        window.show();
                    }
                }
            },
            'thematicProdAddWindow button[itemId=thematicProBackBtnID]': {
                click: function (view, e, eOpts) {
                    var window = view.up('thematicProdAddWindow');
                    window.close();
                }
            },
            'thematicProdGridView button[itemId=compilation]': {
                click: function (view, e, eOpts) {
                    var thematicProdGridView = view.findParentByType('thematicProdGridView');
                    var select = thematicProdGridView.getSelectionModel();
                    if (select.getSelection().length == 0) {
                        XD.msg('请选择操作记录');
                    } else if (select.getSelection().length > 1) {
                        XD.msg('只能选中一条数据');
                    } else {
                        this.findView(view).setActiveItem(this.findDetailGridView(view));
                        this.findDetailGridView(view).initGrid({thematicId: select.getSelection()[0].get('id')});
                    }
                }
            },
            'thematicProdDetailGridView button[itemId=seeBtnID]': {
                click: function (view, e, eOpts) {
                    var thematicProdDetailGridView = this.findDetailGridView(view);
                    var select = thematicProdDetailGridView.getSelectionModel();
                    var records = select.getSelection();
                    if (records.length == 0) {
                        XD.msg('请选择数据');
                    } else if (records.length > 1) {
                        XD.msg('查看只能选中一条数据');
                    } else {
                        var window1 = Ext.create('BranchAudit.view.ThematicProdDetailAddWindow');
                        var record = records[0];
                        var form = window1.down('form');
                        form.loadRecord(record);
                        var fields = form.getForm().getFields().items;
                        Ext.each(fields, function (item) {
                            item.setReadOnly(true);//设置查看表单中非按钮控件属性为只读
                        });
                        window1.setTitle('查看');
                        window1.down('[itemId = saveBtnID]').setHidden(true);
                        window1.show();
                        window1.down('form').query('[itemId=mediacount]')[0].setText('共' + (record.data.mediatext == '' ? 0 : record.data.mediatext.split(',').length) + '份');
                        window.ztid = record.data.thematicdetilid;
                        var form = window1.down('form');
                        window.wform = form;
                    }
                }
            },
            'thematicProdDetailGridView button[itemId=seeElectronicBtnID]': {
                click: function (view, e, eOpts) {
                    var thematicProdDetailGridView = this.findDetailGridView(view);
                    var select = thematicProdDetailGridView.getSelectionModel();
                    var records = select.getSelection();
                    if (records.length == 0) {
                        XD.msg('请选择数据');
                    } else if (records.length > 1) {
                        XD.msg('查看只能选中一条数据');
                    } else {
                        var window1 = Ext.create('BranchAudit.view.ThematicProdDetailAddWindow');
                        var record = records[0];
                        var form = window1.down('form');
                        form.loadRecord(record);
                        var fields = form.getForm().getFields().items;
                        Ext.each(fields, function (item) {
                            item.setReadOnly(true);//设置查看表单中非按钮控件属性为只读
                        });
                        window1.setTitle('查看');
                        window1.down('[itemId = saveBtnID]').setHidden(true);
                        window1.down('form').query('[itemId=mediacount]')[0].setText('共' + (record.data.mediatext == '' ? 0 : record.data.mediatext.split(',').length) + '份');
                        window.ztid = record.data.thematicdetilid;
                        var form = window1.down('form');
                        window.wform = form;
                    }
                    window.leadIn = Ext.create("Ext.window.Window", {
                        width: '100%',
                        height: '100%',
                        title: '添加电子文件',
                        modal: true,
                        header: false,
                        draggable: false,//禁止拖动
                        resizable: false,//禁止缩放
                        closeToolText: '关闭',
                        layout: 'fit',
                        items: [{xtype: 'electronicPro'}]
                    });
                    Ext.on('resize', function (a, b) {
                        window.leadIn.setPosition(0, 0);
                        window.leadIn.fitContainer();
                    });
                    window.wmedia = 'undefined';
                    window.isType="查看";
                    //初始化原文数据
                    for (var i = 0; i < window.leadIn.down('electronicPro').down('toolbar').query('button').length - 1; i++) {
                        window.leadIn.down('electronicPro').down('toolbar').query('button')[i].hide();
                    }
                    window.leadIn.down('electronicPro').initData(window.ztid);
                    window.leadIn.show();
                    window1.hide();
                }
            },
            'thematicProdDetailGridView button[itemId=back]': {
                click: function (view, e, eOpts) {
                    this.findView(view).setActiveItem(this.findGridView(view));
                }
            },
            'thematicProdDetailAddWindow button[itemId=backBtnID]': {
                click: function (view, e, eOpts) {
                    var window = view.up('thematicProdDetailAddWindow');
                    window.close();
                }
            },
            'thematicProdGridView button[itemId=branchAudit]': {  //审核
                click:function(view){
                    var thematicProdGridView = view.findParentByType('thematicProdGridView');
                    var select = thematicProdGridView.getSelectionModel().getSelection();
                    if(select.length <1){
                        XD.msg('请至少选择一条数据');
                        return;
                    }
                    var ids = [];
                    for(var i=0;i<select.length;i++){
                        ids.push(select[i].get('id'));
                    }
                    Ext.create('BranchAudit.view.ApproveAddView',{ids:ids,grid:thematicProdGridView}).show();
                }
            },
            'approveAddView button[itemId=approveAddClose]':{ //审核-关闭
                click:function(view){
                    view.findParentByType("approveAddView").close();
                }
            },
            'approveAddView':{
                render:function(field){
                    field.down('[itemId=selectApproveId]').on('change',function(val){
                        if(val.value=='退回'){
                            field.down('[itemId=approveId]').setValue("");
                        }else{
                            field.down('[itemId=approveId]').setValue(val.value);
                        }
                    });
                }
            },
            'approveAddView button[itemId=approveAddSubmit]':{ //审核-提交
                click:function(view){
                    var approveAddView = view.up('approveAddView');
                    var auditDclGridView = view.up('approveAddView').grid;
                    var approveresult = approveAddView.down('[itemId=selectApproveId]').getValue();//审核结果
                    var areaText = approveAddView.down('[itemId=approveId]').getValue();//批示
                    if(''==areaText){
                        XD.msg('请输入批示');
                        return;
                    }
                    Ext.Ajax.request({
                        url:'/branchAudit/auditSubmit',
                        method:'POST',
                        params:{
                            ids:approveAddView.ids.join(','),
                            approveresult:approveresult,
                            areaText:areaText
                        },
                        success:function (rep) {
                            var respText = Ext.decode(rep.responseText);
                            approveAddView.close();
                            if(!respText.success){
                                XD.msg('提交失败');
                            }else{
                                XD.msg('提交成功');
                               auditDclGridView.getStore().reload();
                            }
                        },
                        failure:function () {
                            XD.msg('操作失败');
                        }
                    });
                }
            },
            'thematicProdDetailAddWindow button[itemId=electronId]': {
                click: function (view) {
                    window.leadIn = Ext.create("Ext.window.Window", {
                        width: '100%',
                        height: '100%',
                        title: '添加电子文件',
                        modal: true,
                        header: false,
                        draggable: false,//禁止拖动
                        resizable: false,//禁止缩放
                        closeToolText: '关闭',
                        layout: 'fit',
                        items: [{xtype: 'electronicPro'}]
                    });
                    Ext.on('resize', function (a, b) {
                        window.leadIn.setPosition(0, 0);
                        window.leadIn.fitContainer();
                    });
                    var title = view.up('thematicProdDetailAddWindow').title;
                    if (title == '增加') {
                        window.ztid = 'undefined';
                        window.isType="增加";
                    }
                    if (title == '查看') {
                        window.wmedia = 'undefined';
                        window.isType="查看";
                        //初始化原文数据
                        for (var i = 0; i < window.leadIn.down('electronicPro').down('toolbar').query('button').length - 1; i++) {
                            window.leadIn.down('electronicPro').down('toolbar').query('button')[i].hide();
                        }
                    } else if (title == '修改') {
                        window.wmedia = 'undefined';
                        window.isType="修改";
                    }
                    window.leadIn.down('electronicPro').initData(window.ztid);
                    window.leadIn.show();
                }
            },
            'electronicPro': {
                render: function (view) {
                    var buttons = view.down('toolbar').query('button');
                    for (var i = 0; i < buttons.length; i++) {
                        if (buttons[i].text == '上传' || buttons[i].text == '删除' || buttons[i].text == '返回') {
                            continue;
                        }
                        buttons[i].hide();
                    }
                }
            },
        })

    },
    //获取专题制作应用视图
    findView: function (btn) {
        return btn.findParentByType('branchAuditView');
    },
    findGridView: function (btn) {
        return this.findView(btn).down('thematicProdGridView');
    },

    findDetailGridView: function (btn) {
        return this.findView(btn).down('thematicProdDetailGridView');
    }
});