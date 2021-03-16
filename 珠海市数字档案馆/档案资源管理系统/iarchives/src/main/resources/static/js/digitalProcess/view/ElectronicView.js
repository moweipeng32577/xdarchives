/**
 * Created by Administrator on 2019/9/10.
 */

Ext.define('DigitalProcess.view.ElectronicView', {
    entryid: '',     //条目主键ID
    entrytype: '',   //数据类型（采集、管理、利用）
    timer:null,     //上移下移定时器
    extend: 'Ext.panel.Panel',
    xtype: 'electronic',
    layout: 'border',
    operateFlag:'',
    bodyBorder: false,
    defaults: {
        split: true
    },
    items: [{
        region: 'west',
        width: '20%',
        xtype: 'treepanel',
        header: false,
        hideHeaders: true,
        rootVisible:true,
        store: {
            extend: 'Ext.data.TreeStore',
            autoLoad:false,
            proxy: {
                type: 'ajax',
                url: '/electronic/szhelectronics/tree/',
                reader: {
                    type: 'json',
                    expanded: false
                }
            },
            root: {
                text: '文件名',
                expanded: true
            }
        },
        autoScroll: true,
        checkPropagation: 'both',
        buttons:{
            xtype:'label',
            itemId:'etips',
            hidden: true,
        },
        listeners: {
            beforeload: function () {
                var view = this.findParentByType('electronic');
                this.getStore().proxy.url = '/electronic/szhelectronics/tree/'+ view.entryid ;
            },
            select: function ( view, record, item, index, e, eOpts )  {
                if (!record.get('leaf')){
                    return;
                }
                // if(e.getTarget('.x-tree-checkbox',1,true)){
                //     return;
                // }
                var view = this.findParentByType('electronic');
                // var mediaFrame = document.getElementById('mediaFrame');
                //当采集、管理模块在未归已归、案卷、卷内点击著录或修改时，会创建多个相同ID的iframe
                //document.getElementById只会拿第一个，导致下面的src对应不了正确显示的那个iframe
                var allMediaFrame = document.querySelectorAll('#mediaFrame');
                // var mediaFrame;
                // //创建electronicview需要指定是著录还是修改类型，经调试，著录的iframe是第一个，修改的是最后一个
                // if (allMediaFrame.length > 0 && view.operateFlag == 'add') {
                //     mediaFrame = allMediaFrame[allMediaFrame.length - 1];
                // } else {
                //     mediaFrame = allMediaFrame[0];
                // }
                var filename = record.get('text');
                p7.changeImg('/digitalProcess/szhShowMedia?eleid=' + record.get('fnid'),filename);
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
                html:'<div id="p7" class="pw-view"></div>'
                // html: '<iframe id="mediaFrame" src=""  width="100%" height="100%" style="border:0px;"></iframe>'
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
        var treeNode = this.down('treepanel');
        Ext.defer(function () {
            treeStore.load({
                callback:function(r,options,success){
                    if(treeStore.getCount()>0){
                        treeNode.getSelectionModel().select(treeStore.getAt(1));
                    }
                    treeNode.getRootNode().set('text','文件名  （总数：'+r.length+'）');
                }
            });
        },300);
    },

    getEleids: function () {
        var ids = [];
        var root = this.down('treepanel').getStore().getRoot();
        if(root != null){
            var records = root.childNodes;
            for (var i = 0; i < records.length; i++) {
                ids.push(records[i].get('fnid'));
            }
        }
        return ids.join(",");
    }
});
