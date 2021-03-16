Ext.define('DigitalInspection.view.DigitalInspectionDetailRightView', {
    extend:'Ext.panel.Panel',
    xtype:'DigitalInspectionDetailRightView',
    layout:'border',
    items: [
        {
            region: 'center',
            xtype: 'DigitalInspectionDetailRightTopView',
        },
        {
            region: 'south',
            xtype:'entrygrid',
            itemId:'detailErrorId',
            title:'错误标识',
            hasPageBar:false,
            collapsible:true,
            collapseToolText:'收起',
            expandToolText:'展开',
            collapsed: false,
            split:true,
            allowDrag:true,
            hasSearchBar:false,
            expandOrcollapse:'expand',//默认打开
            store:'DigitalInspectionMediaErrGridStore',
            height:250,
            columns:[
                {text: '错误类型', dataIndex: 'errtype', flex: 2, menuDisabled: true},
                {text: '描述', dataIndex: 'depict', flex: 2, menuDisabled: true},
                {text: '文件名', dataIndex: 'filename', flex: 2, menuDisabled: true},
                {text: '状态', dataIndex: 'status', flex: 2, menuDisabled: true}
            ],
            tbar:{
                overflowHandler:'scroller',
                items:[
                    {
                        text:'增加',
                        iconCls:'fa fa-plus-circle',
                        itemId:'add'
                    },'-',{
                        text:'删除',
                        iconCls:'fa fa-trash-o',
                        itemId:'del'
                    },
                    {
                        text:'修复',
                        iconCls:'fa fa-cogs',
                        itemId:'repair'
                    },'-',{
                        text:'报告',
                        iconCls:'fa fa-download',
                        itemId:'export'
                    }
                ]
            }
        }
    ],
});
