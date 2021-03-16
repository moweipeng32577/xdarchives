/**
 * Created by tanly on 2018/03/24.
 */
Ext.define('Comps.view.LongView', {
    entryid: '',     //条目主键ID
    entrytype: '',   //数据类型（采集、管理、利用）

    extend: 'Ext.panel.Panel',
    xtype: 'long',
    layout: 'border',
    bodyBorder: false,
    defaults: {
        split: true
    },
    items: [{
        region: 'west',
        width: 350,
        xtype: 'treepanel',
        header: false,
        hideHeaders: true,
        store: {
            extend: 'Ext.data.TreeStore',
            autoLoad:false,
            proxy: {
                type: 'ajax',
                url: '/electronic/electronics/tree/',
                reader: {
                    type: 'json',
                    expanded: false
                }
            },
            root: {
                text: '长期存储d文件',
                checked: false
            }
        },
        autoScroll: true,
        rootVisible: true,
        checkPropagation: 'both',
        dockedItems: [{
            xtype: 'toolbar',
            dock: 'top',
            items: [{
                xtype: 'button', text: '下载', handler: function () {
                    var view = this.findParentByType('long');
                    var treeview = view.down('treepanel');
                    var records = treeview.getView().getChecked();
                    if(records.length == 0){
                        XD.msg('未勾选下载文件');
                        return;
                    }
                    if(treeview.getView().getStore().data.length==records.length){
                        records.splice(0,1);
                    }
                    view.download(records);
                }
            }]
        }],
        listeners: {
            beforeload: function () {
                var view = this.findParentByType('long');
                this.getStore().proxy.url = '/electronic/electronics/tree/' + view.entrytype + '/' + view.entryid + '/'+window.remainEleids;
            },
            afterrender: function () {
                this.expandAll();
            },
            itemclick: function ( view, record, item, index, e, eOpts )  {
                if (!record.get('leaf')){
                    return;
                }
                if(e.getTarget('.x-tree-checkbox',1,true)){
                    return;
                }
                var longFrame = document.getElementById('longFrame');
                var view = this.findParentByType('long');
                var filename = record.get('text');
                longFrame.setAttribute('src', '/electronic/media?entrytype=' + view.entrytype + '&eleid=' + record.get('fnid') + '&filetype=' + filename.substring(filename.lastIndexOf('.') + 1));
            }
        }
    }, {
        region: 'center',
        layout: 'border',
        items: [
            {
                region: 'center',
                width: '100%',
                height: '100%',
                html: '<iframe id="longFrame" src=""  width="100%" height="100%" style="border:0px;"></iframe>'
            }
        ]
    }],

    initData: function (entryid) {
        this.entryid = entryid;
        window.remainEleids = 'undefined';
        var treeStore = this.down('treepanel').getStore();
        if(typeof(entryid) == 'undefined'){
            this.down('treepanel').getRootNode().removeAll();
            return;
        }
        Ext.defer(function () {
            treeStore.reload();
        },300);
    },

    getEleids: function () {
        var ids = [];
        var records = this.down('treepanel').getStore().getRoot().childNodes;
        for (var i = 0; i < records.length; i++) {
            ids.push(records[i].get('fnid'));
        }
        return ids.join(",");
    },

    download: function (records) {
        var ids = [];
        for (var i = 0; i < records.length; i++) {
            if(records[i].get('fnid')!==''){
                ids.push(records[i].get('fnid'));
            }
        }
        var idsStr = ids.join(',');
        Ext.Ajax.request({
            method: 'GET',
            url: '/electronic/ifFileExist/' + this.entrytype+ '/' + idsStr,
            scope: this,
            success: function (response) {
                var responseText = Ext.decode(response.responseText);
                if(responseText.success==true){
                    if(ids.length == 1){
                        location.href = '/electronic/electronics/download/' + this.entrytype+ '/' + ids[0];
                    }else{
                        location.href = '/electronic/electronics/downloads/' + this.entrytype + '/'+ idsStr;
                    }
                }else{
                    XD.msg('下载失败！'+responseText.msg);
                }
            }
        });
    }
});