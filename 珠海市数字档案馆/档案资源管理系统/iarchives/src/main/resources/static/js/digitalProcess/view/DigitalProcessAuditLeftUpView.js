/**
 * Created by Administrator on 2019/9/20.
 */


Ext.define('DigitalProcess.view.DigitalProcessAuditLeftUpView', {
    extend:'Ext.panel.Panel',
    xtype:'DigitalProcessAuditLeftUpView',
    layout:'border',
    items: [
        {
            region: 'center',
            xtype:'entrygrid',
            itemId:'detailEntryGridId',
            title:'条目信息',
            hasPageBar:false,
            collapsible:true,
            collapseToolText:'收起',
            expandToolText:'展开',
            collapsed: false,
            split:true,
            allowDrag:true,
            hasSearchBar:false,
            hasCheckColumn:false,
            expandOrcollapse:'expand',//默认打开
            store:'DigitalProcessAuditAuditGridStore',
            columns:[
                {text: '档号', dataIndex: 'archivecode', flex: 2, menuDisabled: true},
                {text: '实体签收人', dataIndex: 'entrysigner', flex: 2, menuDisabled: true}
            ],
            tbar:[{
                itemId:'pre',
                xtype: 'button',
                text: '上一条'
            },{
                itemId:'next',
                xtype: 'button',
                text: '下一条'
            },{
                text:'退回环节',
                iconCls:'fa fa-reply',
                itemId:'backLink'
            }]
        },
        {
            region: 'south',
            height:'60%',
            title:'原文信息',
            xtype: 'panel',
            itemId:"eleGridId",
            collapsible:true,
            collapseToolText:'收起',
            expandToolText:'展开',
            collapsed: false,
            split:true,
            allowDrag:true,
            expandOrcollapse:'expand',//默认打开
            items: [{
                xtype:'entrygrid',
                itemId:'detailMediaEntryGridId',
                hasSearchBar:false,
                hasCheckColumn:false,
                hasPageBar:false,
                store:'DigitalProcessMediaGridStore',
                columns:[
                    {text: 'id', dataIndex: 'id', flex: 1.5, menuDisabled: true,hidden:true},
                    {text: '文件名', dataIndex: 'filename',flex: 3, menuDisabled: true},
                    {text: '状态', dataIndex: 'status', flex: 1.5, menuDisabled: true}
                ]
            }]
        }
    ]
});
