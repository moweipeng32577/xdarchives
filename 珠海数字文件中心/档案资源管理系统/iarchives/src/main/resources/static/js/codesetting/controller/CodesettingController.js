/**
 * Created by tanly on 2017/10/24 0024.
 */
Ext.define('Codesetting.controller.CodesettingController', {
    extend: 'Ext.app.Controller',
    views: [
        'CodesettingView',
        'CodesettingTreeView',
        'CodesettingSxTreeView',
        'CodesettingSelectedFormView',
        'CodesettingPromptView',
        'CodesettingSxPromptView',
        'CodesettingSelectedFormView',
        'CodesettingItemSelectedFormView',
        'CodesettingDetailFormView'
    ],
    stores: ['CodesettingTreeStore','CodesettingSxTreeStore', 'CodesettingSelectStore'],
    models: ['CodesettingTreeModel'],
    init: function () {
        var ifShowRightPanel = false;
        var ifSxShowRightPanel = false;
        this.control({
            'codesettingTreeView': {
                select: function (treemodel, record) {
                    // if (record.get('leaf')) {
                    window.treesettingview = treemodel.view;
                    var codesettingView = treemodel.view.findParentByType('codesettingView');
                    var codesettingPromptView = codesettingView.down('[itemId=codesettingPromptViewID]');
                    if (record.parentNode != null) {//非根目录（功能节点）

                        if (!ifShowRightPanel) {
                            codesettingPromptView.removeAll();
                            var items = {xtype: 'codesettingSelectedFormView'};
                            codesettingPromptView.add(items);
                            ifShowRightPanel = true;
                        }
                        var codesettingSelectedFormView = codesettingPromptView.down('[itemId=codesettingSelectedFormViewID]');
                        codesettingSelectedFormView.setTitle("当前位置：" + record.get('text'));
                        var detailformview = codesettingSelectedFormView.down('[itemId=codesettingDetailFormViewItemID]');
                        detailformview.down('[itemId=areaid]').reset();//清空值
                        detailformview.down('[itemId=splitcodeid]').reset();
                        detailformview.down('[itemId=lengthid]').reset();
                        detailformview.down('[itemId=hiddenfieldId]').reset();
                        var itemselectorView = codesettingSelectedFormView.down('[itemId=itemselectorID]');
                        itemselectorView.store.proxy.extraParams = {datanodeid: record.get('fnid')};
                        itemselectorView.getStore().load(function (storedata) {
                            if(storedata.length===0){
                                XD.msg('请先去模板维护设置模板信息');
                            }
                            var records = [];
                            for (var i = 0; i < storedata.length; i++) {
                                var temp = storedata[i].data.fieldcode.split('∪');
                                if (temp[0] != "") {
                                    records.push(storedata[i]);
                                }
                            }
                            itemselectorView.toField.store.removeAll();
                            itemselectorView.setValue(records);
                            itemselectorView.toField.boundList.select(0);//默认选中第一个
                        });
                    }else{
                        codesettingPromptView.removeAll();
                        codesettingPromptView.add({xtype: 'codesettingPromptView'});
                        ifShowRightPanel = false;
                    }
                }
            },
            'codesettingSxTreeView': {
                select: function (treemodel, record) {
                    // if (record.get('leaf')) {
                    window.sxtreesettingview = treemodel.view;
                    var codesettingView = treemodel.view.findParentByType('codesettingView');
                    var codesettingPromptView = codesettingView.down('[itemId=codesettingSxPromptViewID]');
                    if (record.parentNode != null) {//非根目录（功能节点）

                        if (!ifSxShowRightPanel) {
                            codesettingPromptView.removeAll();
                            var items = {xtype: 'codesettingSelectedFormView'};
                            codesettingPromptView.add(items);
                            ifSxShowRightPanel = true;
                        }
                        var codesettingSelectedFormView = codesettingPromptView.down('[itemId=codesettingSelectedFormViewID]');
                        codesettingSelectedFormView.setTitle("当前位置：" + record.get('text'));
                        var detailformview = codesettingSelectedFormView.down('[itemId=codesettingDetailFormViewItemID]');
                        detailformview.down('[itemId=areaid]').reset();//清空值
                        detailformview.down('[itemId=splitcodeid]').reset();
                        detailformview.down('[itemId=lengthid]').reset();
                        detailformview.down('[itemId=hiddenfieldId]').reset();
                        var itemselectorView = codesettingSelectedFormView.down('[itemId=itemselectorID]');
                        itemselectorView.store.proxy.extraParams = {xtType:window.xtType,datanodeid: record.get('fnid')};
                        itemselectorView.getStore().load(function (storedata) {
                            if(storedata.length===0){
                                XD.msg('请先去模板维护设置模板信息');
                            }
                            var records = [];
                            for (var i = 0; i < storedata.length; i++) {
                                var temp = storedata[i].data.fieldcode.split('∪');
                                if (temp[0] != "") {
                                    records.push(storedata[i]);
                                }
                            }
                            itemselectorView.toField.store.removeAll();
                            itemselectorView.setValue(records);
                            itemselectorView.toField.boundList.select(0);//默认选中第一个
                        });
                    }else{
                        codesettingPromptView.removeAll();
                        codesettingPromptView.add({xtype: 'codesettingSxPromptView'});
                        ifShowRightPanel = false;
                    }
                }
            },
            'codesettingView':{
                tabchange:function(view){
                    if(view.activeTab.title == '档案系统'){
                        window.xtType='档案系统';
                    }else if(view.activeTab.title == '声像系统'){
                        window.xtType='声像系统';
                    }
                }
            },
            'codesettingItemSelectedFormView': {
                render: function (field) {
                    field.getComponent("itemselectorID").toField.boundList.on('select', function () {
                        var codesettingSelectedFormView = this.findParentByType('codesettingSelectedFormView');
                        var codesettingDetailFormView = codesettingSelectedFormView.down('[itemId=codesettingDetailFormViewItemID]');
                        var areatextfield = codesettingDetailFormView.down('[itemId=areaid]');
                        var splitcodetextfield = codesettingDetailFormView.down('[itemId=splitcodeid]');
                        var lengthtextfield = codesettingDetailFormView.down('[itemId=lengthid]');
                        var hideidfield = codesettingDetailFormView.down('[itemId=hiddenfieldId]');

                        var temp = this.selModel.selected.items[0].get('fieldcode').split('∪');
                        if (temp[0] == "") {
                            //将从模板中获得的字段传到输入框中
                            areatextfield.setValue(temp[2]);
                            splitcodetextfield.setValue(temp[3]);
                            lengthtextfield.setValue(temp[4]);
                            //把字段全称保存在隐藏域中，输入框修改保存时用到
                            hideidfield.setValue(temp[1]);
                        } else {
                            areatextfield.setValue(temp[1]);
                            splitcodetextfield.setValue(temp[2]);
                            lengthtextfield.setValue(temp[3]);
                            hideidfield.setValue(temp[4]);
                        }
                    });
                }
            },
            'codesettingDetailFormView': {
                render: function (field) {
                    field.getComponent("splitcodeid").on('keyup', function (ob) {
                        var codesettingSelectedFormView = this.findParentByType('codesettingSelectedFormView');
                        var codesettingItemSelectedFormView = codesettingSelectedFormView.down('[itemId=itemselectorItemID]');
                        var codesettingDetailFormView = codesettingSelectedFormView.down('[itemId=codesettingDetailFormViewItemID]');
                        changeToMultiselect(ob, codesettingItemSelectedFormView, codesettingDetailFormView);
                    });
                    field.getComponent("lengthid").on('keyup', function (ob) {
                        var codesettingSelectedFormView = this.findParentByType('codesettingSelectedFormView');
                        var codesettingItemSelectedFormView = codesettingSelectedFormView.down('[itemId=itemselectorItemID]');
                        var codesettingDetailFormView = codesettingSelectedFormView.down('[itemId=codesettingDetailFormViewItemID]');
                        changeToMultiselect(ob, codesettingItemSelectedFormView, codesettingDetailFormView);
                    });
                }
            },
            'codesettingSelectedFormView button[itemId=codesettingSaveBtnId]': {
                click: function (view) {
                    var nodeid;
                    if(window.xtType=='声像系统'){
                        if (window.sxtreesettingview.selection.isRoot()) {
                            XD.msg('请选择有效的数据节点');
                            return;
                        }
                        nodeid=window.sxtreesettingview.selection.get('fnid');
                    }else{
                        if (window.treesettingview.selection.isRoot()) {
                            XD.msg('请选择有效的数据节点');
                            return;
                        }
                        nodeid=window.treesettingview.selection.get('fnid');
                    }

                    var codesettingSelectedFormView = view.findParentByType('codesettingSelectedFormView');
                    var codesettingItemSelectedFormView = codesettingSelectedFormView.down('[itemId=itemselectorItemID]');
                    var tostore = codesettingItemSelectedFormView.getComponent("itemselectorID").toField.boundList.store;
                    if (tostore.getCount() <= 0) {
                        XD.msg("请至少选择一个字段");
                        return;
                    }
                    var recordslist = [];
                    for (var i = 0; i < tostore.getCount(); i++) {
                        recordslist.push(tostore.getAt(i).get('fieldcode'));
                    }
                    Ext.Ajax.request({
                        params: {
                            datanodeid: nodeid,
                            xtType:window.xtType,
                            fieldcodelist: recordslist
                        },
                        url: '/codesetting/setCode',
                        method: 'post',
                        success: function (resp) {
                            XD.msg(Ext.decode(resp.responseText).msg);
                        },
                        failure: function () {
                            XD.msg('操作失败');
                        }
                    });
                }
            }
        });
    }
});

