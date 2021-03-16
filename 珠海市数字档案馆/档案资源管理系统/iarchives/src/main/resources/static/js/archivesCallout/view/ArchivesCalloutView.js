/**
 * 表格与表单视图
 */
Ext.define('ArchivesCallout.view.ArchivesCalloutView',{
    extend:'Ext.panel.Panel',
    xtype:'ArchivesCalloutView',
    layout : 'fit',
    items:[{
        itemId:'pairgrid',
        layout:{
            type:'vbox',
            pack: 'start',
            align: 'stretch'
        },
        items:[{
            flex:5,
            itemId:'northgrid',
            xtype:'ArchivesCalloutGridView'
        },{
            flex:5,
            title:'批次条目',
            itemId:'southgrid',
            xtype:'entrygrid',
            collapsible:true,
            collapseToolText:'收起',
            expandToolText:'展开',
            collapsed: false,
            split:true,
            allowDrag:true,
            hasSearchBar:false,
            expandOrcollapse:'expand',//默认打开
            store:'ArchivesCalloutEntryGridStore',
            columns:[
                {text: 'id', dataIndex: 'id', width:150, menuDisabled: true,hidden:true},
                {text: '批次号', dataIndex: 'batchcode', width:150, menuDisabled: true},
                {text: '档号', dataIndex: 'archivecode', width:150, menuDisabled: true},
                {text: '工作状态', dataIndex: 'workstate', width:150, menuDisabled: true},
                {text: '借出状态', dataIndex: 'lendstate', width:150, menuDisabled: true},
                {text: '检查状态', dataIndex: 'checkstate', width:150, menuDisabled: true},
                {text: '扫描状态', dataIndex: 'scanstate', width:150, menuDisabled: true},
                {text: '图片状态', dataIndex: 'picturestate', width:150, menuDisabled: true},
                {text: '业务签收人', dataIndex: 'businesssigner', width:150, menuDisabled: true},
                {text: '业务签收工号', dataIndex: 'businesssigncode', width:150, menuDisabled: true},
                {text: '签收时间', dataIndex: 'signtime', width:150, menuDisabled: true},
                {text: '实体签收人', dataIndex: 'entrysigner', width:150, menuDisabled: true},
                {text: '实体签收代号', dataIndex: 'entrysigncode', width:150, menuDisabled: true},
                {text: '实体签收时间', dataIndex: 'entrysigntime', width:150, menuDisabled: true},
                {text: '实体签收单位', dataIndex: 'entrysignorgan', width:150, menuDisabled: true},
                {text: '实体归还人', dataIndex: 'returnman', width:150, menuDisabled: true},
                {text: '实体归还人代号', dataIndex: 'returnloginname', width:150, menuDisabled: true},
                {text: '实体归还时间', dataIndex: 'returntime', width:150, menuDisabled: true},
                {text: 'A0页数', dataIndex: 'a0', width:150, menuDisabled: true},
                {text: 'A1页数', dataIndex: 'a1', width:150, menuDisabled: true},
                {text: 'A2页数', dataIndex: 'a2', width:150, menuDisabled: true},
                {text: 'A3页数', dataIndex: 'a3', width:150, menuDisabled: true},
                {text: 'A4页数', dataIndex: 'a4', width:150, menuDisabled: true},
                {text: '折算A4页数', dataIndex: 'za4', width:150, menuDisabled: true}
            ],
            tbar:{
                overflowHandler:'scroller',
                items:[{
                    text:'增加',
                    iconCls:'fa fa-plus-circle',
                    itemId:'add'
                },'-',{
                    text:'修改',
                    iconCls:'fa fa-pencil-square-o',
                    itemId:'edit'
                },'-',{
                    text:'删除',
                    iconCls:'fa fa-trash-o',
                    itemId:'del'
                },'-',{
                    text:'归还',
                    iconCls:'fa fa-undo',
                    itemId:'returnback'
                },'-',{
                    itemId: 'importmenu',
                    text: '批量添加',
                    iconCls: '',
                    menu: [{
                        text:'导入Excel',
                        iconCls:'fa fa-level-down',
                        itemId:'import',
                        menu: null
                    },{
                        text:'下载Excel模板',
                        iconCls:'fa fa-download',
                        itemId:'dowmLoad',
                        menu: null
                    }]
                }]
            }
        }]
    }]
});