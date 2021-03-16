/**
 * Created by yl on 2017/10/27.
 */
Ext.define('ThematicUtilize.controller.ThematicUtilizeController', {
    extend: 'Ext.app.Controller',

    // 其实翻译出来就是“从根 app 开始找 view（注意没带 s 哦） 目录，在这个目录下找到 student 目录，然后加载 List.js 这个文件”
    views: ['ThematicUtilizeView','ThematicUtilizeGridView','ThematicUtilizeViews','ThematicUtilizeTreeView','ElectronicView'],//加载view
    stores: ['ThematicUtilizeGridStore','ThematicUtilizethematictypeStore'],//加载store
    models: ['ThematicUtilizeGridModel','ThematicUtilizeTreeModel'],//加载model
    init: function () {
        this.control({
            'ThematicUtilizeTreeView':{
                select:function (treemodel,record) {
                    var thematicUtilizeView = treemodel.view.findParentByType('thematicUtilizeViews').down('thematicUtilizeView');
                    thematicUtilizeView.initGrid({ thematictypes: record.get('text') });
                }
            },
            'thematicUtilizeView [itemId=download]': {
                click: this.downloadHandler
            },
            'thematicUtilizeView [itemId=look]': {
                click: this.lookHandler
            },
            'thematicUtilizeView [itemId=lookEle]': {
                click: this.lookEleHandler
            },
            'thematicUtilizeView [itemId=dataview]': {
                itemdblclick: this.itemdblclickHandler,
                itemmouseenter :this.itemmouseenterHandler
            },
            'thematicUtilizeWindow button[itemId=thematicProBackBtnID]': {
                click: function (view, e, eOpts) {
                    var window = view.up('thematicUtilizeWindow');
                    window.close();
                }
            }
        });
    },
    lookHandler:function(btn){
        var thematicUtilizeView = btn.findParentByType('thematicUtilizeView');
        var select = thematicUtilizeView.acrossSelections;
        if (select.length != 1){
            XD.msg('请选择一条操作记录！');
        }else {
            var window = Ext.create('ThematicUtilize.view.ThematicUtilizeWindow');
            window.title = '查看';
            var record = select[0];
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
    },
    lookEleHandler:function(btn){
        var thematicUtilizeView = btn.findParentByType('thematicUtilizeView');
        var select = thematicUtilizeView.acrossSelections;
        if (select.length != 1){
            XD.msg('请选择一条操作记录！');
        }else {
            window.leadIn = Ext.create("Ext.window.Window", {
                width: '100%',
                height: '100%',
                title: '查看电子文件',
                modal: true,
                header: false,
                draggable: false,//禁止拖动
                resizable: false,//禁止缩放
                closeToolText: '关闭',
                layout: 'fit',
                items: [{xtype: 'electronicPro'}],
            });
            window.leadIn.down('electronicPro').entrytype='thematicUtilize';
            window.leadIn.down('electronicPro').initData(select[0].get("thematicid"));
            window.leadIn.show();
        }
    },
    findDataView :function (btn) {
        return btn.up('thematicUtilizeView');
    },
    downloadHandler : function (btn) {
        var thematicUtilizeView = this.findDataView(btn);
        if(thematicUtilizeView.acrossSelections.length==0){
            XD.msg('请选择记录');
            return;
        }
        for(var i=0;i<thematicUtilizeView.acrossSelections.length;i++){
            setTimeout(function(thematicid,name){
                //判断本地文件是否存在，存在则下载，不存在则提醒
                Ext.Ajax.request({
                    params: {thematicid: thematicid},
                    url: '/thematicUtilize/getThematicFile',
                    method: 'POST',
                    sync: true,
                    success: function (resp) {
                        var respText = Ext.decode(resp.responseText);
                        if (respText.success == true) {
                            var downloadForm = document.createElement("form");
                            document.body.appendChild(downloadForm);
                            downloadForm.action = '/thematicUtilize/downloadZt/'+thematicid;
                            downloadForm.submit();
                        }else{
                            XD.msg("专题："+name+"，文件不存在，无法下载");
                        }
                    },
                    failure: function() {
                        XD.msg('操作失败');
                    }
                });
            }, i*300, thematicUtilizeView.acrossSelections[i].data.id,thematicUtilizeView.acrossSelections[i].data.name);
        }
    },
    itemdblclickHandler: function (view, item) {
        var thematicUtilizeView = this.findDataView(view);
        var name=thematicUtilizeView.acrossSelections[0].data.name;
        Ext.Ajax.request({
            params: {thematicid: item.data.thematicid},
            url: '/thematicUtilize/getThematicFile',
            method: 'POST',
            sync: true,
            success: function (resp) {
                var respText = Ext.decode(resp.responseText);
                if (respText.success == true) {
                    var downloadForm = document.createElement("form");
                    document.body.appendChild(downloadForm);
                    downloadForm.action = '/thematicUtilize/downloadZt/'+item.data.thematicid;
                    downloadForm.submit();
                }else{
                    XD.msg("专题："+name+"，文件不存在，无法下载");
                }
            },
            failure: function() {
                XD.msg('操作失败');
            }
        });
    },
    itemmouseenterHandler: function (view, index) {
        if (view.tip == null) {
            view.tip = Ext.create('Ext.tip.ToolTip', {
                target: view.el,
                delegate: view.itemSelector,
                renderTo: Ext.getBody()
            });
        }
        view.el.clean();
        view.tip.update(index.data.name);
    }
});