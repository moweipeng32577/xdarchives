/**
 * Created by Administrator on 2020/7/21.
 */


Ext.define('ManageCenter.controller.ManageCenterController', {
    extend: 'Ext.app.Controller',
    views: [
        'ManageCenterDataView','ManageCenterView','ManageCenterUnitView','ManageCenterYearView',
        'ManageCenterTotalView','ManagementGridView'
    ],
    models: [
        'ManageCenterDataModel','ManageCenterUnitModel','ManageCenterYearModel','ManagementModel'
    ],
    stores: [
        'ManageCenterDataStore','ManageCenterUnitStore','ManageCenterYearStore','ManagementStore'
    ],

    init: function () {
        var treeNode;
        this.control({
            'manageCenterView': {
                afterrender: function (view) {
                    var manageCenterDataView = view.down('manageCenterDataView');
                    manageCenterDataView.initGrid();
                },
                tabchange:function (view) {
                    if(view.activeTab.title=='立档单位'){
                        var manageCenterDataView = view.down('manageCenterDataView');
                        manageCenterDataView.initGrid();
                    }else if(view.activeTab.title=='单位汇总'){
                        var manageCenterUnitView = view.down('manageCenterUnitView');
                        manageCenterUnitView.initGrid();
                    }else if(view.activeTab.title=='年度汇总'){
                        var manageCenterYearView = view.down('manageCenterYearView');
                        manageCenterYearView.initGrid();
                    }else{
                        var manageCenterTotalView = view.down('manageCenterTotalView');
                        Ext.MessageBox.wait('正在获取数据请稍后...','提示');
                        manageCenterTotalView.down('form').load({
                            url:'/manageCenter/getManageCenterTotal',
                            method:'POST',
                            params:{
                            },
                            success:function () {
                                Ext.MessageBox.hide();
                            },
                            failure:function () {
                                Ext.MessageBox.hide();
                                XD.msg('获取表单信息失败');
                            }
                        });
                    }
                }
            },

            'manageCenterDataView [itemId=lookId]':{  //立档单位-查看
                click:function (view) {
                    var manageCenterDataView = view.findParentByType('manageCenterDataView');
                    var manageCenterView = view.findParentByType('manageCenterView');
                    var panelcard = manageCenterView.down('[itemId=panelcardId]');
                    var gridview = panelcard.down('[itemId=gridview]');
                    var tree = gridview.down('[itemId=treepanelId]');
                    var record = manageCenterDataView.acrossSelections ;
                    if(record.length!=1){
                        XD.msg('只能选择一条需要查看的数据');
                        return;
                    }
                    tree.getStore().removeAll();
                    tree.getStore().proxy.extraParams.pcid = '';
                    tree.getStore().load();
                    window.organId = record[0].get('id');
                    var parentOrganids = this.getParentOrganids(record[0].get('id'));
                    window.parentOrganids = parentOrganids;
                    tree.fireEvent('render',tree.getStore());
                    panelcard.setActiveItem(gridview);
                }
            },

            'managementgrid [itemId=backId]':{  //立档单位-查看-返回
                click:function (view) {
                    var manageCenterView = view.findParentByType('manageCenterView');
                    var manageCenterDataView = manageCenterView.down('manageCenterDataView');
                    var panelcard = manageCenterView.down('[itemId=panelcardId]');
                    panelcard.setActiveItem(manageCenterDataView);
                }
            },

            'manageCenterView [itemId=treepanelId]':{
                render: function (view) {
                    var flag = true;
                    if(window.organId != undefined){
                        view.getRootNode().on('expand', function (node) {
                            for (var i = 0; i < node.childNodes.length; i++) {
                                if (node.childNodes[i].raw.text == '全宗卷管理') {//隐藏全宗卷管理
                                    node.childNodes[i].raw.visible = false;
                                }
                                if (node.childNodes[i].raw.text == '已归管理'||node.childNodes[i].raw.text == '归档管理') {//默认打开已归管理第一条节点
                                    treeNode = node.childNodes[i].raw.id;
                                }
                                if (node.childNodes[i].raw.parentId == treeNode) {//找到已归管理下的所有节点
                                    if (node.childNodes[i].raw.roottype != 'classification') {  //当前子节点是否机构节点
                                        if (node.childNodes[i].raw.organid == window.organId) {  //当前子节点是否用户机构节点
                                            node.getOwnerTree().expandPath(node.childNodes[i].raw.id, "id");
                                            node.getOwnerTree().getSelectionModel().select(node.childNodes[i]);
                                            return;
                                        } else {
                                            //找到用户机构父节点
                                            if (window.parentOrganids.length > 0) {
                                                for (var j = 0; j < window.parentOrganids.length; j++) {
                                                    if (window.parentOrganids[j] == node.childNodes[i].raw.organid) {
                                                        flag = false;
                                                        treeNode = node.childNodes[i].raw.id;
                                                        node.getOwnerTree().expandPath(node.childNodes[i].raw.id, "id");
                                                        node.getOwnerTree().getSelectionModel().select(node.childNodes[i]);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    //如果没有找到用户机构父节点，则选择第一个子节点
                                    if(flag&&i==node.childNodes.length-1){
                                        treeNode = node.childNodes[0].raw.id;
                                        node.getOwnerTree().expandPath(node.childNodes[0].raw.id, "id");
                                        node.getOwnerTree().getSelectionModel().select(node.childNodes[0]);
                                    }
                                }
                            }
                        });
                    }
                },
                select: function(treemodel, record){
                    var gridcard = treemodel.view.findParentByType('manageCenterView').down('[itemId=gridcard]');
                    var onlygrid = gridcard.down('[itemId=onlygrid]');
                    var pairgrid = gridcard.down('[itemId=pairgrid]');
                    var grid;
                    var nodeType = record.data.nodeType;
                    var bgSelectOrgan = gridcard.down('[itemId=bgSelectOrgan]');
                    //树节点为分类，更改右边页面为“请选择机构节点”
                    if (nodeType == 2) {
                        gridcard.setActiveItem(bgSelectOrgan);
                    } else {
                        if (record.data.classlevel == 2) {
                            gridcard.setActiveItem(pairgrid);
                            var ajgrid = pairgrid.down('[itemId=northgrid]');
                            ajgrid.setTitle("当前位置：" + record.data.text);
                            var jngrid = pairgrid.down('[itemId=southgrid]');
                            jngrid.setTitle("查看卷内");
                            if (jngrid.expandOrcollapse == 'expand') {
                                jngrid.expand();
                            } else {
                                jngrid.collapse();
                            }
                            jngrid.dataUrl = '/management/entries/innerfile/' + '' + '/';
                            jngrid.initGrid(this.getNodeid(record.get('nodeid')));
                            grid = ajgrid;
                        } else {
                            gridcard.setActiveItem(onlygrid);
                            onlygrid.setTitle("当前位置：" + record.data.text);
                            grid = onlygrid;
                            window.jnNodeid="";//标记点击案卷的卷内标记nodeid为空
                        }

                        var gridview = gridcard.up('manageCenterView').down('managementgrid');
                        gridview.setTitle("当前位置：" + record.data.text);//将表单与表格视图标题改成当前位置
                        grid.nodeid = record.get('fnid');
                        grid.initGrid({nodeid: record.get('fnid')});
                    }
                }
            }
        })
    },
    getNodeid:function (parentid) {
        var nodeid;
        var params = {};
        if(typeof(parentid) != 'undefined' && parentid != ''){
            params.parentid = parentid;
        }
        Ext.Ajax.request({
            url:'/publicUtil/getNodeid',
            async:false,
            params:params,
            success:function(response){
                nodeid = Ext.decode(response.responseText).data;
            }
        });
        return nodeid;
    },

    getParentOrganids:function (organid) {
        var parentOrganids;
        Ext.Ajax.request({
            url:'/manageCenter/getParentOrganids',
            async:false,
            params:{
                organid:organid
            },
            success:function(response){
                parentOrganids = Ext.decode(response.responseText).data;
            }
        });
        return parentOrganids;
    }
});