function changeToMultiselect(variable, SelectedFormView, DetailFormView) {
    if (variable.getName() == 'splitcodetext') {
        if (!validaSplitCode(variable)) {
            return;
        }
    } else {
        if (!validaLength(variable)) {
            return;
        }
    }

    var boundlist = SelectedFormView.getComponent("itemselectorID").toField.boundList;
    var tostore = boundlist.store;
    var records = [];
    var hiddenvalue = DetailFormView.down('[itemId=hiddenfieldId]').getValue();
    if (tostore.getCount() > 0) {
        for (var i = 0; i < tostore.getCount(); i++) {
            var record = tostore.getAt(i);
            var num = tostore.indexOf(record);
            var temp = record.data.fieldcode.split('∪');
            if (hiddenvalue == temp[1] || hiddenvalue == temp[4]) {
                var changeValue = insertChange(variable.getValue(), record.data.fieldcode, variable.getName());
                record.data.fieldcode = changeValue;   //要改变提交到后台的值
                records.push(record);
                tostore.remove(record);
                tostore.insert(num, records);
                records = [];
            }
        }
    }
}

function validaSplitCode(splitcode) {
    var str = splitcode.getValue();
    if (str.length > 1) {
        XD.msg("只能输入一个符号!");
        splitcode.setValue('');
        return false;
    }
    // var reg = /~|!|@|#|%|_|-|=|\*|\.|\+|\?|\||·/;
    var reg = /_|-|=|(|)|\*|\.|\||·/;
    if (str.match(reg) == null) {
        if (str != "") {
            // XD.msg("本系统只支持以下分割符号：~ ! @ # % _ - = * . + ? | ·");
            XD.msg("支持分割符号为：&nbsp;&nbsp;_&nbsp;&nbsp;-&nbsp;&nbsp;=&nbsp;&nbsp;*&nbsp;&nbsp;.&nbsp;&nbsp;|&nbsp;&nbsp;·");
            splitcode.setValue('');
        } else {
            XD.msg("[分割符号]不能为空!");
        }
        return false;
    } else {
        return true;
    }
}

function validaLength(length) {
    var str = length.getValue();
    var reg = new RegExp("^([0-9])$");
    if (!reg.test(str)) {
        XD.msg("请输入0到9的一位数字");
        length.setValue('');
        return false;
    } else {
        return true;
    }
}

function insertChange(str, changeValue, isSign) {
    var temp = changeValue.split("∪");
    var haveChange = temp[0] + "∪";
    if (temp[0] == "") {
        if (isSign == 'splitcodetext')//分割符号的改变
            temp[3] = str;
        else//单位长度的改变
            temp[4] = str;
        for (var i = 1; i < temp.length; i++) {
            if (i != temp.length - 1)
                haveChange = haveChange + temp[i] + "∪";
            else
                haveChange = haveChange + temp[i];
        }
    } else {
        //从数据库中获得的字段
        if (isSign == 'splitcodetext')//分割符号的改变
            temp[2] = str;
        else//单位长度的改变
            temp[3] = str;
        for (var i = 1; i < temp.length; i++) {
            if (i != temp.length - 1)
                haveChange = haveChange + temp[i] + "∪";
            else
                haveChange = haveChange + temp[i];
        }
    }
    return haveChange
}