/**
 * Created by xd on 2017/10/21.
 */
Ext.define('DigitalInspection.view.DigitalInspectionView', {
    extend: 'Ext.tab.Panel',
    xtype:'DigitalInspectionView',
    requires: [
        'Ext.layout.container.Border'
    ],
    tabPosition: 'top',
    tabRotation: 0,
    activeTab: 0,
    items: [{
        title: '未抽检批次',
        layout: 'border',
        itemId: 'notInspectionViewId',
        items: [
            {
                layout:'fit',
                region: 'center',
                xtype: 'DigitalInspectionWclView',
            }
        ]
    }, {
        title: '正在抽检批次',
        layout: 'border',
        itemId: 'InspectioningId',
        items: [
            {
                layout:'fit',
                region: 'center',
                xtype: 'DigitalInspectionIngView',
            }
        ]
    }
    ,{
        title: '完成抽检批次',
        layout: 'border',
        itemId: 'finishInspectionId',
        items: [
            {
                layout:'fit',
                region: 'center',
                xtype: 'DigitalInspectionWcView',
            }
        ]
    }
    // ,{
    //         title: '完成验收批次',
    //         layout: 'border',
    //         itemId: 'finishAcceptId',
    //         items: [
    //             {
    //                 layout:'fit',
    //                 region: 'center',
    //                 xtype: 'DigitalInspectionWcAcceptView',
    //             }
    //         ]
    //     }
    ],
});
