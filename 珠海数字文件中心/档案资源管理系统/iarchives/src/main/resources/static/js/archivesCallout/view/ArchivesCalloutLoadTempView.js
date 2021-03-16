/**
 * Created by Administrator on 2019/3/7.
 */


Ext.define('ArchivesCallout.view.ArchivesCalloutLoadTempView', {
    extend: 'Ext.window.Window',
    xtype: 'archivesCalloutLoadTempView',
    itemId:'archivesCalloutLoadTempViewId',
    title: '模板下载',
    frame: true,
    resizable: true,
    closeToolText:'关闭',
    width: 610,
    minWidth: 610,
    minHeight: 100,
    modal:true,
    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    defaults: {
        layout: 'form',
        xtype: 'container',
        defaultType: 'textfield',
        style: 'width: 50%'
    },

    items: [
        {
            xtype: 'form',
            modelValidation: true,
            margin: '15',
            items: [
                {
                    columnWidth:1,
                    xtype: 'TreeComboboxView',
                    fieldLabel: '选择下载的节点',
                    editable: false,
                    url: '/nodesetting/getSzhWCLNodeByParentId',
                    extraParams: {pcid:''},//根节点的ParentNodeID为空，故此处传入参数为空串
                    allowBlank: false,
                    name: 'nodename',
                    itemId: 'dismantleNode',
                    margin:'20 20 5 10',
                    allowBlack:false,
                    afterLabelTextTpl: [
                        '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'
                    ]
                }
            ]
        }
    ],

    buttons: [
        { text: '提交',itemId:'loadSubmit'},
        { text: '关闭',itemId:'loadClose'}
    ]
});
