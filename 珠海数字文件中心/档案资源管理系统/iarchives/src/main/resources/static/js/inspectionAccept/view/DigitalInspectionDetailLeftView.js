Ext.define('DigitalInspection.view.DigitalInspectionDetailLeftView', {
    extend:'Ext.panel.Panel',
    xtype:'DigitalInspectionDetailLeftView',
    layout:'border',
    items: [
        {
            region: 'north',
            height:200,
            layout: 'form',
            xtype:'form',
            header:false,
            title:'批次信息',
            collapsible:true,
            collapseToolText:'收起',
            expandToolText:'展开',
            collapsed: true,
            split:true,
            allowDrag:true,
            items:[
                {
                    xtype: 'textfield',
                    name: 'batchcode',
                    fieldLabel: '批次号',
                    value: '',
                    readOnly:true
                },
                {
                    xtype: 'textfield',
                    name: 'batchname',
                    fieldLabel: '批次名',
                    value: '',
                    readOnly:true
                },
                {
                    xtype: 'textfield',
                    name: 'archivetype',
                    fieldLabel: '抽检类型',
                    value: '',
                    readOnly:true
                },
                {
                    xtype: 'textfield',
                    name: 'inspector',
                    fieldLabel: '抽检员',
                    value: '',
                    readOnly:true
                }
            ]
        },
        {
            region: 'center',
            xtype:'entrygrid',
            itemId:'detailEntryGridId',
            title:'条目信息(双击条目进入修改页面)',
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
            store:'DigitalInspectionEntryDetailGridStore',
            columns:[
                {text: '案卷号', dataIndex: 'filecode', flex: 2, menuDisabled: true},
                {text: '档号', dataIndex: 'archivecode', flex: 2, menuDisabled: true},
                {text: '页数', dataIndex: 'pagenum', flex: 2, menuDisabled: true},
                {text: '状态', dataIndex: 'status', flex: 2, menuDisabled: true}
            ],
            tbar:[]
        },
        {
            region: 'south',
            height:250,
            title:'原文信息(单击条目切换原文)',
            xtype:'entrygrid',
            itemId:'detailMediaEntryGridId',
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
            store:'DigitalInspectionEntryMediaGridStore',
            columns:[
                {text: '文件名', dataIndex: 'medianame', flex: 5, menuDisabled: true},
                {text: '状态', dataIndex: 'status', flex: 2, menuDisabled: true},
            ],
            tbar:[]
        }
    ],
});
