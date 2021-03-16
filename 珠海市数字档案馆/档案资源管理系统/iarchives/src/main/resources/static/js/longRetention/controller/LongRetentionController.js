/**
 * Created by yl on 2017/10/25.
 */
var nodeid;
Ext.define('LongRetention.controller.LongRetentionController', {
    extend: 'Ext.app.Controller',
    views: [
        'LongRetentionView', 'LongRetentionGridView', 'LongRetentionDetailView',
        'LongRetentionPackageView', 'LongRetentionTimeSetView','LongRetentionSetting'
    ],//加载view
    stores: [
        'LongRetentionTreeStore', 'LongRetentionGridStore'
    ],//加载store
    models: [
        'LongRetentionTreeModel', 'LongRetentionGridModel', 'LongRetentionResultGridModel'
    ],//加载model
    init: function () {
        this.control({
            'longRetentionView [itemId=treepanelId]': {
                select: function (treemodel, record) {
                    var gridcard = this.findView(treemodel.view).down('[itemId=gridcard]');
                    var onlygrid = gridcard.down('[itemId=onlygrid]');
                    var nodeType = record.data.nodeType;
                    var bgSelectOrgan = gridcard.down('[itemId=bgSelectOrgan]');
                    nodeid = record.get('fnid');
                    //树节点为分类，更改右边页面为“请选择机构节点”
                    if (nodeType == 2) {
                        gridcard.setActiveItem(bgSelectOrgan);
                    } else {
                        gridcard.setActiveItem(onlygrid);
                        onlygrid.setTitle("当前位置：" + record.data.text);
                        onlygrid.initGrid({nodeid: nodeid});
                    }

                }
            },
            'longRetentionGridView [itemId=look]': {//查看
                click: this.lookHandler
            },
            'longRetentionGridView [itemId=implement]': {//执行验证
                click: this.implementHandler
            },
            'longRetentionGridView [itemId=lookdetail]': {//查看验证明细
                click: this.lookdetailsHandler
            },
            'longRetentionGridView [itemId=reset]': {//状态重置
                click: this.resetHandler
            },
            'longRetentionGridView [itemId=lookpacket]': {//查看数据包
                click: this.lookPackpageHandler
            },
            'longRetentionGridView [itemId=download]': {//下载数据包
                click: this.downloadLongRetention
            },
            'longRetentionGridView [itemId=set]': {//设置定时任务
                click: this.setTimeJob
            },
            'longRetentionGridView [itemId=validationset]': {//验证项设置
                click: this.validationset
            },
            'EntryFormView [itemId=back]': {
                click: function (btn) {
                    this.activeGrid(btn, true);
                }
            },
            'packageWindow [itemId=treepanelId]': {
                render: function (view) {
                    view.getRootNode().on('expand', function (node) {
                        node.getOwnerTree().getSelectionModel().select(node.childNodes[0]);
                    })
                },
                select: function (treemodel, record) {
                    if (record.get('children') != null) {
                        var dataview = this.findPackageWindow(treemodel.view).down('[itemId=dataview]');
                        var childrens = [];
                        for (var i = 0; i < record.get('children').length; i++) {
                            childrens.push(record.get('children')[i].text);
                        }
                        dataview.getStore().proxy.extraParams.childrens = childrens;
                        dataview.getStore().reload();
                    }
                    if (record.data.text.indexOf('.xml') > -1) {
                        Ext.MessageBox.wait('正在解析获取元数据...', '提示');
                        var form = this.findMetadataForm(treemodel.view).getForm();
                        setTimeout(function () {
                            Ext.Ajax.request({
                                url: '/longRetention/getMetadata',
                                params: {
                                    entryid: window.entryid,
                                    xmlName: record.data.text
                                },
                                success: function (response) {
                                    Ext.MessageBox.hide();
                                    var responseText = Ext.decode(response.responseText);
                                    form.reset();
                                    form.setValues(responseText);
                                }
                            });
                        }, 100);
                    }
                }
            },
            'longRetentionTimeSetView [itemId=stop]': {
                click: this.stopHandler
            },
            'longRetentionTimeSetView [itemId=start]': {
                click: this.startHandler
            },
            'longRetentionTimeSetView [itemId=save]': {
                click: this.saveHandler
            },
            'longRetentionSetting [itemId=saveId]': {
                click: this.settingSaveHandler
            }
        });
    },
    findView: function (btn) {
        return btn.up('longRetentionView');
    },
    findGridView: function (btn) {
        return this.findView(btn).down('[itemId=gridview]');
    },
    findOnlygridView: function (btn) {
        return this.findView(btn).down('[itemId=onlygrid]');
    },
    //获取表单界面视图
    findFormView: function (btn) {
        return this.findView(btn).down('EntryFormView');
    },
    findPackageWindow: function (btn) {
        return btn.up('packageWindow');
    },
    findMetadataForm: function (btn) {
        return this.findPackageWindow(btn).down('[itemId=metadataForm]');
    },
    //查看
    lookHandler: function (btn) {
        var grid = this.findOnlygridView(btn);
        var record = grid.selModel.getSelection();
        var tree = this.findView(btn).down('treepanel');
        var node = tree.selModel.getSelected().items[0];
        if (!node) {
            XD.msg('请选择节点');
            return;
        }
        if (grid.down('[itemId=selectAll]').checked) {
            XD.msg('请选择一条需要查看的数据');
            return;
        }
        if (record.length == 0) {
            XD.msg('请至少选择一条需要查看的数据');
            return;
        }
        var entryids = [];
        var nodeids = [];
        for (var i = 0; i < record.length; i++) {
            entryids.push(record[i].get('entryid'));
            nodeids.push(record[i].get('nodeid'));
        }
        var entryid = record[0].get('entryid');
        var form = this.findFormView(btn).down('dynamicform');
        form.operate = 'look';
        form.entryids = entryids;
        form.nodeids = nodeids;
        form.entryid = entryids[0];
        var initFormFieldState = this.initFormField(form, 'hide', node.get('fnid'));
        if (!initFormFieldState) {//表单控件加载失败
            return;
        }
        this.initFormData('look', form, entryid);
    },
    //执行验证
    implementHandler: function (btn) {
        var grid = this.findOnlygridView(btn);
        var tree = this.findView(btn).down('treepanel');
        var node = tree.selModel.getSelected().items[0];
        if (!node) {
            XD.msg('请选择节点');
            return;
        }
        var selectAll;
        var selLen;
        if (grid.selModel != null) {
            selectAll = grid.down('[itemId=selectAll]').checked;
            if (grid.selModel.getSelectionLength() == 0) {
                XD.msg('请至少选择一条需要验证的数据');
                return;
            }
            selLen = grid.selModel.getSelectionLength();
        } else {
            if (grid.acrossSelections.length == 0) {
                XD.msg('请至少选择一条需要验证的数据');
                return;
            }
            selLen = grid.acrossSelections.length;
        }
        XD.confirm('确定要执行验证这' + selLen + '条数据吗', function () {
            Ext.MessageBox.wait('正在进行数据包安全认证...', '提示');
            var record = grid.selModel.getSelection();
            var isSelectAll = false;
            if (selectAll) {
                record = grid.acrossDeSelections;
                isSelectAll = true;
            }
            var tmp = [];
            for (var i = 0; i < record.length; i++) {
                tmp.push(record[i].get('entryid'));
            }
            var entryids = tmp.join(",");
            var tempParams = grid.getStore().proxy.extraParams;
            tempParams['entryids'] = entryids;
            tempParams['isSelectAll'] = isSelectAll;
            Ext.Ajax.request({
                url: '/longRetention/verification',
                method: 'post',
                scope: this,
                params: tempParams,
                timeout: XD.timeout,
                success: function (res) {
                    XD.msg('执行验证成功');
                    Ext.MessageBox.hide();
                    grid.notResetInitGrid();
                }
            });
        }, this);
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
            XD.msg('请检查模板设置信息是否正确');
            return;
        }
        form.templates = formField;
        form.initField(formField, operate);//重新动态添加表单控件
//        }
        return '加载表单控件成功';
    },
    initFormData: function (operate, form, entryid) {
        var formview = form.up('EntryFormView');
        var nullvalue = new Ext.data.Model();
        var fields = form.getForm().getFields().items;
        if (operate == 'look') {
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

            Ext.each(fields, function (item) {
                item.setReadOnly(true);
            });
        } else {
            Ext.each(fields, function (item) {
                if (!item.freadOnly) {
                    item.setReadOnly(false);
                }
            });
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
        var etips = formview.down('[itemId=etips]');
        etips.show();
        if (operate != 'look') {
            var settingState = ifSettingCorrect(form.nodeid, form.templates);
            if (!settingState) {
                return;
            }
        }
        this.activeForm(form);
        var eleview = this.findFormView(form).down('electronic');
        var solidview = this.findFormView(form).down('solid');
        // var longview = this.findFormView(form).down('long');
        if (typeof (entryid) != 'undefined') {
            Ext.Ajax.request({
                method: 'GET',
                scope: this,
                url: '/management/entries/' + entryid,
                success: function (response, opts) {
                    var entry = Ext.decode(response.responseText);
                    if (operate == 'add') {
                        delete entry.entryid;
                    }
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
                    eleview.initData(entry.entryid);
                    solidview.initData(entry.entryid);
                    // longview.initData(entry.entryid);
                }
            });
        } else {
            eleview.initData();
            solidview.initData();
            // longview.initData();
        }
//        form.formStateChange(operate);
        form.fileLabelStateChange(eleview, operate);
        form.fileLabelStateChange(solidview, operate);
        // form.fileLabelStateChange(longview,operate);
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
    },//切换到表单界面视图
    activeForm: function (btn) {
        var view = this.findView(btn);
        var formview = this.findFormView(btn);
        view.setActiveItem(formview);
        formview.items.get(0).enable();
        formview.setActiveTab(0);
        return formview;
    },
    //切换到列表界面视图
    activeGrid: function (btn, flag) {
        var view = this.findView(btn);
        view.setActiveItem(this.findGridView(btn));
        if (document.getElementById('mediaFrame')) {
            document.getElementById('mediaFrame').setAttribute('src', '');
        }
        if (document.getElementById('solidFrame')) {
            document.getElementById('solidFrame').setAttribute('src', '');
        }
    },
    lookdetailsHandler: function (btn) {
        var grid = this.findOnlygridView(btn);
        var record = grid.selModel.getSelection();
        var tree = this.findView(btn).down('treepanel');
        var node = tree.selModel.getSelected().items[0];
        if (!node) {
            XD.msg('请选择节点');
            return;
        }
        if (grid.down('[itemId=selectAll]').checked) {
            XD.msg('请选择一条需要查看的数据');
            return;
        }
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
        var win = Ext.create('LongRetention.view.LongRetentionDetailView');
        win.down('[itemId=closeBtn]').on('click', function () {
            win.close()
        });
        win.down('[itemId=authenticity]').html = record[0].data.authenticity;
        win.down('[itemId=integrity]').html = record[0].data.integrity;
        win.down('[itemId=usability]').html = record[0].data.usability;
        win.down('[itemId=safety]').html = record[0].data.safety;
        win.show();
    },
    resetHandler: function (btn) {
        var grid = this.findOnlygridView(btn);
        var tree = this.findView(btn).down('treepanel');
        var node = tree.selModel.getSelected().items[0];
        if (!node) {
            XD.msg('请选择节点');
            return;
        }
        var selectAll;
        var selLen;
        if (grid.selModel != null) {
            selectAll = grid.down('[itemId=selectAll]').checked;
            if (grid.selModel.getSelectionLength() == 0) {
                XD.msg('请至少选择一条需要重置的数据');
                return;
            }
            selLen = grid.selModel.getSelectionLength();
        } else {
            if (grid.acrossSelections.length == 0) {
                XD.msg('请至少选择一条需要重置的数据');
                return;
            }
            selLen = grid.acrossSelections.length;
        }
        XD.confirm('确定要重置这' + selLen + '条数据吗', function () {
            Ext.MessageBox.wait('正在进行数据包重置...', '提示');
            var record = grid.selModel.getSelection();
            var isSelectAll = false;
            if (selectAll) {
                record = grid.acrossDeSelections;
                isSelectAll = true;
            }
            var tmp = [];
            for (var i = 0; i < record.length; i++) {
                tmp.push(record[i].get('entryid'));
            }
            var entryids = tmp.join(",");
            var tempParams = grid.getStore().proxy.extraParams;
            tempParams['entryids'] = entryids;
            tempParams['isSelectAll'] = isSelectAll;
            Ext.Ajax.request({
                url: '/longRetention/deleteLongRetention',
                method: 'post',
                scope: this,
                params: tempParams,
                timeout: XD.timeout,
                success: function (resp, opts) {
                    Ext.MessageBox.hide();
                    XD.msg(Ext.decode(resp.responseText).msg);
                    grid.notResetInitGrid();
                },
                failure: function (resp, opts) {
                    Ext.MessageBox.hide();
                    XD.msg(Ext.decode(resp.responseText).msg);
                }
            });
        }, this);
    },
    lookPackpageHandler: function (btn) {
        var grid = this.findOnlygridView(btn);
        var record = grid.selModel.getSelection();
        var tree = this.findView(btn).down('treepanel');
        var node = tree.selModel.getSelected().items[0];
        if (!node) {
            XD.msg('请选择节点');
            return;
        }
        if (grid.down('[itemId=selectAll]').checked) {
            XD.msg('请选择一条需要查看的数据');
            return;
        }
        if (record.length == 0) {
            XD.msg('请选择一条需要查看的数据');
            return;
        }
        window.entryid = record[0].get('entryid');
        Ext.Ajax.request({
            params: {
                entryid: window.entryid
            },
            url: '/longRetention/checkFile',
            success: function (resp) {
                var respText = Ext.decode(resp.responseText);
                if (respText.success == true) {
                    var win = Ext.create('LongRetention.view.LongRetentionPackageView');
                    win.show();
                } else {
                    XD.msg(respText.msg);
                }
            },
            failure: function (resp) {
                XD.msg('操作失败');
            }
        });

    },
    //下载长期保管包
    downloadLongRetention: function (btn) {
        var grid = this.findOnlygridView(btn);
        var tree = this.findView(btn).down('treepanel');
        var node = tree.selModel.getSelected().items[0];
        if (!node) {
            XD.msg('请选择节点');
            return;
        }
        var selectAll;
        var selLen;
        if (grid.selModel != null) {
            selectAll = grid.down('[itemId=selectAll]').checked;
            if (grid.selModel.getSelectionLength() == 0) {
                XD.msg('请至少选择一条需要下载的数据');
                return;
            }
            selLen = grid.selModel.getSelectionLength();
        } else {
            if (grid.acrossSelections.length == 0) {
                XD.msg('请至少选择一条需要下载的数据');
                return;
            }
            selLen = grid.acrossSelections.length;
        }
        XD.confirm('确定要下载这' + selLen + '个封装包吗', function () {
            Ext.MessageBox.wait('正在下载...', '提示');
            var record = grid.selModel.getSelection();
            var isSelectAll = false;
            if (selectAll) {
                record = grid.acrossDeSelections;
                isSelectAll = true;
            }
            var tmp = [];
            for (var i = 0; i < record.length; i++) {
                tmp.push(record[i].get('entryid'));
            }
            //判断是否选择全选按钮
            if (selectAll) {
                var entryids = tmp.join(",");
                var tempParams = grid.getStore().proxy.extraParams;
                tempParams['entryids'] = entryids;
                tempParams['isSelectAll'] = isSelectAll;
                Ext.Ajax.request({
                    url: '/longRetention/getSelectAllData',
                    method: 'post',
                    scope: this,
                    params: tempParams,
                    timeout: XD.timeout,
                    success: function (res) {
                        var records = Ext.decode(res.responseText);
                        for (var i = 0; i < records.length; i++) {
                            setTimeout(function (entryid) {
                                var downloadForm = document.createElement("form");
                                document.body.appendChild(downloadForm);
                                downloadForm.action = '/export/createLongRetention';
                                var inputTextElement = document.createElement('input');
                                inputTextElement.name = "entryid";
                                inputTextElement.value = entryid;
                                downloadForm.appendChild(inputTextElement);
                                downloadForm.method = 'POST';
                                downloadForm.submit();
                            }, i * 300, records[i]);
                        }
                        Ext.MessageBox.hide();
                        XD.msg('下载完成');
                    }
                });
            } else {
                for (var i = 0; i < tmp.length; i++) {
                    setTimeout(function (entryid) {
                        var downloadForm = document.createElement("form");
                        document.body.appendChild(downloadForm);
                        downloadForm.action = '/export/createLongRetention';
                        var inputTextElement = document.createElement('input');
                        inputTextElement.name = "entryid";
                        inputTextElement.value = entryid;
                        downloadForm.appendChild(inputTextElement);
                        downloadForm.method = 'POST';
                        downloadForm.submit();
                    }, i * 300, tmp[i]);
                }
                Ext.MessageBox.hide();
                XD.msg('下载完成');
            }
        }, this);
    },
    setTimeJob: function (btn) {
        var win = new Ext.create({
            xtype: 'longRetentionTimeSetView'
        });
        var form = win.down('[itemId=formitemid]');
        form.load({
            url: '/longRetention/getTimeJob',
            params: {
                jobname: '长期保管'
            },
            success: function (form, action) {
                var jobstate = action.result.data.jobstate;
                var runcycle = action.result.data.runcycle;
                var monthly = win.down('[itemId=monthlyid]');
                var weekly = win.down('[itemId=weeklyid]');
                var stop = win.down('[itemId=stop]');
                var start = win.down('[itemId=start]');
                if (runcycle =='day'){
                    monthly.setDisabled(true);
                    weekly.setDisabled(true);
                }else if (runcycle =='month'){
                    monthly.setDisabled(false);
                    weekly.setDisabled(true);
                }else{
                    monthly.setDisabled(true);
                    weekly.setDisabled(false);
                }
                if (jobstate == '0') {
                    form.findField('jobstate').setValue('未开启');
                    stop.hide();
                    start.show();
                } else {
                    form.findField('jobstate').setValue('已开启');
                    stop.show();
                    start.hide();
                }
            },
            failure: function () {
                XD.msg('操作失败');
            }
        });
        win.show();
    },
    validationset: function (btn) {
        var win = new Ext.create({
            xtype: 'longRetentionSetting'
        });
        Ext.Ajax.request({
            method: 'GET',
            scope: this,
            url: '/longRetention/getSetting',
            params: {
                nodeid: nodeid
            },
            success: function (response, opts) {
                var setting = Ext.decode(response.responseText);
                // 准确性
                if (setting.data.authenticity.indexOf(win.down('[itemId=authenticity1]').boxLabel)!=-1){
                    win.down('[itemId=authenticity1]').setValue(true)
                }else{
                    win.down('[itemId=authenticity1]').setValue(false)
                }
                if (setting.data.authenticity.indexOf(win.down('[itemId=authenticity2]').boxLabel)!=-1){
                    win.down('[itemId=authenticity2]').setValue(true)
                }else{
                    win.down('[itemId=authenticity2]').setValue(false)
                }
                if (setting.data.authenticity.indexOf(win.down('[itemId=authenticity3]').boxLabel)!=-1){
                    win.down('[itemId=authenticity3]').setValue(true)
                }else{
                    win.down('[itemId=authenticity3]').setValue(false)
                }
                if (setting.data.authenticity.indexOf(win.down('[itemId=authenticity4]').boxLabel)!=-1){
                    win.down('[itemId=authenticity4]').setValue(true)
                }else{
                    win.down('[itemId=authenticity4]').setValue(false)
                }
                //完整性
                if (setting.data.integrity.indexOf(win.down('[itemId=integrity1]').boxLabel)!=-1){
                    win.down('[itemId=integrity1]').setValue(true)
                }else{
                    win.down('[itemId=integrity1]').setValue(false)
                }
                if (setting.data.integrity.indexOf(win.down('[itemId=integrity2]').boxLabel)!=-1){
                    win.down('[itemId=integrity2]').setValue(true)
                }else{
                    win.down('[itemId=integrity2]').setValue(false)
                }
                if (setting.data.integrity.indexOf(win.down('[itemId=integrity3]').boxLabel)!=-1){
                    win.down('[itemId=integrity3]').setValue(true)
                }else{
                    win.down('[itemId=integrity3]').setValue(false)
                }
                //可用性
                if (setting.data.usability.indexOf(win.down('[itemId=usability1]').boxLabel)!=-1){
                    win.down('[itemId=usability1]').setValue(true)
                }else{
                    win.down('[itemId=usability1]').setValue(false)
                }
                if (setting.data.usability.indexOf(win.down('[itemId=usability2]').boxLabel)!=-1){
                    win.down('[itemId=usability2]').setValue(true)
                }else{
                    win.down('[itemId=usability2]').setValue(false)
                }
                if (setting.data.usability.indexOf(win.down('[itemId=usability3]').boxLabel)!=-1){
                    win.down('[itemId=usability3]').setValue(true)
                }else{
                    win.down('[itemId=usability3]').setValue(false)
                }
                //安全性
                if (setting.data.safety.indexOf(win.down('[itemId=safety1]').boxLabel)!=-1){
                    win.down('[itemId=safety1]').setValue(true)
                }else{
                    win.down('[itemId=safety1]').setValue(false)
                }
                if (setting.data.safety.indexOf(win.down('[itemId=safety2]').boxLabel)!=-1){
                    win.down('[itemId=safety2]').setValue(true)
                }else{
                    win.down('[itemId=safety2]').setValue(false)
                }
                if (setting.data.safety.indexOf(win.down('[itemId=safety3]').boxLabel)!=-1){
                    win.down('[itemId=safety3]').setValue(true)
                }else{
                    win.down('[itemId=safety3]').setValue(false)
                }
            }
        });
        win.show();
    },
    stopHandler: function (btn) {
        XD.confirm('是否要停止该任务', function () {
            var win = btn.up('longRetentionTimeSetView');
            this.submit(win, 0);
        }, this);
    },
    startHandler: function (btn) {
        XD.confirm('是否要启动该任务', function () {
            var win = btn.up('longRetentionTimeSetView');
            this.submit(win, 1);
        }, this);
    },
    saveHandler: function (btn) {
        XD.confirm('是否要保存该任务', function () {
            var win = btn.up('longRetentionTimeSetView');
            this.submit(win, 2);
        }, this);
    },
    /**
     *
     * @param win
     * @param type 0：未启动 1:启动 2：保存
     */
    submit: function (win, type) {
        var form = win.down('[itemId=formitemid]');
        form.submit({
            waitTitle: '提示',// 标题
            waitMsg: '正在启动请稍后...',// 提示信息
            url: '/longRetention/timeJobSubmit',
            params: { // 此处可以添加额外参数
                type: type
            },
            method: 'POST',
            success: function (form, action) {
                XD.msg(action.result.msg);
                win.close();
            },
            failure: function () {
                XD.msg('操作失败');
            }
        });
    },
    settingSaveHandler: function (btn) {
        XD.confirm('是否要保存该设置', function () {
            var win = btn.up('longRetentionSetting');
            var authenticity = [];
            var integrity = [];
            var usability = [];
            var safety = [];
            // 准确性
            if (win.down('[itemId=authenticity1]').checked){
                authenticity.push(win.down('[itemId=authenticity1]').boxLabel);
            }
            if (win.down('[itemId=authenticity2]').checked){
                authenticity.push(win.down('[itemId=authenticity2]').boxLabel);
            }
            if (win.down('[itemId=authenticity3]').checked){
                authenticity.push(win.down('[itemId=authenticity3]').boxLabel);
            }
            if (win.down('[itemId=authenticity4]').checked){
                authenticity.push(win.down('[itemId=authenticity4]').boxLabel);
            }
            //完整性
            if (win.down('[itemId=integrity1]').checked){
                integrity.push(win.down('[itemId=integrity1]').boxLabel);
            }
            if (win.down('[itemId=integrity2]').checked){
                integrity.push(win.down('[itemId=integrity2]').boxLabel);
            }
            if (win.down('[itemId=integrity3]').checked){
                integrity.push(win.down('[itemId=integrity3]').boxLabel);
            }
            //可用性
            if (win.down('[itemId=usability1]').checked){
                usability.push(win.down('[itemId=usability1]').boxLabel);
            }
            if (win.down('[itemId=usability2]').checked){
                usability.push(win.down('[itemId=usability2]').boxLabel);
            }
            if (win.down('[itemId=usability3]').checked){
                usability.push(win.down('[itemId=usability3]').boxLabel);
            }
            //安全性
            if (win.down('[itemId=safety1]').checked){
                safety.push(win.down('[itemId=safety1]').boxLabel);
            }
            if (win.down('[itemId=safety2]').checked){
                safety.push(win.down('[itemId=safety2]').boxLabel);
            }
            if (win.down('[itemId=safety3]').checked){
                safety.push(win.down('[itemId=safety3]').boxLabel);
            }
            Ext.MessageBox.wait('正在保存...', '提示');
            Ext.Ajax.request({
                url: '/longRetention/settingSubmit',
                async:true,
                methods:'Post',
                params:{
                    nodeid:nodeid,
                    authenticity:authenticity.join(","),
                    integrity:integrity.join(","),
                    usability:usability.join(","),
                    safety:safety.join(",")
                },
                success: function (response) {
                    btn.up('longRetentionSetting').close();
                    Ext.MessageBox.hide();
                    XD.msg('保存成功');
                },
                failure: function (response) {
                    Ext.MessageBox.hide();
                    XD.msg('操作失败');
                }
            });
        }, this);
    }
});