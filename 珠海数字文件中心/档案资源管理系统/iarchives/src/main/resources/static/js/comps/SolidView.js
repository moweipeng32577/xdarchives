/**
 * Created by tanly on 2018/03/24.
 */
Ext.define('Comps.view.SolidView', {
    entryid: '',     //条目主键ID
    entrytype: '',   //数据类型（采集、管理、利用）
    timer:null,     //上移下移定时器
    extend: 'Ext.panel.Panel',
    xtype: 'solid',
    layout: 'border',
    bodyBorder: false,
    defaults: {
        split: true
    },
    items: [{
        region: 'west',
        width: 400,
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
                text: '利用文件',
                checked: false
            }
        },
        autoScroll: true,
        rootVisible: true,
        checkPropagation: 'both',
        dockedItems: [{
            xtype: 'toolbar',
            overflowHandler: 'scroller',
            dock: 'top',
            items: [{
                xtype: 'button', text: '下载', handler: function () {
                    var view = this.findParentByType('solid');
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
            },{
                xtype: 'button', text: '全部下载', handler: function () {
                    var view = this.findParentByType('solid');
                    var records = view.down('treepanel').getStore().getRoot().childNodes;
                    if (records.length == 0) {
                        XD.msg('没有可下载文件');
                        return;
                    }
                    view.download(records,'allDownload');
                }
            },{
                xtype: 'button', text: '上移', handler: function () {
                    var view = this.findParentByType('solid');
                    var treeview = view.down('treepanel');
                    var records = treeview.getView().getChecked();
                    if(records.length != 1){
                        XD.msg('只能选择一条数据');
                        return;
                    }

                    var record = records[0];
                    var parentNode = record.parentNode;
                    var length = parentNode.childNodes.length;
                    var currentIndex = 0;

                    //获取当前节点
                    for(var i =0; i < length; i++){
                        if(parentNode.childNodes[i] === record){
                            currentIndex = i;
                        }
                    }

                    //操作
                    if(currentIndex <=0){
                        currentIndex = 1;
                    }else{
                        currentIndex = currentIndex - 1;
                    }

                    parentNode.insertChild(currentIndex,record);

                    if(!view.timer){
                        clearTimeout(view.timer);//清除计时器
                    }

                    view.timer = setTimeout(function () {
                        view.changeSort(parentNode.childNodes);
                    },500);
                }
            },{
                xtype: 'button', text: '下移', handler: function () {
                    var view = this.findParentByType('solid');
                    var treeview = view.down('treepanel');
                    var records = treeview.getView().getChecked();
                    if(records.length != 1){
                        XD.msg('只能选择一条数据');
                        return;
                    }

                    var record = records[0];
                    //当前节点父节点
                    var parentNode = record.parentNode;
                    var length = parentNode.childNodes.length;
                    var currentIndex= 0;

                    // 获取当前节点的索引
                    for(var i = 0; i < length; i++){
                        if(parentNode.childNodes[i] === record){
                            currentIndex = i;
                        }
                    }

                    // 计算移除当前节点后，插入的目标节点索引
                    var targetIndex = (currentIndex>=(length-2))? (length-1) : currentIndex+1;

                    // 删除当前节点
                    parentNode.childNodes[currentIndex].remove();

                    // 插入当前节点，至计算的目标位置
                    parentNode.insertChild(targetIndex,record);

                    if(!view.timer){
                        clearTimeout(view.timer);//清除计时器
                    }

                    view.timer = setTimeout(function () {
                        view.changeSort(parentNode.childNodes);
                    },500);
                }
            }, {
                xtype: 'button', text: '打印', itemId: 'print',
                handler: function () {
                    var view = this.findParentByType('solid');
                    var treeview = view.down('treepanel');
                    var records = treeview.getView().getChecked();
                    var texts = [];
                    var eleid = [];
                    var fType = 0;
                    for (var i = 0; i < records.length; i++) {
                        var t = records[i].get('text');
                        if (records[i].get('fnid')) {//过滤文件夹
                            texts.push(t);
                            eleid.push(records[i].get('fnid'));
                            if (t.toLowerCase().lastIndexOf(".png") > 0 || t.toLowerCase().lastIndexOf(".jpg") > 0 || t.toLowerCase().lastIndexOf(".jpeg") > 0 || t.toLowerCase().lastIndexOf(".bmp") > 0) {
                                fType = 1;
                            }
                            else if (t.toLowerCase().lastIndexOf(".pdf") > 0) {
                                fType = 0;
                            }
                            else {
                                XD.msg('暂时只支持常用图片和PDF格式的电子文件打印！');
                                return;
                            }
                        }
                    }
                    if (texts.length != 1) {
                        XD.msg('请选择1条数据进行操作！');
                        return;
                    }
                    var filename = texts[0];
                    var url = '/electronic/media?entrytype=' + view.entrytype + '&eleid=' + eleid[0] + '&filename=' + filename + '&filetype=' + filename.substring(filename.lastIndexOf('.') + 1)+"&xtType="+view.xtType;
                    var newurl = encodeURI(url);

                    if (document.getElementById("print-iframe")) {
                        document.getElementById("print-iframe").parentNode.removeChild(document.getElementById("print-iframe"));
                    }

                    //iframe代表主页面的dom元素，iframe.contentWindow代表iframe这个页面的window对象
                    var iframe = document.createElement('IFRAME');
                    iframe.setAttribute("id", "print-iframe");
                    iframe.setAttribute('style', 'position:absolute;width:"100%";height:100%;left:-500px;top:-500px;');
                    iframe.setAttribute('src', newurl);
                    document.body.appendChild(iframe);
                    iframe.contentWindow.focus();
                    iframe.onload = function () {
                        var _this = this;
                        if (fType == 1) {
                            setTimeout(function () {
                                var iframe = _this;
                                var img = iframe.contentDocument.getElementById('photo_img');
                                var width = img.scrollWidth;
                                var height = img.scrollHeight;
                                img.setAttribute('style', 'width:100%;height:100%;display:block;')//铺满整个页面
                                iframe.contentDocument.body.innerHTML = img.outerHTML;
                                //横向打印
                                if (parseInt(width) > parseInt(height)) {
                                    var style = document.createElement('style');
                                    style.setAttribute("type", "text/css");
                                    style.setAttribute("media", "print");
                                    style.innerHTML = '@page { size: landscape; }';
                                    iframe.contentDocument.head.appendChild(style);
                                }
                                iframe.contentWindow.print();
                            }, 200);
                        } else {
                            var iframe = document.getElementById("print-iframe");
                            iframe.setAttribute('src', '/electronic/printYWMedia?entrytype=' + view.entrytype + '&eleid=' + eleid[0]+"&mType="+view.winType);
                            iframe.onload = function () {
                                iframe.contentWindow.print();
                            };
                        }
                    }
                }
                },{
                    xtype: 'button', text: '批量打印',itemId:'batchprint',
                    handler: function () {
                        var view = this.findParentByType('solid');
                        var treeview = view.down('treepanel');
                        var records = treeview.getView().getChecked();
                        var eleid = [];
                        for (var i = 0; i < records.length; i++) {
                            var t = records[i].get('text');
                            if (records[i].get('fnid')) {//过滤文件夹
                                eleid.push(records[i].get('fnid'));
                                if (t.toLowerCase().lastIndexOf(".png") > 0 || t.toLowerCase().lastIndexOf(".jpg") > 0 || t.toLowerCase().lastIndexOf(".jpeg") > 0 || t.toLowerCase().lastIndexOf(".pdf") > 0) {
                                    continue;
                                }
                                else {
                                    XD.msg('暂时只支持常用图片和pdf文件批量打印！');
                                    return;
                                }
                            }
                        }

                        if (eleid.length <1) {
                            XD.msg('请选择数据！');
                            return;
                        }
                        if (document.getElementById("print-iframe")) {
                            document.getElementById("print-iframe").parentNode.removeChild(document.getElementById("print-iframe"));
                        }

                        var url = '/electronic/batchprint?datatype=' + view.entrytype + '&eleid=' + eleid +"&mType="+view.winType+"&xtType="+view.xtType;
                        var newurl = encodeURI(url);
                        var iframe = document.createElement('IFRAME');
                        iframe.setAttribute("id", "print-iframe");
                        iframe.setAttribute('style', 'position:absolute;width:"100%";height:100%;left:-500px;top:-500px;');
                        iframe.setAttribute('src', newurl);
                        document.body.appendChild(iframe);
                        Ext.MessageBox.wait('正在处理中...请稍后....','提示');
                        iframe.contentWindow.focus();
                        iframe.onload = function () {
                            iframe.contentWindow.print();
                            Ext.MessageBox.hide();
                        };
                    }
            }
            ]
        }],
        listeners: {
            beforeload: function () {
                var view = this.findParentByType('solid');
                if(view.unionAll){
                    this.getStore().proxy.url = '/electronic/solidUnion/'+view.entryid+'/'+window.remainEleids;
                }else{
                    if(view.xtType=="声像系统"){
                        this.getStore().proxy.url = '/electronic/electronics/tree/' + view.entrytype + '/' + view.entryid + '/'+window.remainEleids+'/'+"声像系统";
                    }else {
                        this.getStore().proxy.url = '/electronic/electronics/tree/' + view.entrytype + '/' + view.entryid + '/' + window.remainEleids;
                    }
                }
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
                var solidFrame = document.getElementById('solidFrame');
                var view = this.findParentByType('solid');
                var filename = record.get('text');
                if(view.isJy){
                    var xtType="";
                    if(view.xtType=="声像系统"){
                        xtType=view.xtType;
                    }else {
                        xtType="";
                    }
                    solidFrame.setAttribute('src', '/electronic/jyMedia?entrytype=' + view.entrytype + '&eleid=' + record.get('fnid') + '&filetype=' + filename.substring(filename.lastIndexOf('.') + 1)+"&xtType="+xtType);
                }else{
                    solidFrame.setAttribute('src', '/electronic/media?entrytype=' + view.entrytype + '&eleid=' + record.get('fnid') + '&filetype=' + filename.substring(filename.lastIndexOf('.') + 1));
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
                html: '<iframe id="solidFrame" src=""  width="100%" height="100%" style="border:0px;"></iframe>'
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
                    if(r!=null)
                        treeNode.getRootNode().set('text','利用文件  （总数：'+r.length+'）');
                }
            });
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
            params: {
                xtType:this.xtType
            },
            success: function (response) {
                var responseText = Ext.decode(response.responseText);
                if(responseText.success==true){
                    if(ids.length == 1||this.xtType=="声像系统"){
                        location.href = '/electronic/electronics/download/' + this.entrytype+ '/' + ids[0]+"?mType="+this.winType+"&xtType="+this.xtType;
                    }else{
                        location.href = '/electronic/electronics/downloads/' + this.entrytype + '/'+ idsStr+"?mType="+this.winType+"&xtType="+this.xtType;
                    }
                }else{
                    XD.msg('下载失败！'+responseText.msg);
                }
            }
        });
    },

    changeSort : function(nodes){
        var eleids = [];
        for(var i in nodes){
            eleids[i] = nodes[i].data.fnid;
        }

        Ext.Ajax.request({
            params: {
                eleids:eleids,
                entryType:'solid'
            },
            url: '/electronic/mediaFileSort',
            method: 'POST',
            scope: this,
            success: function (response) {
            },
            failure:function(response){
                XD.msg('操作失败');
            }
        });
    }
});