/**
 * Created by Administrator on 2020/6/17.
 */


Ext.define('JyAdmins.view.LookEvidenceTextView', {
    entryid: undefined,     //条目主键ID
    entrytype: '',   //数据类型（采集、管理、利用）

    extend: 'Ext.panel.Panel',
    xtype: 'electronicPro',
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
                text: '附件'
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
                        var view = this.findParentByType('electronicPro');
                        view.findParentByType('window').close();
                    }
                }
            ]
        }],
        listeners: {
            beforeload: function () {
                var view = this.findParentByType('electronicPro');
                this.getStore().proxy.url = '/electronic/electronics/tree/management' + '/' + view.entryid + '/' + 'undefined';
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
                var view = this.findParentByType('electronic');
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
        this.down('treepanel').getStore().reload();
    },

    getEleids: function () {
        var ids = [];
        var records = this.down('treepanel').getStore().getRoot().childNodes;
        for (var i = 0; i < records.length; i++) {
            ids.push(records[i].data.fnid);
        }
        return ids.join(",");
    },

    del: function () {
        var records = this.down('treepanel').getView().getChecked();
        if (records.length == 0) {
            XD.msg('请至少选择一条需要删除的数据');
            return;
        }
        XD.confirm('确定要删除这' + records.length + '条数据吗', function () {
            var eleids = [];
            for (var i = 0; i < records.length; i++) {
                eleids.push(records[i].data.fnid);
            }
            Ext.Ajax.request({
                method: 'DELETE',
                url: '/electronic/ztelectronics/deleteApproveEle/' + eleids.join(","),
                scope: this,
                success: function (response) {
                    XD.msg(Ext.decode(response.responseText).msg);
                    this.down('treepanel').getStore().reload();
                    var mediaFrame = document.getElementById('mediaFrame');
                    mediaFrame.setAttribute('src', '');
                }
            })
        }, this);
    },

    getEvidenceText: function (borrowcode) {
        var evidencetext;
        Ext.Ajax.request({
            url: '/electronApprove/getEvidenceText',
            params:{
                borrowcode:borrowcode
            },
            async:false,
            scope: this,
            success: function (response) {
                evidencetext = Ext.decode(response.responseText).data;
            }
        });
        return evidencetext;
    }
});
