/**
 * Created by Administrator on 2020/7/21.
 */


Ext.define('ManageCenter.view.ManageCenterView', {
    extend: 'Ext.tab.Panel',
    xtype: 'manageCenterView',

    //标签页靠左配置--start
    tabPosition: 'top',
    tabRotation: 0,
    //标签页靠左配置--end

    activeTab: 0,
    items: [{
        xtype: 'panel',
        layout: 'card',
        itemId: 'panelcardId',
        title: '立档单位',
        activeItem: 0,
        items: [{
            xtype: 'manageCenterDataView',
            address: 'unit'
        }, {
            layout: 'border',
            xtype: 'panel',
            itemId: 'gridview',
            items: [{
                region: 'west',
                width: XD.treeWidth,
                xtype: 'treepanel',
                itemId: 'treepanelId',
                rootVisible: false,
                store: 'ManagementStore',
                collapsible: true,
                split: 1,
                header: false,
                hideHeaders: true
            }, {
                region: 'center',
                layout: 'card',
                itemId: 'gridcard',
                activeItem: 2,
                items: [{
                    itemId: 'onlygrid',
                    xtype: 'managementgrid'
                }, {
                    itemId: 'pairgrid',
                    layout: {
                        type: 'vbox',
                        pack: 'start',
                        align: 'stretch'
                    },
                    items: [{
                        flex: 3,
                        itemId: 'northgrid',
                        xtype: 'managementgrid'
                    }, {
                        flex: 2.3,
                        itemId: 'southgrid',
                        xtype: 'entrygrid',
                        collapsible: true,
                        collapseToolText: '收起',
                        expandToolText: '展开',
                        collapsed: true,
                        split: true,
                        allowDrag: true,
                        hasSearchBar: false,
                        expandOrcollapse: 'expand',//默认打开
                        tbar: [],
                        listeners: {
                            "collapse": function (view) {
                                view.expandOrcollapse = 'collapse';
                            },
                            "expand": function (view) {
                                view.expandOrcollapse = 'expand';
                            }
                        }
                    }]
                }, {
                    xtype: 'panel',
                    itemId: 'bgSelectOrgan',
                    bodyStyle: 'background:#DFE8F6;background-image:url(../../img/background/bg_select_organ.jpg);background-repeat:no-repeat;background-position:center;'
                }]
            }]
        }]
    }, {
        title: '汇总',
        xtype:'panel',
        layout:'fit',
        items:[{
            xtype:'manageCenterTotalView'
        }]
    }, {
        title: '年度汇总',
        xtype:'panel',
        layout:'fit',
        items:[{
            xtype:'manageCenterYearView'
        }]
    }, {
        title: '单位汇总',
        xtype:'panel',
        layout:'fit',
        items:[{
            xtype:'manageCenterUnitView'
        }]
    }]
});
