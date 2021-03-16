/**
 * Created by RonJiang on 2018/4/18 0018.
 */
Ext.define('Acquisition.view.transfor.AcquisitionShowTransdocGridView', {
    extend: 'Comps.view.BasicGridView',
    xtype:'acquisitionShowTransdocGridView',
    hasCloseButton:false,
    searchstore: auditOpened == 'true' ? [
        {item: "transdesc", name: "移交说明"},
        {item: "transorgan", name: "移交部门"},
        {item: "transdate", name: "移交日期"},
        {item: "transcount", name: "移交数量"},
        {item: "state", name: "移交状态"},
        {item: "sendbackreason", name: "退回原因"}
    ]:[
        {item: "transdesc", name: "移交说明"},
        {item: "transorgan", name: "移交部门"},
        {item: "transdate", name: "移交日期"},
        {item: "transcount", name: "移交数量"}
    ],
    tbar: [{
        itemId:'showEntryDetail',
        xtype: 'button',
        text: '详细内容'
    }, '-',{
        itemId:'print',
        xtype: 'button',
        text: '打印'
    }, '-',{
        itemId:'reTransfor',
        xtype: 'button',
        text: '重新移交'
    }/*,'-',{
        xtype: 'button',
        text: '签章',
        itemId:'signId'
    },'-',{
        xtype: 'button',
        text: '验证签章',
        itemId:'signVerify'
    }*/,{
        xtype: 'button',
        itemId: 'urging',
        text: '催办',
        iconCls:'fa fa-print',
        hidden :true
    },{
        xtype: "checkboxfield",
        boxLabel : '发送短信',
        itemId:'message',
        checked:true,
        hidden :true
    }
    // ,  '-',{
    //     itemId:'deleteTransfor',
    //     xtype: 'button',
    //     text: '删除移交单据'
    // }
    ,  '-',{
        itemId:'back',
        xtype: 'button',
        text: '返回'
    },'-',{
            text:'查看退回原因',
            iconCls:'fa fa-reply-all',
            itemId:'lookBack'
        }],
    store: 'TransdocGridStore',
    columns: [
        {text: '移交说明', dataIndex: 'transdesc', flex: 2, menuDisabled: true},
        {text: '移交人', dataIndex: 'transuser', flex: 2, menuDisabled: true},
        {text: '移交部门', dataIndex: 'transorgan', flex: 2, menuDisabled: true},
        {text: '移交日期', dataIndex: 'transdate', flex: 3, menuDisabled: true},
        {text: '移交数量', dataIndex: 'transcount', flex: 2, menuDisabled: true},
        {text: '移交状态', dataIndex: 'state', flex: 2, menuDisabled: true},
        {text: '移交签章', dataIndex: 'transforcasign', flex: 2, menuDisabled: true},
        {text: '审核签章', dataIndex: 'editcasign', flex: 2, menuDisabled: true},
        {text: '退回原因', dataIndex: 'sendbackreason', flex: 3, menuDisabled: true}
    ]
});