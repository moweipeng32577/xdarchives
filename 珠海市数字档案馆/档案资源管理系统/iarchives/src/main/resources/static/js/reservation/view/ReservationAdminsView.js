
Ext.define('Reservation.view.ReservationAdminsView', {
    extend: 'Comps.view.BasicGridView',
    xtype: 'reservationAdminsView',
    region: 'center',
    allowDrag:true,
    searchstore:[
        {item: "yystate", name: "状态"},
        {item: "replier", name: "回复者"},
        {item: "replytime", name: "回复时间"},
        {item: "borrowman", name: "预约者"},
        {item: "borrowcontent", name: "查档内容"},
        {item: "borrowmantel", name: "联系电话"},
        {item: "yytime", name: "预约时间"},
        {item: "borroworgan", name: "工作单位"}
    ],
    tbar:[{
        itemId:'cdReservate',
        xtype:'button',
        iconCls:'fa fa-bars',
        text:'查档预约',
        hidden :true
    },'-',{
        itemId:'exhibition',
        xtype:'button',
        iconCls:'fa fa-eye',
        text:'展厅参观',
        hidden :true
    }, '-',{
        itemId:'hfReservate',
        xtype:'button',
        text:'预约回复',
        hidden :true
    }, '-',{
        itemId:'qxReservate',
        xtype:'button',
        text:'取消预约',
        hidden :true
    }, '-',{
        itemId:'lookReservate',
        xtype:'button',
        text:'查看预约详情',
        hidden :true
    }, '-',{
        itemId:'printId',
        iconCls:'fa fa-print',
        xtype:'button',
        text:'打印',
        hidden :true
    },'-',{
        itemId:'excel',
        iconCls: 'fa fa-download',
        xtype:'button',
        text:'导出Excel',
        hidden :true
    }],
    store: 'ReservationAdminsStore',
    columns: [
        {text: '查档内容', dataIndex: 'borrowcontent', flex: 2, menuDisabled: true},
        {text: '预约者', dataIndex: 'borrowman', flex: 2, menuDisabled: true},//查档人
        {text: '预约时间', dataIndex: 'yytime', flex: 2, menuDisabled: true},
        {text: '工作单位', dataIndex: 'borroworgan', flex: 2, menuDisabled: true},
        {text: '联系电话', dataIndex: 'borrowmantel', flex: 2, menuDisabled: true},
        {text: '证件类型', dataIndex: 'certificatestype', flex: 2, menuDisabled: true},
        {text: '证件号', dataIndex: 'certificatenumber', flex: 2, menuDisabled: true},
        {text: '登记时间', dataIndex: 'djtime', flex: 2, menuDisabled: true},
        {text: '预约状态', dataIndex: 'yystate', flex: 2, menuDisabled: true},
        {text: '回复者', dataIndex: 'replier', flex: 2, menuDisabled: true},
        {text: '回复时间', dataIndex: 'replytime', flex: 2, menuDisabled: true},
        {text: '回复内容', dataIndex: 'replycontent', flex: 2, menuDisabled: true},
    ]
});