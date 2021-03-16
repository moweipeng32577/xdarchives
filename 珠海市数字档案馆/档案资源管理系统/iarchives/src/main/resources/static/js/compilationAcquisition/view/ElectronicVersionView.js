/**
 * Created by Administrator on 2019/2/22.
 */


Ext.define('CompilationAcquisition.view.ElectronicVersionView', {
    entryid: undefined,     //条目主键ID
    entrytype: '',   //数据类型（采集、管理、利用）

    extend: 'Ext.panel.Panel',
    xtype: 'electronicVersion',
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
                extraParams: {
                    eleversionid:"",
                    eletype:""
                },
                url: '/electronic/electronics/management/Version/tree',
                reader: {
                    type: 'json',
                    expanded: false
                }
            },
            root: {
                text: '版本文件'
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
                    var view = this.findParentByType('electronicVersion');
                    view.findParentByType('window').close();
                }
                }
            ]
        }],
        listeners: {
            beforeload: function () {
                var view = this.findParentByType('electronicVersion');
                this.getStore().proxy.url = '/electronic/electronics/management/Version/tree';
                this.getStore().proxy.extraParams.eleversionid = window.eleVersionid;
                this.getStore().proxy.extraParams.eletype = window.eletype;
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
                var mediaFrame = document.getElementById('mediaFrameVersion');
                var filename = record.get('text');
                mediaFrame.setAttribute('src', '/electronic/verMedia?eleid=' + record.get('fnid') + '&filetype=' + filename.substring(filename.lastIndexOf('.') + 1)+'&entrytype=management');
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
                html: '<iframe id="mediaFrameVersion" src=""  width="100%" height="100%" style="border:0px;"></iframe>'
            }
        ]
    }],

    initData: function (entryid) {
        this.entryid = entryid;
        this.down('treepanel').getStore().reload();
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
