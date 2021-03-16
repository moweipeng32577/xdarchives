/**
 * Created by Administrator on 2020/6/17.
 */


Ext.define('JyAdmins.view.LookBorrowDetailFormView',{
    extend: 'Ext.form.Panel',
    xtype: 'lookBorrowDetailFormView',
    itemId:'lookBorrowDetailFormViewId',
    region: 'north',
    height:'50%',
    autoScroll: true,
    fieldDefaults: {
        labelWidth: 70
    },
    layout:'column',
    bodyPadding: 15,
    items:[{
        xtype: 'textfield',
        name:'id',
        hidden:true
    },{
        columnWidth: .47,
        xtype: 'textfield',
        fieldLabel: '申办人',
        labelWidth: 85,
        name:'borrowman',
        margin:'10 0 0 0',
        readOnly:true
    },{
        columnWidth:.06,
        xtype:'displayfield'
    },{
        columnWidth: .47,
        xtype: 'textfield',
        fieldLabel: '查档内容',
        labelWidth: 85,
        name:'borrowcontent',
        margin:'10 0 0 0',
        readOnly:true
    },{
        columnWidth: .23,
        xtype: 'textfield',
        fieldLabel: '提供证件',
        labelWidth: 85,
        name:'certificatetype',
        readOnly:true,
        margin:'10 0 0 0'
    },{
        columnWidth: .01,
        xtype: 'displayfield'
    },{
        columnWidth: .23,
        fieldLabel: '证件号码',
        xtype: 'textfield',
        name: 'certificatenumber',
        labelWidth: 85,
        readOnly:true,
        margin: '10 0 0 0'
    },{
        columnWidth: .06,
        xtype: 'displayfield'
    },{
        columnWidth: .47,
        xtype: 'textfield',
        itemId: 'borrowmdId',
        name: 'borrowmd',
        fieldLabel: '目的',
        labelWidth: 85,
        readOnly:true,
        margin: '10 0 0 0'
    },{
        columnWidth: .47,
        fieldLabel: '地址',
        xtype: 'textfield',
        name: 'comaddress',
        labelWidth: 85,
        readOnly:true,
        margin: '10 0 0 0'
    }, {
        columnWidth:.06,
        xtype:'displayfield'
    },{
        columnWidth: .47,
        fieldLabel: '电话或者电子邮箱',
        xtype: 'textfield',
        name: 'borrowmantel',
        labelWidth: 85,
        readOnly:true,
        margin: '10 0 0 0'
    },{
        columnWidth: .23,
        fieldLabel: '与当事人的关系',
        xtype: 'textfield',
        name: 'relationship',
        labelWidth: 85,
        readOnly:true,
        margin: '-7 0 0 0'
    },{
        columnWidth:.01,
        xtype:'displayfield'
    },{
        columnWidth: .23,
        fieldLabel: '查档(接收)单位',
        xtype: 'textfield',
        name: 'borroworgan',
        labelWidth: 85,
        readOnly:true,
        margin: '-7 0 0 0'
    }, {
        columnWidth: .06,
        xtype: 'displayfield'
    },{
        columnWidth: .47,
        fieldLabel: '利用方式',
        xtype: 'textfield',
        name: 'lymode',
        labelWidth: 85,
        readOnly:true,
        margin: '10 0 0 0'
    },{
        columnWidth: .47,
        fieldLabel: '申请时间',
        xtype: 'textfield',
        name: 'borrowdate',
        labelWidth: 85,
        readOnly:true,
        margin:'10 0 0 0'
    }, {
        columnWidth: .06,
        xtype: 'displayfield'
    },{
        columnWidth: .23,
        xtype: 'textfield',
        itemId:'borrowtytsId',
        fieldLabel: '同意查档天数',
        labelWidth: 85,
        name: 'borrowtyts',
        allowBlank: false,
        margin: '10 0 0 0'
    },{
        columnWidth:.01,
        xtype:'displayfield'
    },{
        columnWidth: .23,
        xtype: 'textfield',
        itemId:'borrowtsId',
        fieldLabel: '查档天数',
        labelWidth: 85,
        name: 'borrowts',
        allowBlank: false,
        margin: '10 0 0 0'
    },{
        columnWidth: .47,
        xtype: 'textfield',
        itemId:'borrowmantimeld',
        fieldLabel: '利用人数',
        labelWidth: 85,
        name: 'borrowmantime',
        readOnly:true,
        margin: '10 0 0 0'
    },{
        columnWidth: .06,
        itemId:'notext',
        xtype: 'displayfield'
    },{
        columnWidth: .32,
        xtype: 'textfield',
        itemId:'media',
        fieldLabel: '附件',
        labelWidth: 85,
        readOnly:true,
        name: 'evidencetext',
        margin: '10 0 0 0'
    },{
        columnWidth: .08,
        style : 'text-align:center;',
        margin: '17 0 0 0',
        items: [{
            xtype: 'label',
            itemId:'mediacount',
            text: '共0份',
            listeners:{
                render:function (view) {
                    var lookBorrowDetailFormView = view.findParentByType('lookBorrowDetailFormView');
                    Ext.Ajax.request({
                        url: '/electronApprove/getEvidencetextCount',
                        params:{
                            borrowcode:lookBorrowDetailFormView.borrowcode
                        },
                        success: function (response) {
                            var text = Ext.decode(response.responseText).data;
                            view.setText('共'+text+'份');
                        }
                    });
                }
            }
        }]
    },{
        columnWidth: .07,
        margin: '10 0 0 0',
        items: [{
            xtype: 'button',
            itemId:'electronId',
            text: '查看'
        }]
    },{
        layout : 'column',
        xtype:'fieldset',
        style:'background:#fff;padding-top:0px',
        columnWidth:1,
        title: '其它字段',
        collapsible: true,
        collapsed:true,
        autoScroll: true,
        items:[{
            columnWidth: 0.47,
            xtype: 'textfield',
            fieldLabel: '复制内容',
            labelWidth: 85,
            name: 'copycontent',
            margin: '5 0 0 0'
        },{
            columnWidth:.06,
            xtype:'displayfield'
        },{
            columnWidth: 0.47,
            xtype: 'textfield',
            fieldLabel: '复制目的',
            labelWidth: 85,
            name: 'copymd',
            margin: '5 0 0 0'
        }]
    },{
        columnWidth: 1,
        xtype: 'textarea',
        fieldLabel: '备注',
        labelWidth: 85,
        name:'desci',
        margin: '5 0 0 0',
        height:30,//文本框默认高度为30
        readOnly:true
    },{
        columnWidth: 1,
        itemId:'approveId',
        xtype: 'textarea',
        fieldLabel: '批示',
        labelWidth: 85,
        name:'approve',
        flex: 1,
        margin: '5 0 0 0',
        readOnly:true
    }]
});
