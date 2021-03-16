/**
 * Created by tanly
 */
var selectLabelItems = '';
Ext.define('MetadataSearch.view.TreeLabelView', {
    extend: 'Ext.panel.Panel',
    xtype: 'treeLabelView',
    itemId: 'treeLabelView',
    layout: 'border',
    items: [{
        region: 'center',
        header: false,
        xtype: 'treepanel',
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
                text: '原文列表',
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
                xtype: 'button', text: '上传', handler: function () {
                    var view = this.findParentByType('electronicPro');
                    var win = Ext.create('Comps.view.UploadView', {
                        entrytype: view.entrytype,
                        entryid: view.entryid,
                        treepanel: view.down('treepanel'),
                        eleview:view
                    });
                    window.UploadView = win;
                    win.on('close', function () {
                        if (typeof(this.entryid) == 'undefined') {
                            // var treeNodesNum = this.down('treepanel').getStore().getRoot().childNodes.length;//树节点的文件数量
                            // var uploadNum = 0;
                            var store = win.down('grid').getStore();
                            for (var i = 0; i < store.getCount(); i++) {
                                var data = store.getAt(i).data;
                                if (data.progress == 1) {
                                    this.down('treepanel').getRootNode().appendChild({
                                        fnid: data.eleid,
                                        text: data.name,
                                        checked: false,
                                        leaf: true
                                    });
                                }
                            }
                        } else {
                            this.down('treepanel').getStore().reload();
                        }
                    }, view);
                    win.show();
                }
            }, {
                xtype: 'button', text: '下载', handler: function () {
                    var view = this.findParentByType('electronicPro');
                    var treeview = view.down('treepanel');
                    var records = treeview.getView().getChecked();
                    if (records.length == 0) {
                        XD.msg('未勾选下载文件');
                        return;
                    }
                    if (treeview.getView().getStore().data.length == records.length) {
                        records.splice(0, 1);
                    }
                    view.download(records);
                }
            }, {
                xtype: 'button', text: '全部下载', handler: function () {
                    var view = this.findParentByType('electronicPro');
                    var records = view.down('treepanel').getStore().getRoot().childNodes;
                    if (records.length == 0) {
                        XD.msg('没有可下载文件');
                        return;
                    }
                    view.download(records);
                }
            }, {
                xtype: 'button', text: '删除', handler: function () {
                    var view = this.findParentByType('electronicPro');
                    view.del();
                }
            }, {
                xtype: 'button', text: '上移', handler: function () {
                    var view = this.findParentByType('electronicPro');
                    view.moveup();
                }
            }, {
                xtype: 'button', text: '下移', handler: function () {
                    var view = this.findParentByType('electronicPro');
                    view.movedown();
                }
            }, {
                xtype: 'button', text: '打印',itemId:'print',
                handler: function () {
                    var view = this.findParentByType('electronicPro');
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
                    var url = '/electronic/media?entrytype=' + view.entrytype + '&eleid=' + eleid[0] + '&filename=' + filename + '&filetype=' + filename.substring(filename.lastIndexOf('.') + 1);
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
                                iframe.contentDocument.body.innerHTML=img.outerHTML;
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
                            iframe.setAttribute('src',  '/electronic/printYWMedia?entrytype='+view.entrytype+'&btnType=YW&eleid='+eleid[0]);
                            iframe.onload=function () {
                                iframe.contentWindow.print();
                            };
                        }
                    }
                }
            },{
                xtype: 'button', text: '批量打印',itemId:'batchprint',
                handler: function () {
                    var view = this.findParentByType('electronicPro');
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

                    if (eleid.length < 1) {
                        XD.msg('请选择数据！');
                        return;
                    }
                    if (document.getElementById("print-iframe")) {
                        document.getElementById("print-iframe").parentNode.removeChild(document.getElementById("print-iframe"));
                    }

                    var url = '/electronic/batchprint?datatype=' + view.entrytype + '&eleid=' + eleid ;
                    var newurl = encodeURI(url);
                    var iframe = document.createElement('IFRAME');
                    iframe.setAttribute("id", "print-iframe");
                    iframe.setAttribute('style', 'position:absolute;width:"100%";height:100%;left:-500px;top:-500px;');
                    iframe.setAttribute('src', newurl);
                    document.body.appendChild(iframe);
                    Ext.MessageBox.wait('正在处理中...请稍后....','提示');
                    iframe.contentWindow.focus();
                    iframe.onload = function () {
                        // iframe.contentWindow.origin  = window.location.protocol + "//" + "localhost" + (window.location.port ? ':' + window.location.port: '');
                        iframe.contentWindow.print();
                        Ext.MessageBox.hide();
                    };
                }
            },{
                xtype: 'button', text: '查看历史版本', itemId: 'getEleVersion', hidden: true
            }]
        }],
        buttons:{
            xtype:'label',
            itemId:'etips',
            hidden: true,
            html:' <i class="fa fa-info-circle"></i>  温馨提示：支持常用图片和PDF格式的电子文件预览，其它格式可下载到本地查看',
            style:{color:'red','padding-left':'1em'}
        },
        listeners: {
            beforeload: function () {
                var view = this.findParentByType('electronicPro');
                this.getStore().proxy.url = '/electronic/electronics/tree/' + view.entrytype + '/' + view.entryid + '/'+window.remainEleids;
            },
            afterrender: function () {
                this.expandAll();
            },
            itemclick: function ( view, record, item, index, e, eOpts )  {
                if (!record.get('leaf')){
                    return;
                }
                // if(e.getTarget('.x-tree-checkbox',1,true)){
                //     return;
                // }
                var view = this.findParentByType('electronicPro');
                // var mediaFrame = document.getElementById('mediaFrame');
                //当采集、管理模块在未归已归、案卷、卷内点击著录或修改时，会创建多个相同ID的iframe
                //document.getElementById只会拿第一个，导致下面的src对应不了正确显示的那个iframe
                var allMediaFrame = document.querySelectorAll('#mediaFrame');
                var mediaFrame;
                //创建electronicview需要指定是著录还是修改类型，经调试，著录的iframe是第一个，修改的是最后一个
                if (allMediaFrame.length > 0 && view.operateFlag == 'add') {
                    mediaFrame = allMediaFrame[allMediaFrame.length - 1];
                } else {
                    mediaFrame = allMediaFrame[0];
                }
                var filename = record.get('text');
                if (view.isJy) {
                    mediaFrame.setAttribute('src', '/electronic/jyMedia?entrytype=' + view.entrytype + '&eleid=' + record.get('fnid') + '&filetype=' + filename.substring(filename.lastIndexOf('.') + 1));
                } else {
                    mediaFrame.setAttribute('src', '/electronic/media?entrytype=' + view.entrytype + '&eleid=' + record.get('fnid') + '&filetype=' + filename.substring(filename.lastIndexOf('.') + 1));
                }
            },
            select:function (view, record, item, index, e) {
                if (!record.get('leaf')){
                    return;
                }
                // if(e.getTarget('.x-tree-checkbox',1,true)){
                //     return;
                // }
                var view = this.findParentByType('electronicPro');
                // var mediaFrame = document.getElementById('mediaFrame');
                //当采集、管理模块在未归已归、案卷、卷内点击著录或修改时，会创建多个相同ID的iframe
                //document.getElementById只会拿第一个，导致下面的src对应不了正确显示的那个iframe
                var allMediaFrame = document.querySelectorAll('#mediaFrame');
                var mediaFrame;
                //创建electronicview需要指定是著录还是修改类型，经调试，著录的iframe是第一个，修改的是最后一个
                if (allMediaFrame.length > 0 && view.operateFlag == 'add') {
                    mediaFrame = allMediaFrame[allMediaFrame.length - 1];
                } else {
                    mediaFrame = allMediaFrame[0];
                }
                var filename = record.get('text');
                var subname = filename.substring(filename.lastIndexOf('.') + 1);
                var islook = ['png','jpg','pdf','jpeg','img'];
                if (view.isJy) {
                    if(islook.indexOf(subname)!=-1) {
                        document.getElementById("loadingDiv").style.display="block";
                        mediaFrame.setAttribute('src', '/electronic/jyMedia?entrytype=' + view.entrytype + '&eleid=' + record.get('fnid') + '&filetype=' + filename.substring(filename.lastIndexOf('.') + 1));
                    }
                } else {
                    if(islook.indexOf(subname)!=-1) {
                        document.getElementById("loadingDiv").style.display="block";
                        mediaFrame.setAttribute('src', '/electronic/media?entrytype=' + view.entrytype + '&eleid=' + record.get('fnid') + '&filetype=' + filename.substring(filename.lastIndexOf('.') + 1));
                    }
                }
                //加载结束--隐藏loading.gif
                if (mediaFrame.attachEvent) {
                    mediaFrame.attachEvent("onload", function() {
                        document.getElementById("loadingDiv").style.display="none";
                    });
                } else {
                    mediaFrame.onload = function() {
                        document.getElementById("loadingDiv").style.display="none";
                    };
                }
                //更新下方的文件元数据
                var treeLabelView =  this.findParentByType('treeLabelView');
                var electronicMetadata = treeLabelView.down('[itemId=electronicMetadata]');
                Ext.Ajax.request({
                    method: 'GET',
                    url: '/electronic/findByEleid?eleid=' + record.get('fnid'),
                    scope: this,
                    async: false,
                    success: function (response) {
                        var responseText = Ext.decode(response.responseText);
                        electronicMetadata.down('[itemId=sortsequence]').setValue(responseText.sortsequence);
                        electronicMetadata.down('[itemId=filetype]').setValue(responseText.filetype);
                        electronicMetadata.down('[itemId=filesize]').setValue((responseText.filesize/1024/1024).toFixed(2)+'MB');
                        electronicMetadata.down('[itemId=pages]').setValue(responseText.pages);
                        electronicMetadata.down('[itemId=filepath]').setValue(responseText.filepath);
                    }
                });
            }
        }
    }, {
        region: 'south',
        header: true,
        //split: true,         // enable resizing
        layout: 'form',
        xtype: 'panel',
        itemId: 'electronicMetadata',
        flex: 1,
        collapsible: true,
        collapsed: true,
        collapseToolText: '收起',
        expandToolText: '展开',
        titleCollapse:true,
        title: '文件元数据',
        items: [ {
            xtype: 'textfield',
            fieldLabel: '序号',
            allowBlank: false,
            name: 'sortsequence',
            itemId:'sortsequence'
        },{
            xtype: 'textfield',
            fieldLabel: '文件格式',
            allowBlank: false,
            name: 'filetype',
            itemId:'filetype'
        },{
            xtype: 'textfield',
            fieldLabel: '文件大小',
            allowBlank: false,
            name: 'filesize',
            itemId:'filesize'
        },{
            xtype: 'textfield',
            fieldLabel: '页数',
            allowBlank: false,
            name: 'pages',
            itemId:'pages'
        },{
            xtype: 'textfield',
            fieldLabel: '文件存储位置',
            allowBlank: false,
            name: 'filepath',
            itemId:'filepath'
        }]
    }]
});







