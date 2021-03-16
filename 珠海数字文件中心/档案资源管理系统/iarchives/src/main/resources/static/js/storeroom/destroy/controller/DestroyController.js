/**
 * Created by yl on 2017/10/26.
 */
Ext.define('Destroy.controller.DestroyController', {
    extend: 'Ext.app.Controller',

    views: ['DestructionBillView', 'DestructionBillTreeView','DestructionBillGridView',
        'DestructionBillInfoView', 'DestructionBillDetailView', 'DestructionBillDetailGridView',
        'DestructionBillInstructionsView','DealDetailsGridView'],//加载view
    stores: ['DestructionBillGridStore', 'DestructionBillDetailGridStore','DealDetailsGridStore'],//加载store
    models: ['DestructionBillGridModel', 'DestructionBillDetailGridModel','DealDetailsGridModel'],//加载model
    init: function () {
        var billId;
        var nodeId;
        var destructionBillGridView;
        var destructionBillDetailGridView;
        this.control({
            'destructionBillTreeView': {
                select: function (treemodel, record) {
                    var destructionBillGridView = treemodel.view.findParentByType('destructionBillView').down('destructionBillGridView');
                    if (record.get('leaf')) {
                        destructionBillGridView.setTitle("当前位置：" + record.get('text'));
                        treeId=record.get('id');
                        destructionBillGridView.getDockedItems('toolbar[dock="top"]')[0].removeAll();
                        var toolbar = [];
                        switch (record.get('id')) {
                            case 4://已执行
                                toolbar.push(
                                    {
                                        itemId:'xhDealDetailsId',
                                        xtype: 'button',
                                        iconCls:'fa fa-newspaper-o',
                                        text: '办理详情'
                                    },
                                    '-',
                                    {
                                        xtype: 'button',
                                        text: '查看单据记录',
                                        itemId: 'lookDetailBill',
                                        iconCls:'fa fa-indent'
                                    }, '-', {
                                        xtype: 'button',
                                        itemId: 'implementKf',
                                        text: '执行库存销毁',
                                        iconCls:'fa fa-check-square'
                                    }, '-', {
                                        xtype: 'button',
                                        itemId: 'viewApproval',
                                        text: '查看批示',
                                        iconCls:'fa fa-comment-o'
                                    }, '-',
                                    {
                                        itemId:'xhDealDetailsId',
                                        xtype: 'button',
                                        iconCls:'fa fa-newspaper-o',
                                        text: '办理详情'
                                    },
                                    '-',
                                    {
                                        xtype: 'button',
                                        itemId: 'print',
                                        text: '打印',
                                        iconCls:'fa fa-print'
                                    });
                                break;
                            case 7://已执行实体库存销毁
                                toolbar.push(
                                    {
                                        itemId:'xhDealDetailsId',
                                        xtype: 'button',
                                        iconCls:'fa fa-newspaper-o',
                                        text: '办理详情'
                                    },
                                    '-',
                                    {
                                        xtype: 'button',
                                        text: '查看单据记录',
                                        itemId: 'lookDetailBill',
                                        iconCls:'fa fa-indent'
                                    }, '-', {
                                        xtype: 'button',
                                        itemId: 'viewApproval',
                                        text: '查看批示',
                                        iconCls:'fa fa-comment-o'
                                    }, '-',
                                    {
                                        itemId:'xhDealDetailsId',
                                        xtype: 'button',
                                        iconCls:'fa fa-newspaper-o',
                                        text: '办理详情'
                                    },
                                    '-',
                                    {
                                        xtype: 'button',
                                        itemId: 'print',
                                        text: '打印',
                                        iconCls:'fa fa-print'
                                    });
                                break;
                            default:
                        }
                        if (record.get('id') == '1' ||record.get('id') == '6') {
                            Ext.Ajax.request({//根据审批id判断是否可以催办
                                url: '/destructionBill/findByWorkId',
                                method: 'GET',
                                success: function (resp) {
                                    var respDate = Ext.decode(resp.responseText).data;
                                    if(respDate.urgingstate=="1"){
                                        destructionBillGridView.down('[itemId=urging]').show();
                                        destructionBillGridView.down('[itemId=message]').show();
                                    }
                                }
                            });
                            destructionBillGridView.columns[1].show();
                            destructionBillGridView.columns[2].show();
                        } else {
                            destructionBillGridView.columns[1].hide();
                            destructionBillGridView.columns[2].hide();
                        }
                        destructionBillGridView.getDockedItems('toolbar[dock="top"]')[0].add(toolbar);
                        destructionBillGridView.initGrid({ state: record.get('id') });
                    }
                }
            },
            'destructionBillGridView button[itemId=lookDetailBill]': {//查看单据记录
                click: function (btn) {
                    destructionBillGridView = btn.findParentByType('destructionBillGridView');
                    var select = destructionBillGridView.getSelectionModel().getSelection();
                    if (select.length == 0) {
                        XD.msg('请至少选择一条需要查看的数据');
                        return;
                    } else if (select.length != 1) {
                        XD.msg('查看只能选中一条数据');
                        return;
                    } else {
                        billId = select[0].get("billid");
                        nodeId = select[0].get("nodeid");
                        var window = Ext.create('Destroy.view.DestructionBillDetailView');
                        window.show();
                    }
                }
            },

            'destructionBillDetailGridView button[itemId=backBill]': {//查看单据记录界面返回
                click: function (btn) {
                    this.findDesBillDetailView(btn).hide();
                }
            },

            'destructionBillInfoView': {//查看单据north位置的form
                afterRender: function (view) {
                    view.loadRecord(destructionBillGridView.getSelectionModel().getLastSelected());
                }
            },

            'destructionBillDetailGridView': {//查看单据south位置的grid
                afterrender: function (view) {
                    var toolbar = [];
                    if(treeId==2){
                        toolbar.push({
                            xtype: 'button',
                            itemId:'outStoreId',
                            text: '出库'
                        },'-',{
                            xtype: 'button',
                            itemId:'inStoreId',
                            text: '入库'
                        },'-',{
                            xtype: 'button',
                            itemId:'backBill',
                            text: '返回'
                        });
                    }else{
                        toolbar.push({
                            xtype: 'button',
                            itemId:'backBill',
                            text: '返回'
                        }/*, '-', {
                            xtype: 'button',
                            text: '查看',
                            itemId:'lookBill'
                        }*/);
                    }
                    view.initGrid({billId:billId});
                    view.getDockedItems('toolbar[dock=top]')[0].add(toolbar);
                }
            },

            'destructionBillGridView button[itemId=viewApproval]': {//查看批示
                click: function (btn) {
                    destructionBillGridView = btn.findParentByType('destructionBillGridView');
                    var select = destructionBillGridView.getSelectionModel().getSelection();
                    if (select.length == 0) {
                        XD.msg('请至少选择一条需要查看的数据');
                        return;
                    } else if (select.length != 1) {
                        XD.msg('查看只能选中一条数据');
                        return;
                    } else {
                        var window = Ext.create('Destroy.view.DestructionBillInstructionsView');
                        window.show();
                    }
                }
            },

            'destructionBillInstructionsView':{
                render: function (view) {
                    var form =view.down('form');
                    form.load({
                        url: '/destructionBill/findByBillid',
                        params: {
                            billid: destructionBillGridView.getSelectionModel().getSelection()[0].get("billid")
                        }
                    });
                }
            },

            'destructionBillGridView button[itemId=xhDealDetailsId]': {//办理详情
                click: function (view) {
                    var destructionBillGridView = view.findParentByType('destructionBillGridView');
                    var select = destructionBillGridView.getSelectionModel();
                    if (!select.hasSelection()) {
                        XD.msg('请选择一条数据!');
                    }
                    var details = select.getSelection();
                    if(details.length!=1){
                        XD.msg('只支持单条数据查看!');
                        return;
                    }
                    var billId = details[0].get("billid");
                    this.showDealDetailsWin(billId);
                }
            },

            'destructionBillGridView button[itemId=print]': {//打印报表
                click:this.printHandler
            },
            'destructionBillGridView button[itemId=implementKf]': {//执行库房销毁
                click: function (btn) {
                    destructionBillGridView = btn.findParentByType('destructionBillGridView');
                    var select = destructionBillGridView.getSelectionModel().getSelection();
                    if (select.length == 0) {
                        XD.msg('请至少选择一条需要执行的数据');
                        return;
                    } else {
                        XD.confirm('确定要执行这' + select.length + '条数据吗',function(){
                            var billIds = [];
                            for (var i = 0; i < select.length; i++) {
                                billIds.push(select[i].get('billid'));
                            }
                            Ext.Ajax.request({
                                params: {
                                    billids:billIds
                                },
                                url: '/destructionBill/implementKfBill/',
                                method: 'POST',
                                sync: true,
                                success: function (resp) {
                                    var resp = Ext.decode(resp.responseText);
                                    if('无法执行'==resp.msg){
                                        var titles = resp.data;
                                        var title;
                                        for(var i=0;i<titles.length;i++){
                                            if(i==0){
                                                title = '['+titles[i]+']';
                                            }else{
                                                title = title + '，' + '['+titles[i]+']';
                                            }
                                        }
                                        //XD.msg('无法执行，这  '+titles.length+'  条题名为  '+title+'  还处于未归状态')
                                        XD.msg('无法执行销毁，请到实体档案出库中处理借出的档案后再进行销毁。出库未归还的档案:'+title);
                                    }else {
                                        XD.msg(resp.msg);
                                        destructionBillGridView.notResetInitGrid({state: '4'});
                                    }
                                },
                                failure : function() {
                                    XD.msg('操作失败');
                                }
                            });
                        },this);
                    }
                }
            }

        });
    },

    findDesBillDetailView: function (btn) {//获取查看单据窗口
        return btn.findParentByType('destructionBillDetailView');
    },

    showDealDetailsWin:function(id){
        var dealDetailsWin = Ext.create('Ext.window.Window',{
            modal:true,
            width:1000,
            height:530,
            title:'办理详情',
            layout:'fit',
            closeToolText:'关闭',
            closeAction:'hide',
            items:[{
                xtype: 'DealDetailsGridView'
            }]
        });
        var store = dealDetailsWin.down('DealDetailsGridView').getStore();
        store.proxy.extraParams.billid = id;
        store.reload();
        dealDetailsWin.show();
    },

    printHandler:function (btn) {
        var grid = btn.up('destructionBillGridView');

        var ids = [];
        var params = {};
        Ext.each(grid.getSelectionModel().getSelection(),function(){
            ids.push(this.get('billid'));
        });
        if(reportServer == 'UReport') {
            params['billid'] = ids.join(",");
            XD.UReportPrint(null, '销毁单据明细管理', params);
        }
        else if(reportServer == 'FReport') {
            XD.FRprint(null, '销毁单据明细管理', ids.length > 0 ? "'billid':'" + ids.join(",") + "'" : '');
        }
    }
});