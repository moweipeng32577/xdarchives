/**
 * Created by yl on 2017/11/3.
 */
Ext.define('Mission.view.MissionAdminsView', {
    extend: 'Ext.tab.Panel',
    xtype: 'missionAdminsView',
    requires: [
        'Ext.layout.container.Border'
    ],
    //标签页靠左配置--start
    tabPosition: 'top',
    tabRotation: 0,
    //标签页靠左配置--end

    activeTab: 0,

    items: [{
        title: '销毁审批',
        layout: 'border',
        itemId: 'xhMessionId',
        items: [{
            xtype: 'destroyTreeView',
            itemId: 'destroyTreeId',
            bodyBorder: false
        }, {
            xtype: 'destroyGridView'
        }]
    }, {
        title: '开放审批',
        layout: 'border',
        itemId: 'kfMessionId',
        items: [{
            xtype: 'openTreeView',
            itemId: 'openTreeId',
            bodyBorder: false
        }, {
            xtype: 'openGridView'
        }]
    },{
        title: '查档审批',
        layout: 'border',
        itemId: 'dzMessionId',
        items: [{
            xtype: 'dzJyTreeView',
            itemId: 'dzJyTreeId',
            bodyBorder: false
        }, {
            xtype: 'dzJyGridView'
        }]
    },
    //     {
    //     title: '实体查档审批',
    //     layout: 'border',
    //     itemId: 'stMessionId',
    //     items: [{
    //         xtype: 'stJyTreeView',
    //         itemId: 'stJyTreeId',
    //         bodyBorder: false
    //     }, {
    //         xtype: 'stJyGridView'
    //     }]
    // },
        {
            title: '电子打印审批',
            layout: 'border',
            itemId: 'printMessionId',
            items: [{
                xtype: 'dzPrintTreeView',
                itemId: 'dzPrintTreeId',
                bodyBorder: false
            }, {
                xtype: 'dzPrintGridView'
            }]
        },{
            title: '数据审核审批',
            layout: 'border',
            itemId: 'auditMessionId',
            items: [{
                xtype: 'auditTreeView',
                itemId: 'auditTreeViewId',
                bodyBorder: false
            }, {
                xtype: 'auditGridView'
            }]
        }],
    listeners: {
        beforerender: function (view) {
            Ext.Ajax.request({
                url: '/jyAdmins/getplatformopen',
                method: 'get',
                async:false,
                success: function (resp) {
                    var respText = Ext.decode(resp.responseText);
                    if(respText.data=='false'){
                        view.remove(2);
                        view.remove(2);
                    }
                }
            });
        }
    }
});