/**
 * Created by yl on 2017/10/27.
 */
Ext.define('ThematicProd.controller.ThematicProdController', {
    extend: 'Ext.app.Controller',

    views: ['ThematicProdView',
        'ThematicProdGridView',
        'ThematicProdAddWindow',
        'ThematicProdDetailGridView',
        'ThematicProdDetailAddWindow',
        'ElectronicView',
        'SimpleSearchView',
        'SimpleSearchGridView'],//加载view
    stores: ['ThematicProdGridStore', 'ThematicProdDetailGridStore', 'SimpleSearchGridStore'],//加载store
    models: ['ThematicProdGridModel', 'ThematicProdDetailGridModel', 'SimpleSearchGridModel'],//加载model
    init: function () {
        var thematicProdGridView;
        var thematicProdDetailGridView;
        var count = 0;
        this.control({
            'thematicProdGridView ': {
                afterrender: function (view, e, eOpts) {
                    view.initGrid();
                }
            },
            'thematicProdGridView button[itemId=thematicAddBtnID]': {
                click: function (view, e, eOpts) {
                    thematicProdGridView = view.findParentByType('thematicProdGridView');
                    var window = Ext.create('ThematicProd.view.ThematicProdAddWindow');
                    window.down('[itemId=component]').backgroundpath = null;
                    window.down('[itemId=component]').autoEl.src = "/thematicMake/getBackground?url=" + '/static/img/icon/thematic_def.png';
                    window.title = '增加';
                    window.show();
                }
            },
            'thematicProdGridView button[itemId=thematicDeleteBtnID]': {
                click: function (view, e, eOpts) {
                    var thematicProdGridView = view.findParentByType('thematicProdGridView');
                    var select = thematicProdGridView.getSelectionModel();
                    var records = select.getSelection();
                    if (records.length == 0) {
                        XD.msg('请选择操作记录');
                    } else {
                        XD.confirm('确定要删除这' + records.length + '条数据吗', function () {
                            var thematicids = [];
                            for (var i = 0; i < records.length; i++) {
                                thematicids.push(records[i].get('thematicid'));
                            }
                            Ext.Ajax.request({
                                params: {thematicids: thematicids},
                                url: '/thematicMake/deleteThematic',
                                method: 'POST',
                                sync: true,
                                success: function (resp, opts) {
                                    XD.msg(Ext.decode(resp.responseText).msg);
                                    thematicProdGridView.delReload(records.length);
                                },
                                failure: function () {
                                    XD.msg('操作失败');
                                }
                            });
                        }, this);
                    }
                }
            },
            'thematicProdGridView button[itemId=thematicUpdateBtnID]': {
                click: function (view, e, eOpts) {
                    thematicProdGridView = view.findParentByType('thematicProdGridView');
                    var select = thematicProdGridView.getSelectionModel();
                    if (select.getSelection().length == 0) {
                        XD.msg('请选择操作记录');
                    } else if (select.getSelection().length > 1) {
                        XD.msg('修改只能选中一条数据');
                    } else {
                        var window = Ext.create('ThematicProd.view.ThematicProdAddWindow');
                        window.title = '修改';
                        var record = select.getSelection()[0];
                        window.down('[itemId=component]').autoEl.src = "/thematicMake/getBackground?url=" + encodeURIComponent(record.data.backgroundpath);
                        window.down('[itemId=component]').backgroundpath = record.data.backgroundpath;
                        window.down('form').loadRecord(record);
                        window.show();
                    }
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
                        var window = Ext.create('ThematicProd.view.ThematicProdAddWindow');
                        window.title = '查看';
                        var record = select.getSelection()[0];
                        window.down('[itemId=component]').setDisabled(true);
                        window.down('[itemId=component]').autoEl.src = "/thematicMake/getBackground?url=" + encodeURIComponent(record.data.backgroundpath);
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
            'thematicProdGridView button[itemId=releaseBtnID]': {
                click: function (view, e, eOpts) {
                    var thematicProdGridView = view.findParentByType('thematicProdGridView');
                    var select = thematicProdGridView.getSelectionModel();
                    var records = select.getSelection();
                    if (records.length == 0) {
                        XD.msg('请选择操作记录');
                    } else {
                        XD.confirm('确定要发布这' + records.length + '条数据吗', function () {
                            var thematicids = [];
                            for (var i = 0; i < records.length; i++) {
                                thematicids.push(records[i].get("thematicid"));
                            }
                            Ext.Msg.wait('正在进行发布操作，请耐心等待……','提示');
                            Ext.Ajax.request({
                                params: {thematicids: thematicids.join(',')},
                                url: '/thematicMake/releaseThematic',
                                method: 'POST',
                                sync: true,
                                success: function (resp, opts) {
                                    Ext.MessageBox.hide();
                                    XD.msg(Ext.decode(resp.responseText).msg);
                                    thematicProdGridView.notResetInitGrid();
                                },
                                failure: function () {
                                    Ext.MessageBox.hide();
                                    XD.msg('操作失败');
                                }
                            });
                        }, this);
                    }
                }
            },
            'thematicProdGridView button[itemId=cancleReleaseBtnID]': {
                click: function (view, e, eOpts) {
                    var thematicProdGridView = view.findParentByType('thematicProdGridView');
                    var select = thematicProdGridView.getSelectionModel();
                    var records = select.getSelection();
                    if (records.length == 0) {
                        XD.msg('请选择操作记录');
                    } else {
                        XD.confirm('确定要取消发布这' + records.length + '条数据吗', function () {
                            var thematicids = [];
                            for (var i = 0; i < records.length; i++) {
                                thematicids.push(records[i].get("thematicid"));
                            }
                            Ext.Ajax.request({
                                params: {thematicids: thematicids.join(',')},
                                url: '/thematicMake/cancleReleaseThematic',
                                method: 'POST',
                                sync: true,
                                success: function (resp, opts) {
                                    XD.msg(Ext.decode(resp.responseText).msg);
                                    thematicProdGridView.notResetInitGrid();
                                },
                                failure: function () {
                                    XD.msg('操作失败');
                                }
                            });
                        }, this);
                    }
                }
            },
            'thematicProdGridView button[itemId=compilation]': {
                click: function (view, e, eOpts) {
                    thematicProdGridView = view.findParentByType('thematicProdGridView');
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
            'thematicProdAddWindow button[itemId=thematicProSaveBtnID]': {
                click: function (view, e, eOpts) {
                    var thematicProdAddWindow = view.findParentByType('thematicProdAddWindow');
                    var title = thematicProdAddWindow.title;
                    var form = thematicProdAddWindow.down('form');
                    var url, params;
                    if (title == '增加') {
                        url = '/thematicMake/saveThematic';
                        params = {backgroundpath: thematicProdAddWindow.down('[itemId=component]').backgroundpath};
                    } else {
                        url = '/thematicMake/updateThematic';
                        params = {
                            backgroundpath: thematicProdAddWindow.down('[itemId=component]').backgroundpath,
                            thematicid: thematicProdGridView.getSelectionModel().getSelection()[0].get("thematicid")
                        }
                    }
                    if (form.isValid()) {
                        form.submit({
                            clientValidation: true,
                            url: url,
                            method: 'POST',
                            params: params,
                            success: function (form, action) {
                                XD.msg(Ext.decode(action.response.responseText).msg);
                                thematicProdAddWindow.close();
                                thematicProdGridView.getStore().reload();//.initGrid();
                            },
                            failure: function (form,action) {
                                var respText = Ext.decode(action.response.responseText);
                                XD.msg(respText.msg);
                            }
                        });
                    }
                }
            },
            'thematicProdAddWindow button[itemId=thematicProBackBtnID]': {
                click: function (view, e, eOpts) {
                    var window = view.up('thematicProdAddWindow');
                    window.close();
                }
            },
            'thematicProdDetailGridView button[itemId=leadInID]': {
                click: function (view, e, eOpts) {
                    thematicProdGridView = this.findGridView(view);
                    thematicProdDetailGridView = this.findDetailGridView(view);
                    var select = thematicProdGridView.getSelectionModel();
                    var records = select.getSelection();
                    if (records.length == 0) {
                        XD.msg('请选择专题条目');
                    } else if (records.length > 1) {
                        XD.msg('请选择一条专题条目');
                    } else if (records[0].get("publishstate") == '已发布') {
                        XD.msg('已发布的专题不能进行导入、增加、修改、删除，请取消发布之后再做操作');
                    } else {
                        var items=thematicProdDetailGridView.getStore().data.items;
                        var entryids='';
                        for(var i=0;i<items.length;i++){//获取编研条目里边所有的数据管理条目
                            if(entryids==''){
                                entryids=items[i].get('entryid');
                            }else{
                                entryids+=','+items[i].get('entryid');
                            }
                        }
                        window.leadIn = Ext.create("Ext.window.Window", {
                            width: '100%',
                            height: '100%',
                            title: '办理',
                            modal: true,
                            header: false,
                            draggable: false,//禁止拖动
                            resizable: false,//禁止缩放
                            closeToolText: '关闭',
                            layout: 'fit',
                            items: [{xtype: 'simpleSearchView'}]
                        }).show();
                        window.leadIn.down('simpleSearchGridView').acrossSelections = [];
                        var store=window.leadIn.down('simpleSearchGridView').getStore();
                        store.proxy.extraParams.entryids = entryids;//过滤之前选的entryid
                        window.leadIn.entryids=entryids;
                        store.load();
                        Ext.on('resize', function (a, b) {
                            window.leadIn.setPosition(0, 0);
                            window.leadIn.fitContainer();
                        });
                    }
                }
            },
            'thematicProdDetailGridView button[itemId=addBtnID]': {
                click: function (view, e, eOpts) {
                    thematicProdGridView = this.findGridView(view);
                    thematicProdDetailGridView = this.findDetailGridView(view);
                    var select = thematicProdGridView.getSelectionModel();
                    var records = select.getSelection();
                    if (records.length == 0) {
                        XD.msg('请选择专题条目');
                    } else if (records.length > 1) {
                        XD.msg('请选择一条专题条目');
                    } else if (records[0].get("publishstate") == '已发布') {
                        XD.msg('已发布的专题不能进行导入、增加、修改、删除，请取消发布之后再做操作');
                    } else {
                        window.wmedia = [];
                        Ext.Ajax.request({
                            url:'/infoCompilation/saveThematicDetail',
                            params: {
                                thematicid: thematicProdGridView.getSelectionModel().getSelection()[0].get("thematicid"),
                            },
                            method: 'POST',
                            success: function (resp) {
                                var respText = Ext.decode(resp.responseText);
                                if(respText.success) {
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
                                        items: [{xtype: 'electronicPro'}],
                                    });
                                    window.wform=null;
                                    window.wmedia = 'undefined';
                                    window.leadIn.on('close', function () {
                                        if(window.wmediaName.length==0){
                                            var thematicdetilids = [];
                                            thematicdetilids.push(respText.data);
                                            Ext.Ajax.request({
                                                url: '/infoCompilation/deleteThematicDetail',
                                                params: {
                                                    thematicdetilids: thematicdetilids
                                                },
                                                method: 'POST',
                                                success: function (resp) {
                                                }
                                            });
                                        }else {
                                            Ext.Ajax.request({
                                                url: '/infoCompilation/updateThematicDetail',
                                                params: {
                                                    thematicdetilid: respText.data,
                                                    mediatext: window.wmediaName.join(),
                                                    thematicid: thematicProdGridView.getSelectionModel().getSelection()[0].get("thematicid"),
                                                },
                                                method: 'POST',
                                                success: function (resp) {
                                                    thematicProdDetailGridView.getStore().reload();
                                                }
                                            });
                                        }
                                    });
                                    Ext.on('resize', function (a, b) {
                                        window.leadIn.setPosition(0, 0);
                                        window.leadIn.fitContainer();
                                    });
                                    window.ztid = 'undefined';
                                    window.leadIn.down('electronicPro').initData(respText.data);
                                    window.leadIn.show();
                                }
                            },
                            failure: function () {
                                XD.msg('操作失败');
                            }
                        });
                        // var window1 = Ext.create('ThematicProd.view.ThematicProdDetailAddWindow');
                        // window1.setTitle('增加');
                        // window1.show();
                        // var form = window1.down('form');
                        // window.wform = form;
                    }
                }
            },
            'thematicProdDetailGridView button[itemId=updateBtnID]': {
                click: function (view, e, eOpts) {
                    thematicProdGridView = this.findGridView(view);
                    var select = thematicProdGridView.getSelectionModel();
                    var records = select.getSelection();
                    if (records.length == 0) {
                        XD.msg('请选择专题条目');
                    } else if (records.length > 1) {
                        XD.msg('请选择一条专题条目');
                        } else if (records[0].get("publishstate") == '已发布') {
                        XD.msg('已发布的专题不能进行导入、增加、修改、删除，请取消发布之后再做操作');
                    } else {
                        thematicProdDetailGridView = this.findDetailGridView(view);
                        var select = thematicProdDetailGridView.getSelectionModel();
                        var records = select.getSelection();
                        if (records.length == 0) {
                            XD.msg('请选择数据');
                        } else if (records.length > 1) {
                            XD.msg('修改只能选中一条数据');
                        } else {
                            var window1 = Ext.create('ThematicProd.view.ThematicProdDetailAddWindow');
                            var record = records[0];
                            window1.down('form').loadRecord(record);
                            window1.setTitle('修改');
                            window1.show();
                            window1.down('form').query('[itemId=mediacount]')[0].setText('共' + (record.data.mediatext == '' ? 0 : record.data.mediatext.split(',').length) + '份');
                            window.ztid = record.data.thematicdetilid;
                            var form = window1.down('form');
                            window.wform = form;
                        }
                    }
                }
            },
            'thematicProdDetailGridView button[itemId=deleteBtnID]': {
                click: function (view, e, eOpts) {
                    var thematicProdGridView = this.findGridView(view);
                    var select = thematicProdGridView.getSelectionModel();
                    var records = select.getSelection();
                    if (records.length == 0) {
                        XD.msg('请选择专题条目');
                    } else if (records.length > 1) {
                        XD.msg('请选择一条专题条目');
                    } else if (records[0].get("publishstate") == '已发布') {
                        XD.msg('已发布的专题不能进行导入、增加、修改、删除，请取消发布之后再做操作');
                    } else {
                        thematicProdDetailGridView = this.findDetailGridView(view);
                        var selectDetail = thematicProdDetailGridView.getSelectionModel();
                        var records = selectDetail.getSelection();
                        if (records.length == 0) {
                            XD.msg('请选择数据');
                        } else {
                            XD.confirm('确定要删除这' + records.length + '条数据吗', function () {
                                var thematicdetilids = [];
                                for (var i = 0; i < records.length; i++) {
                                    thematicdetilids.push(records[i].get("thematicdetilid"));
                                }
                                Ext.Ajax.request({
                                    params: {
                                        thematicdetilids: thematicdetilids
                                    },
                                    url: '/infoCompilation/deleteThematicDetail',
                                    method: 'POST',
                                    sync: true,
                                    success: function (resp, opts) {
                                        XD.msg(Ext.decode(resp.responseText).msg);
                                        thematicProdDetailGridView.delReload(records.length);
                                    },
                                    failure: function (resp, opts) {
                                        XD.msg(Ext.decode(resp.responseText).msg);
                                    }
                                });
                            }, this);
                        }
                    }
                }
            },
            'thematicProdDetailGridView button[itemId=seeBtnID]': {
                click: function (view, e, eOpts) {
                    thematicProdDetailGridView = this.findDetailGridView(view);
                    var select = thematicProdDetailGridView.getSelectionModel();
                    var records = select.getSelection();
                    if (records.length == 0) {
                        XD.msg('请选择数据');
                    } else if (records.length > 1) {
                        XD.msg('查看只能选中一条数据');
                    } else {
                        var window1 = Ext.create('ThematicProd.view.ThematicProdDetailAddWindow');
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
            'thematicProdDetailGridView button[itemId=back]': {
                click: function (view, e, eOpts) {
                    this.findView(view).setActiveItem(this.findGridView(view));
                }
            },
            'thematicProdDetailAddWindow button[itemId=saveBtnID]': {
                click: function (view, e, eOpts) {
                    var thematicProdDetailAddWindow = view.up('thematicProdDetailAddWindow');
                    var form = thematicProdDetailAddWindow.down('form');
                    var url, params;
                    if (thematicProdDetailAddWindow.title == '增加') {
                        url = '/infoCompilation/saveThematicDetail';
                        params = {
                            thematicid: thematicProdGridView.getSelectionModel().getSelection()[0].get("thematicid"),
                            mediaids: window.wmedia
                        }
                    } else {
                        url = '/infoCompilation/updateThematicDetail';
                        params = {
                            thematicid: thematicProdGridView.getSelectionModel().getSelection()[0].get("thematicid"),
                            thematicdetilid: thematicProdDetailGridView.getSelectionModel().getSelection()[0].get("thematicdetilid")
                        }
                    }
                    if (form.isValid()) {
                        form.submit({
                            clientValidation: true,
                            url: url,
                            params: params,
                            method: 'POST',
                            success: function (form, action) {
                                var respText = Ext.decode(action.response.responseText);
                                if (respText.success == true) {
                                    XD.msg(respText.msg);
                                    thematicProdDetailAddWindow.close();
                                    thematicProdDetailGridView.getStore().reload();
                                    //thematicProdDetailGridView.initGrid();
                                } else {
                                    XD.msg(respText.msg);
                                }
                            },
                            failure: function () {
                                XD.msg('操作失败');
                            }
                        });
                    }
                }
            },
            'thematicProdDetailAddWindow button[itemId=backBtnID]': {
                click: function (view, e, eOpts) {
                    var window = view.up('thematicProdDetailAddWindow');
                    window.close();
                    var title = window.title;
                    if (title == '修改') {
                        thematicProdDetailGridView.notResetInitGrid();
                        for (var i = 0; i < thematicProdDetailGridView.getStore().getCount(); i++) {
                            thematicProdDetailGridView.getSelectionModel().deselect(thematicProdDetailGridView.getStore().getAt(i));
                        }
                    }
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
                    }
                    if (title == '查看') {
                        window.wmedia = 'undefined';
                        //初始化原文数据
                        for (var i = 0; i < window.leadIn.down('electronicPro').down('toolbar').query('button').length - 1; i++) {
                            window.leadIn.down('electronicPro').down('toolbar').query('button')[i].hide();
                        }
                    } else if (title == '修改') {
                        window.wmedia = 'undefined';
                    }
                    window.leadIn.down('electronicPro').initData(window.ztid);
                    window.leadIn.show();
                }
            },
            'electronicPro': {
                render: function (view) {
                    var buttons = view.down('toolbar').query('button');
                    for (var i = 0; i < buttons.length; i++) {
                        if (buttons[i].text == '上传'||buttons[i].text == '文件夹重命名'||buttons[i].text == '导入专题包' || buttons[i].text == '删除' || buttons[i].text == '返回') {
                            continue;
                        }
                        buttons[i].hide();
                    }
                }
            },
            'simpleSearchView [itemId=simpleSearchSearchfieldId]': {
                search: function (searchfield) {
                    //获取检索框的值
                    var simpleSearchSearchView = searchfield.findParentByType('panel');
                    var condition = simpleSearchSearchView.down('[itemId=simpleSearchSearchComboId]').getValue(); //字段
                    var operator = 'like';//操作符
                    var content = searchfield.getValue(); //内容
                    //检索数据
                    //如果有勾选在结果中检索，则添加检索条件，如果没有勾选，则重置检索条件
                    var grid = simpleSearchSearchView.findParentByType('panel').down('simpleSearchGridView');
                    var gridstore = grid.getStore();
                    //加载列表数据
                    var searchcondition = condition;
                    var searchoperator = operator;
                    var searchcontent = content;
                    var inresult = simpleSearchSearchView.down('[itemId=inresult]').getValue();
                    if (inresult) {
                        var params = gridstore.getProxy().extraParams;
                        if (typeof(params.condition) != 'undefined') {
                            searchcondition = [params.condition, condition].join(XD.splitChar);
                            searchoperator = [params.operator, operator].join(XD.splitChar);
                            searchcontent = [params.content, content].join(XD.splitChar);
                        }
                    }

                    grid.dataParams = {
                        condition: searchcondition,
                        operator: searchoperator,
                        entryids:window.leadIn.entryids,
                        content: searchcontent
                    };

                    //检索数据前,修改column的renderer，将检索的内容进行标红
                    Ext.Array.each(grid.getColumns(), function () {
                        var column = this;
                        if (column.dataIndex == condition) {
                            column.renderer = function (value) {
                                var contentData = content.split(' ');//切割以空格分隔的多个关键词
                                var reg = new RegExp(contentData.join('|'), 'g');
                                return value.replace(reg, function (match) {
                                    return '<span style="color:red">' + match + '</span>';
                                });
                            }
                        }
                    });
                    grid.initGrid();
                    grid.parentXtype = 'simpleSearchView';
                    grid.formXtype = 'EntryFormView';
                }
            },
            'simpleSearchGridView [itemId=simpleSearchShowId]': {
                click: function (btn) {
                    var simpleSearchGridView = btn.findParentByType('simpleSearchGridView');
                    var record = simpleSearchGridView.getSelectionModel().getSelection();
                    if (record.length == 0) {
                        XD.msg('请至少选择一条需要查看的数据');
                        return;
                    }
                    var entryids = [];
                    var nodeids = [];
                    for(var i=0;i<record.length;i++){
                        entryids.push(record[i].get('entryid'));
                        nodeids.push(record[i].get('nodeid'));
                    }
                    var entryid = record[0].get('entryid');
                    var form = this.findFormView(btn).down('dynamicform');
                    form.operate = 'look';
                    form.entryids = entryids;
                    form.nodeids = nodeids;
                    form.entryid = entryids[0];
                    this.initFormField(form, 'hide', record[0].get('nodeid'));
                    this.initFormData('look', form, entryid);
                }
            },
            'simpleSearchGridView [itemId=simpleSearchBackId]': {
                click: function (btn) {
                    if (window.leadIn != null) {
                        window.leadIn.setVisible(false);
                    }
                }
            },
            'simpleSearchGridView': {
                rowdblclick: function (view, record) {
                    var entryid = record.get('entryid');
                    var form = this.findFormView(view).down('dynamicform');
                    this.initFormField(form, 'hide', record.get('nodeid'));
                    this.initFormData('look', form, entryid);
                }
            },
            'simpleSearchGridView ': {
                eleview: this.activeEleForm
            },
            'simpleSearchGridView button[itemId=searchleadinId]': {
                click: function (view, e, eOpts) {
                    var simpleSearchGridView = view.findParentByType('simpleSearchGridView');
                    var select = simpleSearchGridView.getSelectionModel();
                    if (!select.hasSelection()) {
                        XD.msg('请选择需要导入的数据');
                        return;
                    }

                    var datas = select.getSelection();
                    var array = [];
                    for (var i = 0; i < datas.length; i++) {
                        array[i] = datas[i].get('entryid');
                    }

                    Ext.Ajax.request({
                        params: {
                            dataids: array,
                            treeid: thematicProdGridView.getSelectionModel().getSelection()[0].get("thematicid")
                        },
                        url: '/infoCompilation/searchleadin',
                        method: 'POST',
                        sync: true,
                        success: function (resp, opts) {
                            var respText = Ext.decode(resp.responseText);
                            XD.msg(respText.msg);
                            // window.leadIn.close();
                            window.leadIn.setVisible(false);
                            thematicProdDetailGridView.initGrid();
                        },
                        failure: function () {
                            XD.msg('操作失败');
                        }
                    });

                }
            },
            'simpleSearchView [itemId=topCloseBtn]': {
                click: function (view) {
                    // window.leadIn.close();
                    window.leadIn.setVisible(false);
                }
            },
            'EntryFormView [itemId=preBtn]':{
                click:this.preHandler
            },
            'EntryFormView [itemId=nextBtn]':{
                click:this.nextHandler
            },
            'EntryFormView [itemId=back]': {
                click: function (btn) {
                    this.activeGrid(btn, false);
                }
            },'thematicProdGridView button[itemId=releaseResourceId]':{//发布数字资源
                click:function (btn) {
                    var grid = btn.up('thematicProdGridView');
                    var records = grid.getSelectionModel().getSelection();
                    if (records.length == 0){
                        XD.msg('请至少选择一条数据');
                        return;
                    }
                    var thematicArr = new Array();
                    Ext.each(records,function (item) {
                        thematicArr.push(item.get('thematicid'))
                    })
                    var thematicIds = thematicArr.join();
                    //location.href = '/export/exportReleaseThematicResurce?thematicIds='+ thematicIds;
                    location.href = '/thematicProd/releasenetworkMake?thematicids='+thematicIds;
                }
            },
            'thematicProdGridView button[itemId=releasenetwork]':{//发布到政务网
                click:function (btn) {
                    var grid = btn.up('thematicProdGridView');
                    var records = grid.getSelectionModel().getSelection();
                    if (records.length == 0){
                        XD.msg('请至少选择一条数据');
                        return;
                    }
                    var isrelease =true;
                    var isTitleerepeat =false;
                    var thematicArr = new Array();
                    var thematicTitle = new Array();
                    Ext.each(records,function (item) {
                        thematicArr.push(item.get('thematicid'));
                        thematicTitle.push(item.get('title'));
                        if (item.get('publishstate') != '已发布'){
                            isrelease = false
                        }
                    })
                    var nary=thematicTitle.sort();
                    for(var i=0;i<thematicTitle.length;i++){
                        if (nary[i]==nary[i+1]){
                            isTitleerepeat = true;
                        }
                    }
                    if (!isrelease){
                        XD.msg('发布失败，发布内容必须是已发布的专题！');
                        return;
                    }
                    if (isTitleerepeat){
                        XD.msg('发布失败，有重复专题！');
                        return;
                    }
                    var thematicIds = thematicArr.join(',');
                    var thematicProdDocFormView=new Ext.create("ThematicProd.view.ThematicProdDocFormView");
                    thematicProdDocFormView.thematicIds = thematicIds;
                    var docform=thematicProdDocFormView.down('[itemId=formitemid]');
                    docform.load({
                        url: '/thematicMake/getThematicDoc',
                        params: {
                            thematicIds: thematicIds
                        },
                        success: function (form, action) {},
                        failure: function () {XD.msg('操作中断');}
                    });
                    thematicProdDocFormView.show();
                }
            },
            'thematicProdDocFormView [itemId=releaseID]': {//发布
                click: this.release
            }
        });
    },
    //获取专题制作应用视图
    findView: function (btn) {
        return btn.findParentByType('thematicProdView');
    },
    findGridView: function (btn) {
        return this.findView(btn).down('thematicProdGridView');
    },
    findDetailGridView: function (btn) {
        return this.findView(btn).down('thematicProdDetailGridView');
    },

    //获取简单检索应用视图
    findSearchView: function (btn) {
        return btn.up('simpleSearchView');
    },

    //获取简单检索查看动态表单界面视图
    findFormView: function (btn) {
        return this.findSearchView(btn).down('EntryFormView');
    },
    //获取简单检索列表界面视图
    findSearchGridView: function (btn) {
        return this.findSearchView(btn).down('simpleSearchGridView');
    },
    //切换到简单检索列表界面视图
    activeGrid: function (btn, flag) {
        var view = this.findSearchView(btn);
        var grid = this.findSearchGridView(btn);
        view.setActiveItem(view.down('[itemId=gridview]'));
        if (document.getElementById('mediaFrame')) {
            document.getElementById('mediaFrame').setAttribute('src', '');
        }
        if (document.getElementById('solidFrame')) {
            document.getElementById('solidFrame').setAttribute('src', '');
        }
        // if (document.getElementById('longFrame')) {
        //     document.getElementById('longFrame').setAttribute('src', '');
        // }
        if (flag) {//根据参数确定是否需要刷新数据
            grid.notResetInitGrid();
        }
    },
    //切换到简单检索查看动态表单界面视图
    activeForm: function (btn) {
        var view = this.findSearchView(btn);
        var formview = this.findFormView(btn);
        view.setActiveItem(formview);
        formview.items.get(0).enable();
        formview.setActiveTab(0);
        return formview;
    },
    activeEleForm: function (obj) {
        var view = this.findSearchView(obj.grid);
        var formview = this.findFormView(obj.grid);
        view.setActiveItem(formview);
        formview.items.get(0).disable();
        var eleview = formview.down('electronic');
        var solidview = formview.down('solid');
        eleview.operateFlag = "look"; //电子文件查看标识符
        solidview.operateFlag = "look";//利用文件查看标识符
        eleview.initData(obj.entryid);
        solidview.initData(obj.entryid);
        var from =formview.down('dynamicform');
        //电子文件按钮权限
        var elebtns = eleview.down('toolbar').query('button');
        from.getELetopBtn(elebtns,eleview.operateFlag );
        var soildbtns = solidview.down('toolbar').query('button');
        from.getELetopBtn(soildbtns,solidview.operateFlag);
        formview.setActiveTab(1);
        return formview;
    },

    initFormField: function (form, operate, nodeid) {
//        if (form.nodeid != nodeid) {
            form.nodeid = nodeid;//用左侧树节点的id初始化form的nodeid参数
            form.removeAll();//移除form中的所有表单控件
            var field = {
                xtype: 'hidden',
                name: 'entryid'
            };
            form.add(field);
            var formField = form.getFormField();//根据节点id查询表单字段
            if(formField.length==0){
                XD.msg('请检查模板设置信息是否正确');
                return;
            }
            form.templates = formField;
            form.initField(formField,operate);//重新动态添加表单控件
//        }
        return '加载表单控件成功';
    },

    getCurrentThematicProdform:function (btn) {
        return btn.up('EntryFormView');
    },

    //点击上一条
    preHandler:function(btn){
        var currentThematicProdform = this.getCurrentThematicProdform(btn);
        var form = currentThematicProdform.down('dynamicform');
        this.refreshFormData(form, 'pre');
    },

    //点击下一条
    nextHandler:function(btn){
        var currentThematicProdform = this.getCurrentThematicProdform(btn);
        var form = currentThematicProdform.down('dynamicform');
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


    initFormData: function (operate, form, entryid) {
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
        this.activeForm(form);
        Ext.Ajax.request({
            method: 'GET',
            scope: this,
            url: '/management/entries/' + entryid,
            success: function (response) {
                if (operate != 'look') {
                    var settingState = ifSettingCorrect(form.nodeid, form.templates);
                    if (!settingState) {
                        return;
                    }
                }
                var entry = Ext.decode(response.responseText);
                form.loadRecord({
                    getData: function () {
                        return entry;
                    }
                });
                //字段编号，用于特殊的自定义字段(范围型日期)
                var fieldCode = form.getRangeDateForCode();
                if (fieldCode != null) {
                    //动态解析数据库日期范围数据并加载至两个datefield中
                    form.initDaterangeContent(entry);
                }
                //初始化原文数据
                var eleview = formview.down('electronic');
                eleview.initData(entryid);
                var solidview = formview.down('solid');
                solidview.initData(entryid);
                // var longview = formview.down('long');
                // longview.initData(entryid);
//                form.formStateChange(operate);
                form.fileLabelStateChange(eleview, operate);
                form.fileLabelStateChange(solidview, operate);
                // form.fileLabelStateChange(longview, operate);
            }
        });
    },
    release: function (btn) {
        var thematicProdDocFormView = btn.up('thematicProdDocFormView');
        XD.confirm('确定要发布吗?', function () {
            Ext.MessageBox.wait('正在发布请稍后...', '提示');
            thematicProdDocFormView.down('[itemId=formitemid]').submit({
                url: '/thematicMake/releasenetwork',
                method: 'post',
                params:{
                    thematicIds:thematicProdDocFormView.thematicIds
                },
                success: function (form, action) {
                    location.href = '/thematicMake/downLoadReleasenetwork';
                    XD.msg('发布成功');
                    Ext.MessageBox.hide();
                    thematicProdDocFormView.close();
                },
                failure: function () {
                    Ext.MessageBox.hide();
                    XD.msg('操作失败');
                }
            });

        });
    }
});

function ifCodesettingCorrect(nodeid) {
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
}

function ifSettingCorrect(nodeid, templates) {
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
}