/**
 * Created by Administrator on 2019/10/26.
 */



Ext.define('TransforAuditDeal.view.ElectronicVersionView', {
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
                var mediaFrame = document.getElementById('mediaFrameVersion');
                mediaFrame.setAttribute('src', '/electronic/verMedia?eleid=' + view.entryid + '&filetype=' + view.fileName.substring(view.fileName.lastIndexOf('.') + 1)+'&entrytype=capture');
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
                mediaFrame.setAttribute('src', '/electronic/verMedia?eleid=' + record.get('fnid') + '&filetype=' + filename.substring(filename.lastIndexOf('.') + 1)+'&entrytype=capture');
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

    initData: function (entryid,fileName) {
        this.entryid = entryid;
        this.fileName = fileName;
        this.down('treepanel').getStore().reload();
    },

    getEleids: function () {
        var ids = [];
        //通过实体id去后台找到其所有对应的电子文件，防止树节点采用懒加载时无法获取全部电子文件id
        Ext.Ajax.request({
            method: 'GET',
            url: '/electronic/getEles',
            params:{entrytype:this.entrytype,entryid:this.entryid},
            scope: this,
            async:false,
            success: function (response) {
                var data = Ext.decode(response.responseText).data;
                Ext.each(data,function (item) {
                    ids.push(item.eleid);
                })
            }
        });
        return ids.join();
    }
});

