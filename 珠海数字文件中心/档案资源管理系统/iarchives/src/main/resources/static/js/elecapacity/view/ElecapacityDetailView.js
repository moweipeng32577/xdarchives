/**
 * Created by Administrator on 2017/10/23 0023.
 */
Ext.define('Elecapacity.view.ElecapacityDetailView', {
    extend: 'Ext.panel.Panel',
    xtype: 'elecapacityDetailView',
    itemId:'ElecapacityDetailViewId',
    width: 300,
    minHeight: 350,
    layout: {
        type: 'vbox',
        align: 'stretch'
    },
    defaults: {
        layout: 'form',
        xtype: 'container',
        defaultType: 'textfield',
    },
    items: [{
        xtype: 'form',
        modelValidation: true,
        itemId:'selectApprove',
        layout:'column',
        items: [
            {
                columnWidth:0.5,
                itemId:'capacitySizeId',
                fieldLabel: '已用总空间',
                name:'capacitySize',
                margin:'15',
                readOnly:true
            }, {
                columnWidth:0.5,
                itemId:'capacityNumId',
                fieldLabel: '电子文件总数',
                name:'capacityNum',
                margin:'15',
                readOnly:true
            },  {
                columnWidth:0.5,
                itemId:'captureCapacitySizeId',
                fieldLabel: '采集库',
                name:'captureCapacitySize',
                margin:'15',
                readOnly:true
            }, {
                columnWidth:0.5,
                itemId:'captureCapacityNumId',
                fieldLabel: '采集文件数',
                name:'captureCapacityNum',
                margin:'15',
                readOnly:true
            },  {
                columnWidth:0.5,
                itemId:'manageCapacitySizeId',
                fieldLabel: '管理库',
                name:'manangeCapacitySize',
                margin:'15',
                readOnly:true
            }, {
                columnWidth:0.5,
                itemId:'manageCapacitySumId',
                fieldLabel: '管理文件数',
                name:'manangeCapacityNum',
                margin:'15',
                readOnly:true
            },  {
                columnWidth:0.5,
                itemId:'solidCapacitySizeId',
                fieldLabel: '固化库',
                name:'solidCapacitySize',
                margin:'15',
                readOnly:true
            }, {
                columnWidth:0.5,
                itemId:'solidCapacitySumId',
                fieldLabel: '固化文件数',
                name:'solidCapacityNum',
                margin:'15',
                readOnly:true
            },  {
                columnWidth:0.5,
                itemId:'fzbId',
                fieldLabel: '封装包容量',
                name:'fzbCapacitySize',
                margin:'15',
                readOnly:true
            }, {
                columnWidth:0.5,
                itemId:'fzwjId',
                fieldLabel: '封装文件数',
                name:'fzbCapacityNum',
                margin:'15',
                readOnly:true
            }
        ]
    }]
});