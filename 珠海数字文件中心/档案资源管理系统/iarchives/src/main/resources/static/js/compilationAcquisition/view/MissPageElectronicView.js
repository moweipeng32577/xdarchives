/**
 * Created by Administrator on 2019/3/15.
 */


Ext.define('CompilationAcquisition.view.MissPageElectronicView', {
    entryid: undefined,     //条目主键ID
    entrytype: '',   //数据类型（采集、管理、利用）

    extend: 'Ext.panel.Panel',
    xtype: 'missPageElectronicView',
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
            proxy: {
                type: 'ajax',
                url: '/electronic/electronics/tree/',
                reader: {
                    type: 'json',
                    expanded: false
                }
            },
            root: {
                text: '电子文件'
            }
        },
        autoScroll: true,
        rootVisible: true,
        checkPropagation: 'both',
        dockedItems: [{
            xtype: 'toolbar',
            dock: 'top',
            items: [
                {
                    xtype: 'button', text: '返回', handler: function () {
                    var view = this.findParentByType('missPageElectronicView');
                    view.findParentByType('window').close();
                }
                }
            ]
        }],
        listeners: {
            beforeload: function () {
                var view = this.findParentByType('missPageElectronicView');
                this.getStore().proxy.url = '/electronic/electronics/tree/management' + '/' + view.entryid + '/' + window.wmedia;
            },
            afterrender: function () {
                this.expandAll();
            },
            itemclick: function (view, record, item, index, e, eOpts) {
                if (!record.get('leaf'))
                    return;
                if(e.getTarget('.x-tree-checkbox',1,true)){
                    return;
                }
                var mediaFrame = document.getElementById('mediaFrame');
                var filename = record.get('text');
                mediaFrame.setAttribute('src', '/electronic/media?entrytype=management&eleid=' + record.get('fnid') + '&filetype=' + filename.substring(filename.lastIndexOf('.') + 1));
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
                html: '<iframe id="mediaFrame" src=""  width="100%" height="100%" style="border:0px;"></iframe>'
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
            ids.push(records[i].data.fnid);
        }
        return ids.join(",");
    }
});
