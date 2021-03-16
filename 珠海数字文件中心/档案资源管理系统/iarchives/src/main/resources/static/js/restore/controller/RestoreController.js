/**
 * Created by RonJiang on 2018/1/22 0022.
 */
Ext.define('Restore.controller.RestoreController', {
    extend: 'Ext.app.Controller',
    views: ['RestoreView', 'RestoreGridView'],
    models: ['RestoreGridModel', 'RestoreTreeModel'],
    stores: ['RestoreGridStore', 'RetoreTreeStore'],
    init: function () {
        this.control({
            'restoreView [itemId=treepanelId]': {
                render: function (view) {
                    view.on('checkchange', function (node, checked) {
                        node.expand();
                        node.eachChild(function (child) {
                            child.set("checked", checked);
                        });
                        if (node.parentNode != null) {
                            var allchecked = true;
                            Ext.each(node.parentNode.childNodes, function (node) {
                                if (!node.get('checked')) {
                                    allchecked = false;
                                }
                            });
                            if (allchecked) {
                                node.parentNode.set("checked", true);
                            } else {
                                node.parentNode.set("checked", false);
                            }
                        }
                    }, view);
                }
            },
            'restoreGridView': {
                render: function (view) {
                    view.getSelectionModel().setSelectionMode('SINGLE');
                },
                select: function (sel, record) {
                    var restoreView = sel.view.findParentByType('restoreView');
                    restoreView.down('[itemId=gridsel]').setValue(record.get('filename'));
                    var treestore = restoreView.down('[itemId=treepanelId]').getStore();
                    treestore.proxy.extraParams = {filename: record.get('filename')};
                    treestore.reload();
                }
            },
            'restoreGridView button[itemId=refresh]': {
                click: function (btn) {
                    btn.findParentByType('restoreGridView').getStore().reload();
                }
            },
            'restoreView button[itemId=close]': {
                click: function () {
                    parent.closeObj.close(parent.layer.getFrameIndex(window.name));
                }
            },
             'restoreView button[itemId=restore]': {
                click: function (btn) {
                    var restoreView = btn.findParentByType('restoreView');
                    var tree =restoreView .down('[itemId=treepanelId]');
                    var records = tree.getView().getChecked();
                    if (records.length === 0) {
                        XD.msg('请选择需要恢复的数据');
                        return;
                    }
//                    else if (records.length === 1) {
//                        if(typeof(records[0].get('fnid'))=='undefined'){
//                            XD.msg('请选择有效的备份文件');
//                            return;
//                        }
//                    }
                    XD.confirm('是否确定将选中的数据进行系统恢复',function(){
                        var ids = [];
                        for (var i = 0; i < records.length; i++) {
                            if(typeof(records[i].get('fnid'))!=='undefined'){
                                ids.push(records[i].get('fnid'));
                            }
                        }
                        Ext.Ajax.request({
                            params: {
                                fnidarr:ids.join(','),
                                filename:restoreView.down('[itemId=gridsel]').getValue(),
                                userid:userid
                            },
                            url: '/backupRestore/restore',
                            method: 'post',
                            timeout:XD.timeout,
                            success: function (resp) {
                                var respText = Ext.decode(resp.responseText);
                                XD.msg(respText.msg);
                                var socket = new SockJS("/websocket");
                                stompClient = Stomp.over(socket);
                                var backupFlag=true;
                                stompClient.connect({}, function(frame) {
                                    stompClient.subscribe('/user/'+userid+'/restore', function(respnose){
                                        var progressText = respnose.body;//
                                        if (backupFlag) {
                                            XD.msg(progressText);
                                            backupFlag=false;
                                        }
                                    });
                                });
                            },
                            failure: function () {
                                XD.msg("恢复失败");
                            }
                        });
                    });
                }
            },
            'restoreGridView button[itemId=upload]': {
                click: function (btn) {
                    var view = btn.findParentByType('restoreGridView');
                    var win = Ext.create('Comps.view.UploadView');
                    win.on('close', function () {
                        view.getStore().reload();
                    }, view);
                    win.show();
                }
            }
        })
    }
});