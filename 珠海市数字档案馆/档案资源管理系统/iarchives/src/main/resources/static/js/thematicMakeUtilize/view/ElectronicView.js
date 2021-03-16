/**
 * Created by Rong on 2017/11/15.
 */
Ext.define('ThematicUtilize.view.ElectronicView', {
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
                text: '专题文件'
            },
        },
        autoScroll: true,
        rootVisible: true,
        checkPropagation: 'both',
        dockedItems: [{
            xtype: 'toolbar',
            dock: 'top',
            items: [
                {
                    xtype: 'button', text: '下载', handler: function () {
                    var view = this.findParentByType('electronicPro');
                    var records = view.down('treepanel').getView().getChecked();
                    if (records.length == 0) {
                        XD.msg('未勾选下载文件');
                        return;
                    }
                    view.download(records);
                }
                },
                {
                    xtype: 'button', text: '全部下载', handler: function () {
                    var view = this.findParentByType('electronicPro');
                    var records = view.down('treepanel').getStore().getRoot().childNodes;
                    if (records.length == 0) {
                        XD.msg('没有可下载文件');
                        return;
                    }
                    view.download(records);
                }
                },
                {
                    xtype: 'button', text: '返回', handler: function () {
                    var view = this.findParentByType('electronicPro');
                    window.wmedia = view.getEleids();
                    if(window.wform) {
                        window.wform.query('[itemId=mediacount]')[0].setText('共' + window.wmediaName.length + '份');
                        window.wform.query('[itemId=media]')[0].setValue(window.wmediaName.join());
                    }
                    view.findParentByType('window').close();
                }
                }
            ]
        }],
        listeners: {
            beforeload: function () {
                var view = this.findParentByType('electronicPro');
                this.getStore().proxy.url = '/electronic/electronics/tree/thematicUtilize/' + view.entryid + '/undefined';
            },
            afterrender: function () {
                this.expandAll();
            },
            itemclick: function (view, record, item, index, e, eOpts) {
                if (!record.get('leaf'))
                    return;
                var mediaFrame = document.getElementById('mediaFrame');
                var filename = record.get('text');
                mediaFrame.setAttribute('src', '/electronic/media?entrytype=management&eleid=' + record.get('fnid') + '&filetype=' + filename.substring(filename.lastIndexOf('.') + 1));
            },
            load:function(node) {
                var view = this.findParentByType('electronicPro');
                var nodeItems = node.data.items;
                var record;
                for (var i = 0; i < nodeItems.length; i++) {//遍历去寻找root下的第一个文件
                    if (nodeItems[i].get('cls') == 'file') {
                        record = nodeItems[i];
                        break;
                    }
                }
                if (record){//表示root下有文件，选中第一个文件并且预览文件。
                    this.getSelectionModel().select(record);//默认选中root下面的第一个节点
                    Ext.defer(function () {//需要等mediaFrame页面加完后再执行以下方法。
                        view.openFile(record);
                    },800);
                }else{//表示root下没文件，如果root下有分类默认选中第一个分类，如果没有就不选
                    this.getSelectionModel().select(nodeItems[1]);
                }
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
    openFile:function(record){
        var mediaFrame = getmediaFrame(this);
        if (!mediaFrame){//表示mediaFrame还未加载出来
            return;
        }
        var filename=record.get('text');
        var start=filename.indexOf("n>")+2;
        filename=filename.substring(start,filename.length);
        filename=encodeURIComponent(filename);
        mediaFrame.setAttribute('src', '/electronic/media?entrytype=management&eleid=' + record.get('fnid') + '&filetype=' + filename.substring(filename.lastIndexOf('.') + 1));
    },
    initData: function (entryid) {
        this.entryid = entryid;
        var treeNode = this.down('treepanel');
        var rootNode = treeNode.getRootNode();
        //在加载树列表数据前先清空原有的树列表数据。
        while (rootNode.hasChildNodes()) {
            rootNode.removeChild(rootNode.firstChild);
        }
        var treeStore = this.down('treepanel').getStore();
        var eles = this.getEleids();
        if (eles == "" || eles == "undefined") {//修改时没有电子文件存在
            eles = [];
        }else{//修改时有电子文件存在
            eles = eles.split(',');
        }
        treeNode.getRootNode().set({'text':'原文列表  （总数：'+eles.length+'）',checked:false});
        treeStore.proxy.extraParams.fileClassId = null;
        treeStore.load({
            callback:function(r,options,success){
                var mediaFrame = getmediaFrame(treeNode.findParentByType('electronicPro'));
                if(mediaFrame){
                    mediaFrame.setAttribute('src',"");  //刷新显示的图片
                }
            }
        });
    },

    getEleids: function () {
        var ids = [];
        window.wmediaName=[];
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
                    window.wmediaName.push(item.filename);
                    ids.push(item.eleid);
                })
            }
        });
        return ids.join();
    },

    download: function (records) {
        Ext.MessageBox.wait('正在下载请稍后...', '提示');
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
                if (responseText.success == true) {
                    if(ids.length == 1){
                        location.href = '/electronic/electronics/download/' + this.entrytype+ '/' + ids[0];
                    }else{
                        location.href = '/electronic/electronics/downloads/' + this.entrytype + '/'+ idsStr;
                    }
                } else {
                    Ext.MessageBox.hide();
                    XD.msg('下载失败！' + responseText.msg);
                    return;
                }
                Ext.MessageBox.hide();
            }
        });
    },

    del: function () {
        var records = this.down('treepanel').getView().getChecked();
        if (records.length == 0) {
            XD.msg('请至少选择一条需要删除的数据');
            return;
        }
        var tipStr="";
        for (var i = 0; i < records.length; i++) {
            if(records[i].data.cls == 'folder'){
                tipStr="删除文件夹，文件夹内的文件也会被删除，";
                break;
            }
        }
        XD.confirm(tipStr+"确定要删除选定的数据吗", function () {
            var eleids = [];
            for (var i = 0; i < records.length; i++) {
                eleids.push(records[i].data.fnid);
            }
            Ext.Ajax.request({
                method: 'DELETE',
                url: '/electronic/ztelectronics/management/' + this.entryid + '/' + eleids.join(","),
                scope: this,
                success: function (response) {
                    XD.msg(Ext.decode(response.responseText).msg);
                    window.wmedia = this.getEleids();
                    this.initData(this.entryid);
                    var mediaFrame = document.getElementById('mediaFrame');
                    mediaFrame.setAttribute('src', '');
                }
            })
        }, this);
    }
});
function getmediaFrame(view) {
    var allMediaFrame = document.querySelectorAll('#mediaFrame');
    var mediaFrame;
    //创建electronicview需要指定operateFlag类型，经调试，著录（add）的iframe是二个，修改(modify),查看(look)的是第一个
    if(allMediaFrame.length == 1){
        mediaFrame =allMediaFrame[0];
    }
    else if (allMediaFrame.length > 1 && view.operateFlag == 'add') { //创建了多个iframe时候， 著录的iframe是第二个
        mediaFrame = allMediaFrame[1];
    }
    else if(allMediaFrame.length >1 && (view.operateFlag == 'modify' || view.operateFlag == 'look')){  //创建了多个iframe时候， 修改和查看的iframe是第一个
        mediaFrame = allMediaFrame[0];
    }
    else {
        mediaFrame = allMediaFrame[allMediaFrame.length - 1];
    }
    return mediaFrame;
}