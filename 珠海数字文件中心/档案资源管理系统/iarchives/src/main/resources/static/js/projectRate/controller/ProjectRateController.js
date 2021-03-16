/**
 * Created by Administrator on 2020/5/9.
 */


Ext.define('ProjectRate.controller.ProjectRateController', {
    extend: 'Ext.app.Controller',

    views: ['ProjectRateGridView','ProjectRateView','ProjectRateLookView',
        'ProjectRateLookFormView','ProjectLogLookGridView'],//加载view
    stores: ['ProjectLogLookGridStore'],//加载store
    models: ['ProjectLogLookGridModel'],//加载model
    init: function () {
        this.control({
            'projectRateGridView button[itemId=look]':{   //查看
                click:function (view) {
                    var projectRateGridView = view.findParentByType('projectRateGridView');
                    var select = projectRateGridView.getSelectionModel().getSelection();
                    if(select.length < 1){
                        XD.msg('请选择一条数据');
                        return;
                    }
                    if(select.length !=1){
                        XD.msg('只能选择一条数据');
                        return;
                    }
                    var projectAddLookView = Ext.create('ProjectRate.view.ProjectRateLookView');
                    var form = projectAddLookView.down('form');
                    var projectLogLookGridView = projectAddLookView.down('projectLogLookGridView');
                    projectLogLookGridView.initGrid({id:select[0].get('id')});
                    form.load({
                        url:'/projectRate/getProjectManageByid',
                        method:'GET',
                        params:{
                            id:select[0].get('id')
                        },
                        success:function () {
                        },
                        failure:function () {
                            XD.msg('获取表单信息失败');
                        }
                    });
                    projectAddLookView.show();
                }
            },

            'projectRateLookFormView button[itemId=close]':{  //查看 关闭
                click:function (view) {
                    view.findParentByType('window').close();
                }
            },

            'EntryFormView [itemId=preBtn]':{  //上一条
                click:this.preHandler
            },

            'EntryFormView [itemId=nextBtn]':{ //下一条
                click:this.nextHandler
            },

            'EntryFormView [itemId=back]':{ //返回
                click:function (view) {
                    var projectRateView = view.findParentByType('projectRateView');
                    var projectRateGridView = projectRateView.down('projectRateGridView');
                    projectRateView.setActiveItem(projectRateGridView);
                }
            },

            'projectRateGridView button[itemId=print]':{   //打印
                click:function (view) {
                    var projectRateGridView = view.findParentByType('projectRateGridView');
                    var select = projectRateGridView.getSelectionModel().getSelection();
                    if(select.length < 1){
                        XD.msg('请至少选择一条数据');
                        return;
                    }
                    var ids = [];
                    var params = {};
                    for (var i = 0; i < select.length; i++) {
                        ids.push(select[i].get('id').trim());
                    }
                    if(reportServer == 'UReport') {
                        params['id'] = ids.join(",");
                        XD.UReportPrint(null, '项目工作进度表', params);
                    }
                    else if(reportServer == 'FReport') {
                        XD.FRprint(null, '项目工作进度表', ids.length > 0 ? "'id':'" + ids.join(",") + "'" : '')  ;
                    }
                }
            }
        });
    },

    //点击上一条
    preHandler:function(btn){
        var formView = btn.findParentByType('EntryFormView');
        var form = formView.down('dynamicform');
        this.preNextHandler(form, 'pre');
    },

    //点击下一条
    nextHandler:function(btn){
        var formView = btn.findParentByType('EntryFormView');
        var form = formView.down('dynamicform');
        this.preNextHandler(form, 'next');
    },

    //条目切换，上一条下一条
    preNextHandler:function(form,type){
        this.refreshFormData(form, type);
    },

    refreshFormData:function(form, type){
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
        if(form.operate != 'undefined'){
            this.initFormData(form.operate, form, entryid);
            return;
        }
        this.initFormData('look', form, entryid);
    },

    initFormField: function (form, operate, nodeid) {
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
        return '加载表单控件成功';
    },

    initFormData: function (operate, form, entryid, state) {
        var nullvalue = new Ext.data.Model();
        var fields = form.getForm().getFields().items;
        var prebtn = form.down('[itemId=preBtn]');
        var nextbtn = form.down('[itemId=nextBtn]');
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
            if (fields[i].value && typeof(fields[i].value) == 'string' && fields[i].value.indexOf('label') > -1) {
                continue;
            }
            if (fields[i].xtype == 'combobox') {
                fields[i].originalValue = null;
            }
            nullvalue.set(fields[i].name, null);
        }
        form.loadRecord(nullvalue);
        Ext.each(fields, function (item) {
            item.setReadOnly(true);
        });
        var eleview = form.up('EntryFormView').down('electronic');
        var solidview = form.up('EntryFormView').down('solid');
        Ext.Ajax.request({
            method: 'GET',
            scope: this,
            url: '/management/entries/' + entryid,
            success: function (response) {
                var entry = Ext.decode(response.responseText);
                if (operate == 'lookfile') {
                    prebtn.setVisible(false);
                    nextbtn.setVisible(false);
                }
                var data = Ext.decode(response.responseText);
                if (data.organ) {
                    entry.organ = data.organ;//机构
                }
                form.loadRecord({
                    getData: function () {
                        return entry;
                    }
                });
                var fieldCode = form.getRangeDateForCode();//字段编号，用于特殊的自定义字段(范围型日期)
                if (fieldCode != null) {
                    //动态解析数据库日期范围数据并加载至两个datefield中
                    form.initDaterangeContent(entry);
                }
                eleview.initData(entry.entryid);
                solidview.initData(entry.entryid);
            }
        });
        form.fileLabelStateChange(eleview, operate);
        form.fileLabelStateChange(solidview, operate);
    }
});

