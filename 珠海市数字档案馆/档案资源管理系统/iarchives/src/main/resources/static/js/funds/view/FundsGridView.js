/**
 * Created by RonJiang on 2018/04/08
 */
Ext.define('Funds.view.FundsGridView', {
    extend:'Comps.view.BasicGridView',
    xtype:'fundsgrid',
    itemId:'fundsGridID',
    store:'FundsGridStore',
    searchstore:[
        {item: 'fundsname', name: '全宗名称'},
        {item: 'fundsnameformername', name: '全宗名称曾用名'},
        {item: 'fundsstarttime', name: '全宗起始时间'},
        {item: 'fundsendtime', name: '全宗终止时间'},
        {item: 'funds', name: '全宗号'},
        {item: 'organname', name: '机构名称'},
        {item: 'fundsguidedoc', name: '全宗指南文件'}
    ],
    tbar: [{
        itemId:'save',
        xtype: 'button',
        iconCls:'fa fa-plus-circle',
        text: '同步机构'
    },'-',{
        itemId:'modify',
        xtype: 'button',
        iconCls:'fa fa-pencil-square-o',
        text: '修改'
    },'-',{
    	itemId:'del',
        xtype: 'button',
        iconCls:'fa fa-trash-o',
        text: '删除'
    },{
        itemId:'look',
        xtype: 'button',
        iconCls:'fa fa-eye',
        text: '查看'
    },'-',{
    	itemId:'summary',
        xtype: 'button',
        iconCls:'fa fa-area-chart',
        text: '汇总'
    },'-',{
        itemId:'print',
        xtype: 'button',
        iconCls:'fa fa-print',
        text: '打印全宗目录'
    },'-',{
    	itemId:'printfunds',
        xtype: 'button',
        iconCls:'fa fa-print',
        text: '打印全宗信息'
    }],
    columns: [
        {text: '全宗名称', dataIndex: 'fundsname', flex: 2, menuDisabled: true},
        {text: '全宗名称曾用名', dataIndex: 'fundsnameformername', flex: 2, menuDisabled: true},
        {text: '全宗起始时间', dataIndex: 'fundsstarttime', flex: 2, menuDisabled: true},
        {text: '全宗终止时间', dataIndex: 'fundsendtime', flex: 2, menuDisabled: true},
        {text: '全宗号', dataIndex: 'funds', flex: 2, menuDisabled: true},
        {text: '机构名称', dataIndex: 'organname', flex: 2, menuDisabled: true},
        {
        	text: '全宗指南文件',
        	dataIndex: 'fundsguidedoc',
        	flex: 2,
        	menuDisabled: true,
        	renderer: function (value) {
        		var reg = /(http|ftp|https):\/\/[\w\-_]+(\.[\w\-_]+)+([\w\-\.,@?^=%&:/~\+#]*[\w\-\@?^=%&/~\+#])?/;
        		var r = value.match(reg);
        		if (r) {
        			return "<a href='"+value+"' target='_blank'>"+value+"</a>";
        		}
        		return value;
        	}
        }
    ]
});